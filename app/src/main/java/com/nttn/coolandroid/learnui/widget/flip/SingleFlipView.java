package com.nttn.coolandroid.learnui.widget.flip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.nttn.coolandroid.R;
import com.nttn.coolandroid.tool.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 实验翻页效果    <br>
 * Version: 1.0                 <br>
 * Update：2020/11/7              <br>
 * Created by Apple.                <br>
 * Ref：https://juejin.im/post/6873653741627637774#heading-0     <br>
 * Animation：https://wiki.jikexueyuan.com/project/android-animation/2.html  <br>
 * <p>
 * Attrs: {@link R.styleable#SingleFlipView}
 */
public class SingleFlipView extends View {
    private static final String TAG = "DemoFlipView";

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int paintColor = Color.parseColor("#88000000");
    private final Path mPath = new Path();

    private final Paint mArrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mArrowPath = new Path();
    private int arrowColor = Color.WHITE;
    private int arrowStrokeWidth = 3;

    private int center_x = 540, center_y = 960;

    /* 绘制点及移动方向管理 */
    private CubicPointsManager mPointsManager;

    private int defaultDragWidth = 30;// 默认箭头的宽度
    private float dragWidth;//箭头动态宽度
    private float animDragWidth;//用于动画过渡
    private float offsetScale = 0.5f;

    private ValueAnimator upAnimator;//松手回弹动画
    private Interpolator interpolator = new LinearInterpolator();//默认松手回弹动画
    private final int defaultDuration = 800;
    private int duration = defaultDuration;

    private final List<Interpolator> interpolatorList = new ArrayList<>(4);

    {
        interpolatorList.add(new LinearInterpolator());
        interpolatorList.add(new DecelerateInterpolator());
        interpolatorList.add(new BounceInterpolator());
        interpolatorList.add(new AccelerateInterpolator());
    }

    public SingleFlipView(Context context) {
        this(context, null);
    }

    public SingleFlipView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleFlipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        initAttrs(context, attrs);

        initPaintAndPath();

        //屏幕中心位置
        initCenterPoint(context);

        //松手，动画回弹
        initUpAnimator();

        mPointsManager = new CubicPointsManager(mPath, mArrowPath, center_x, center_y, defaultDragWidth);
    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SingleFlipView);

        paintColor = typedArray.getColor(R.styleable.SingleFlipView_flip_color, paintColor);

        arrowColor = typedArray.getColor(R.styleable.SingleFlipView_flip_arrow_color, arrowColor);
        arrowStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.SingleFlipView_flip_arrow_stroke_width, arrowStrokeWidth);
        defaultDragWidth = typedArray.getDimensionPixelSize(R.styleable.SingleFlipView_flip_arrow_width, defaultDragWidth);

        offsetScale = typedArray.getFloat(R.styleable.SingleFlipView_flip_offset_scale, offsetScale);

        duration = typedArray.getInt(R.styleable.SingleFlipView_flip_duration, defaultDuration);
        int animType = typedArray.getInt(R.styleable.SingleFlipView_flip_anim_type, 0);
        interpolator = interpolatorList.get(animType);

        typedArray.recycle();
    }

    private void initPaintAndPath() {
        mPaint.setColor(paintColor);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mArrowPaint.setColor(arrowColor);
        mArrowPaint.setStrokeWidth(arrowStrokeWidth);
        mArrowPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * 初始化屏幕中心点
     *
     * @param context 上下文环境
     */
    private void initCenterPoint(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return;
        DisplayMetrics screenMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(screenMetrics);

        center_x = screenMetrics.widthPixels / 2;
        center_y = screenMetrics.heightPixels / 2;

        LogUtil.d(TAG, "init center >>> (" + center_x + "," + center_y + ")");
    }

    /**
     * 松手后，曲线恢复动画
     */
    private void initUpAnimator() {
        upAnimator = ValueAnimator.ofFloat(1f, 0f);
        upAnimator.setInterpolator(interpolator);
        upAnimator.setDuration(duration);
        upAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                LogUtil.d(TAG, "animation cancel");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                LogUtil.d(TAG, "animation end");
                if (flipperListener != null) {
                    flipperListener.onFlip(mPointsManager.isTowardLeft());
                }
            }
        });
        upAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 动画过渡，根据最终拖动距离和动画渐变比，计算变化值
                float value = (float) animation.getAnimatedValue();
                dragWidth = animDragWidth * value;

                mPointsManager.updatePointLocation(dragWidth);
                invalidate();
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //可见中心
        center_x = w / 2;
        center_y = h / 2;

        LogUtil.d(TAG, "sizeChanged center >>> (" + center_x + "," + center_y + ")");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制各点
        drawFlipWave(canvas);
    }

    /**
     * 判断移动方向及偏移距离
     *
     * @param event 手势
     * @return 拦截touch事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float current_x = event.getX();
        float current_y = event.getY();

        // 记录手指移动的位置
        mPointsManager.updatePointFinger(current_x, current_y);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (upAnimator.isRunning()) upAnimator.cancel();

                // down事件记录下按下初始位置，后续判断偏移距离和方向
                mPointsManager.updatePointOrigin(current_x, current_y);
                break;
            case MotionEvent.ACTION_MOVE:
                updateOnMove();

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                // 动画过渡，最终拖动距离备份
                animDragWidth = dragWidth;
                upAnimator.start();
                break;
        }
        return true;
    }

    private void updateOnMove() {
        // x move offset
        float offsetX = Math.abs(mPointsManager.getXOffset());

        if (offsetX >= center_x) offsetX = center_x;

        dragWidth = offsetX * offsetScale;

        mPointsManager.updateOnMove(dragWidth);
    }

    /**
     * 绘制移动之后的各点
     *
     * @param canvas 画布
     */
    private void drawFlipWave(Canvas canvas) {
        mPointsManager.drawCubic();

        //移动一段距离后(3 * defaultDragWidth)，显示箭头
        mPointsManager.drawArrow();

        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(mArrowPath, mArrowPaint);

        //绘制完当前路径，删除之前的路径
        mPath.reset();
        mArrowPath.reset();
    }

    public interface FlipperListener {
        void onFlip(boolean towardLeft);
    }

    private FlipperListener flipperListener;
    public void setFlipperListener(FlipperListener flipperListener) {
        this.flipperListener = flipperListener;
    }
}
