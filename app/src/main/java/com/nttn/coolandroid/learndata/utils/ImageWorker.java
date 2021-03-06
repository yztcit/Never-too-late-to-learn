package com.nttn.coolandroid.learndata.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * 手机本地图片异步加载处理类
 * 图片的加载性能影响很大，使用弱引用和软引用
 * 缓存图片，加快响应的速度，提高性能。
 */

public class ImageWorker {
    //这个值设置的是加载图片的动画效果的间隔时间，达到渐隐渐显的效果
    private static final int FADE_IN_TIME = 200;

    private boolean mExitTasksEarly = false;//判断图片加载任务是否提前退出
    private boolean mPauseWork = false;//加载图片线程是否挂起
    private final Object mPauseWorkLock = new Object();//锁对象，这个锁对象是为了判断是否进行图片的加载

    private final Resources mResources;
    private final ContentResolver mContentResolver;//内容解析者
    private final BitmapFactory.Options mOptions;

//    private final HashMap<Long, SoftReference<BitmapDrawable>> bitmapCache = new HashMap<Long, SoftReference<BitmapDrawable>>();//用于缓存图片，每一个缓存的图片对应一个Long类型的id值，SoftReference对应该图片的软引用
    //图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
    private LruCache<Long, BitmapDrawable> mMemoryCache;

    private Bitmap mLoadBitmap;//GridView中默认的背景图片
    //构造器
    public ImageWorker(Context context) {
        this.mResources = context.getResources();
        this.mContentResolver = context.getContentResolver();
        mOptions = new BitmapFactory.Options();
        mOptions.inSampleSize = 3;//缩放图片为原来的1/9。一般应用中加载图片都会进行图片的缩放，防止内存溢出的问题。这部分的内容有关Bitmap，请参考我的博文(http://blog.csdn.net/u010156024/article/details/44103557)

        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<Long,BitmapDrawable>(cacheSize){

            @Override
            protected int sizeOf(Long key, BitmapDrawable value) {
                return value.getBitmap().getByteCount();
            }
        };
    }

    /**
     * 加载图片
     * @param origId 每个本地图片对应一个id值
     * @param imageView
     */
    public void loadImage(long origId, ImageView imageView) {
        BitmapDrawable bitmapDrawable = null;
        //先从缓存中加载图片，如果缓存中有，加载图片即可。
        //如果缓存中没有，首先判断当前任务是否暂停，没有暂停则使用loadBitmapTask异步任务线程加载图片
//        if (bitmapCache.containsKey(origId)) {
//            bitmapDrawable = bitmapCache.get(origId).get();
//        }
        if (getBitmapFromMemoryCache(origId) != null) {
            bitmapDrawable = mMemoryCache.get(origId);
        }
        if (bitmapDrawable != null) {
            imageView.setImageDrawable(bitmapDrawable);
        } else if (cancelPotentialWork(origId, imageView)) {
            final LoadBitmapTask loadBitmapTask = new LoadBitmapTask(imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(mResources, mLoadBitmap, loadBitmapTask);
            imageView.setImageDrawable(asyncDrawable);
            //SERIAL_EXECUTOR 启动线程，保证线程顺序依次执行
            loadBitmapTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, origId);
        }
    }

    /**
     * 该类提供这个方法设置GridView中每个item默认的图片
     */
    public void setLoadBitmap(Bitmap mLoadBitmap) {
        this.mLoadBitmap = mLoadBitmap;
    }

    /**
     * 设置图片动画  加载图片渐隐渐显的效果
     *
     * @param imageView
     * @param drawable
     */
    private void setImageDrawable(ImageView imageView, Drawable drawable) {
        final TransitionDrawable td =
                new TransitionDrawable(new Drawable[]{
//                        new ColorDrawable(android.R.color.transparent),
                        new ColorDrawable(android.R.color.transparent),
                        drawable
                });
        imageView.setImageDrawable(td);
        td.startTransition(FADE_IN_TIME);
    }

    /**
     * 取消可能在运行并且暂停的任务。
     *
     * @param origId
     * @param imageView
     * @return
     */
    private static boolean cancelPotentialWork(long origId, ImageView imageView) {
        final LoadBitmapTask loadBitmapTask = getBitmapWorkerTask(imageView);

        if (loadBitmapTask != null) {
            final long bitmapOrigId = loadBitmapTask.origId;
            if (bitmapOrigId == origId) {
                loadBitmapTask.cancel(true);
            } else {
                // The same work is already in progress.
                return false;
            }
        }
        return true;
    }

