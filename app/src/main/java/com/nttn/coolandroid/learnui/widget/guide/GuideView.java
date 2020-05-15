package com.nttn.coolandroid.learnui.widget.guide;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.nttn.coolandroid.tool.DisplayUtil;
import com.nttn.coolandroid.tool.LogUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Apple.
 * Desc: 用户引导蒙层
 */
public class GuideView extends View {
    private static final String TAG = "GuideView>>>";
    private Paint mPaint;
    private int mCurtainColor = 0xAA000000;
    private Hollow[] mHollows;
    private Map<Hollow, Hollow> mCacheHollows;

    public GuideView(@NonNull Context context) {
        super(context);
        init();
    }

    public GuideView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GuideView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setCurtainColor(int color) {
        mCurtainColor = color;
        postInvalidate();
    }

    public void setHollows(@NonNull Hollow... hollows) {
        mHollows = hollows;
        postInvalidate();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCacheHollows = new ArrayMap<>(4);
        } else {
            mCacheHollows = new HashMap<>();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //全屏
        DisplayMetrics screenMetrics = DisplayUtil.getScreenMetrics(getContext());
        int widthPixels = screenMetrics != null ? screenMetrics.widthPixels : getWidth();
        int heightPixels = screenMetrics != null ? screenMetrics.heightPixels : getHeight();
        setMeasuredDimension(widthPixels, heightPixels);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //保存现场
        int saveCount;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null);
        } else {
            saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        }

        //绘制蒙层和高亮部分
        drawTransparent(canvas);
        drawHollows(canvas);

        //恢复现场
        canvas.restoreToCount(saveCount);
    }

    /**
     * 画半透明蒙层
     *
     * @param canvas 画布
     */
    private void drawTransparent(Canvas canvas) {
        mPaint.setColor(mCurtainColor);
        mPaint.setXfermode(null);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
    }

    /**
     * 画高亮部分
     *
     * @param canvas 画布
     */
    private void drawHollows(Canvas canvas) {
        if (mHollows.length <= 0) {
            LogUtil.w(TAG, "without target to highlight");
            return;
        }
        mPaint.setColor(Color.WHITE);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        for (Hollow hollow : mHollows) {
            drawHollow(canvas, hollow);
        }
    }

    private void drawHollow(Canvas canvas, Hollow hollow) {
        Hollow cacheHollow = mCacheHollows.get(hollow);
        if (null != cacheHollow) {
            realDrawHollow(canvas, cacheHollow);
            return;
        }

        int[] targetLocation = new int[2];
        hollow.targetView.getLocationOnScreen(targetLocation);
        hollow.targetRect.left = targetLocation[0];
        hollow.targetRect.top = targetLocation[1];
        hollow.targetRect.right += hollow.targetRect.left;
        hollow.targetRect.bottom += hollow.targetRect.top;

        int statusBarHeight = DisplayUtil.getStatusBarHeight(getContext());
        hollow.targetRect.top -= statusBarHeight;
        hollow.targetRect.bottom -= statusBarHeight;

        if (hollow.padding != null){
            if (hollow.padding.padding > 0) {
                hollow.targetRect.left -= hollow.padding.padding;
                hollow.targetRect.top -= hollow.padding.padding;
                hollow.targetRect.right += hollow.padding.padding;
                hollow.targetRect.bottom += hollow.padding.padding;
            }else {
                hollow.targetRect.left -= hollow.padding.paddingLeft;
                hollow.targetRect.top -= hollow.padding.paddingTop;
                hollow.targetRect.right += hollow.padding.paddingRight;
                hollow.targetRect.bottom += hollow.padding.paddingBottom;
            }
        }

        if (hollow.offset != null) {
            if (Math.abs(hollow.offset.offsetX) > 0) {
                hollow.targetRect.left += hollow.offset.offsetX;
                hollow.targetRect.right += hollow.offset.offsetX;
            }
            if (Math.abs(hollow.offset.offsetY) > 0) {
                hollow.targetRect.top += hollow.offset.offsetY;
                hollow.targetRect.bottom += hollow.offset.offsetY;
            }
        }

        mCacheHollows.put(hollow, hollow);
        realDrawHollow(canvas, hollow);
    }

    private void realDrawHollow(Canvas canvas, Hollow hollow) {
        if (!drawHollowIfMatched(canvas, hollow)) {
            canvas.drawRect(hollow.targetRect, mPaint);
        }
    }

    private boolean drawHollowIfMatched(Canvas canvas, Hollow hollow) {
        //draw custom shape of user
        if (hollow.shape != null) {
            hollow.shape.drawShape(canvas, mPaint, hollow);
            return true;
        }
        //is not auto adapt view background, draw a rect according to targetView
        if (!hollow.isAutoAdaptViewBackground()) {
            return false;
        }
        //draw shape background
        Drawable background = hollow.targetView.getBackground();
        if (background instanceof GradientDrawable) {
            drawGradientHollow(canvas, hollow, background);
            return true;
        }
        //draw selector background
        if (background instanceof StateListDrawable) {
            if (background.getCurrent() instanceof GradientDrawable) {
                drawGradientHollow(canvas, hollow, background.getCurrent());
                return true;
            }
        }
        return false;
    }

    private void drawGradientHollow(Canvas canvas, Hollow hollow, Drawable background) {
        //通过反射获取shape属性
        int shape = GradientDrawable.RECTANGLE;
        float aRadius = 0;
        Field gradientStateField;
        Object aGradientState;
        try {
            gradientStateField = Class.forName("android.graphics.drawable.GradientDrawable")
                    .getDeclaredField("mGradientState");
            gradientStateField.setAccessible(true);
            aGradientState = gradientStateField.get(background);
            Field shapeField = aGradientState.getClass().getDeclaredField("mShape");
            shapeField.setAccessible(true);
            shape = (int) shapeField.get(aGradientState);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                aRadius = ((GradientDrawable) background).getCornerRadius();
            } else {
                Field mRadius = aGradientState.getClass().getDeclaredField("mRadius");
                mRadius.setAccessible(true);
                aRadius = (float) mRadius.get(background);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (GradientDrawable.OVAL == shape) {
            canvas.drawOval(new RectF(
                    hollow.targetRect.left, hollow.targetRect.top,
                    hollow.targetRect.right, hollow.targetRect.bottom), mPaint);
        } else {
            float r = Math.min(aRadius,
                    Math.min(hollow.targetRect.width(), hollow.targetRect.height()) * 0.5f);
            canvas.drawRoundRect(new RectF(hollow.targetRect.left, hollow.targetRect.top,
                    hollow.targetRect.right, hollow.targetRect.bottom), r, r, mPaint);
        }
    }
}