    /**
     * 将一张图片存储到LruCache中。
     *
     * @param key
     *            LruCache的键，这里传入图片的URL地址。
     * @param drawable
     *            LruCache的值，这里传入从网络上下载的BitmapDrawable对象。
     */
    private void addBitmapToMemoryCache(long key, BitmapDrawable drawable) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, drawable);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     *
     * @param key
     *            LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的BitmapDrawable对象，或者null。
     */
    private BitmapDrawable getBitmapFromMemoryCache(long key) {
        return mMemoryCache.get(key);
    }

    /**
     * 图片异步加载线程类-任务线程
     */
    private class LoadBitmapTask extends AsyncTask<Long, Void, BitmapDrawable> {
        private long origId;
        private WeakReference<ImageView> imageViewReference;//指向Imageview的弱引用，把图片缓存在HashMap<Long, SoftReference<BitmapDrawable>> bitmapCache中。

        LoadBitmapTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected BitmapDrawable doInBackground(Long... params) {
            origId = params[0];
            Bitmap bitmap = null;
            BitmapDrawable drawable = null;

            // Wait here if work is paused and the task is not cancelled
            synchronized (mPauseWorkLock) {
                while (mPauseWork && !isCancelled()) {
                    try {
                        mPauseWorkLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (!isCancelled() && getAttachedImageView() != null && !mExitTasksEarly) {
                //这里是根据图片的id值查询手机本地的图片，获取图片的缩略图，MICRO_KIND 代表96*96大小的图片
                bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                        mContentResolver,
                        origId,
                        MediaStore.Images.Thumbnails.MICRO_KIND,
                        mOptions
                );
            }

            if (bitmap != null) {
                drawable = new BitmapDrawable(mResources, bitmap);
//                bitmapCache.put(origId,new SoftReference<BitmapDrawable>(drawable));
                addBitmapToMemoryCache(origId,drawable);
            }
            return drawable;
        }

        @Override
        protected void onPostExecute(BitmapDrawable drawable) {
            if (isCancelled() || mExitTasksEarly) {
                drawable = null;
            }

            final ImageView imageView = getAttachedImageView();
            if (drawable != null && imageView != null) {
                setImageDrawable(imageView, drawable);
            }
        }

        @Override
        protected void onCancelled(BitmapDrawable drawable) {
            super.onCancelled(drawable);
            synchronized (mPauseWorkLock) {
                mPauseWorkLock.notifyAll();
            }
        }

        /**
         * 返回与此任务相关的ImageView，
         * 如果ImageView 内的任务是当前任务，
         * 则返回当前ImageView,否则返回null。
         * @return
         */
        private ImageView getAttachedImageView() {
            final ImageView imageView = imageViewReference.get();
            final LoadBitmapTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask) {
                return imageView;
            }
            return null;
        }
    }

    /**
     * 存储异步信息图片资源类
     */
    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<LoadBitmapTask> bitmapWorkerTaskReference;//虚引用
        AsyncDrawable(Resources res, Bitmap bitmap, LoadBitmapTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<LoadBitmapTask>(bitmapWorkerTask);
        }
        LoadBitmapTask getLoadBitmapTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    /**
     * 返回图片资源内存放的异步线程，如果存在，则返回，不存在，返回null。
     *
     * @param imageView 当前存放异步资源图片的ImageView
     * @return LoadBitmapTask 图片异步加载线程类
     */
    private static LoadBitmapTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getLoadBitmapTask();
            }
        }
        return null;
    }

    /**
     * 设置异步任务是否暂停，false为启动，true为暂停。
     * @param pauseWork pause flag
     */
    public void setPauseWork(boolean pauseWork) {
        synchronized (mPauseWorkLock) {
            mPauseWork = pauseWork;
            if (!mPauseWork) {
                mPauseWorkLock.notifyAll();
            }
        }
    }

    /**
     * 退出线程
     * @param exitTasksEarly exit flag
     */
    public void setExitTasksEarly(boolean exitTasksEarly) {
        mExitTasksEarly = exitTasksEarly;
        setPauseWork(false);//这个设置为false，使得退出任务优雅。这个设置为true也是可行的，也没有问题，可以达到同样的效果。但是可以比较设置为true或false的区别。
    }
}
