package com.nttn.coolandroid.learnui.widget.flip;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
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

    private static final int LEFT = -1;
    private static final int RIGHT = 1;
    private int direction = 0;

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int paintColor = Color.parseColor("#88000000");
    private final Path mPath = new Path();

    private final Paint mArrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mArrowPath = new Path();
    private int arrowColor = Color.WHITE;
    private int arrowStrokeWidth = 3;

    private int center_x = 540, center_y = 960;

    /**
     * 贝赛尔曲线各点
     */
    private final PointF pointOrigin = new PointF(0, 0);//起始手指按下的点
    private final PointF pointFinger = new PointF(0, 0);//手指按下位置

    private final PointF point1 = new PointF(0, 0);
    private final PointF point2 = new PointF(0, 0);
    private final PointF point3 = new PointF(0, 0);

    private final PointF point4 = new PointF(0, 0);

    private final PointF point5 = new PointF(0, 0);
    private final PointF point6 = new PointF(0, 0);
    private final PointF point7 = new PointF(0, 0);

    private int defaultDragWidth = 30;// 默认箭头的宽度
    private float dragWidth;//箭头动态宽度
    private float animDragWidth;//用于动画过渡

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
        super(context);
        init(context, null);
    }

    public SingleFlipView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
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
    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SingleFlipView);

        paintColor = typedArray.getColor(R.styleable.SingleFlipView_flip_color, paintColor);

        arrowColor = typedArray.getColor(R.styleable.SingleFlipView_flip_arrow_color, arrowColor);
        arrowStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.SingleFlipView_flip_arrow_stroke_width, arrowStrokeWidth);
        defaultDragWidth = typedArray.getDimensionPixelSize(R.styleable.SingleFlipView_flip_drag_width, defaultDragWidth);

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
        upAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 动画过渡，根据最终拖动距离和动画渐变比，计算变化值
                float value = (float) animation.getAnimatedValue();
                dragWidth = animDragWidth * value;

                updatePointLocation();
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

        LogUtil.e(TAG, "sizeChanged center >>> (" + center_x + "," + center_y + ")");
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
        pointFinger.x = current_x;
        pointFinger.y = current_y;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (upAnimator.isRunning()) upAnimator.cancel();

                // down事件记录下按下初始位置，后续判断偏移距离和方向
                pointOrigin.x = current_x;
                pointOrigin.y = current_y;
                break;
            case MotionEvent.ACTION_MOVE:
                judgeDirectionAndOffsetX(current_x);

                updatePointLocation();
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

    private void judgeDirectionAndOffsetX(float current_x) {
        // x move offset
        float offsetX = Math.abs(current_x - pointOrigin.x);

        // → toward right
        if (current_x > pointOrigin.x) {
            direction = RIGHT;
        } else {// ←
            direction = LEFT;
        }

        if (offsetX >= center_x) offsetX = center_x;

        dragWidth = offsetX * 0.5f;
    }

    /**
     * 绘制移动之后的各点
     *
     * @param canvas 画布
     */
    private void drawFlipWave(Canvas canvas) {
        float origin_x = direction == LEFT ? 2 * center_x : 0;

        mPath.lineTo(point1.x, 0);
        mPath.lineTo(point1.x, point1.y);

        //绘制贝赛尔曲线
        mPath.cubicTo(point2.x, point2.y, point3.x, point3.y, point4.x, point4.y);

        mPath.cubicTo(point5.x, point5.y, point6.x, point6.y, point7.x, point7.y);

        mPath.lineTo(point7.x, center_y * 2);
        mPath.lineTo(origin_x, center_y * 2);
        mPath.close();

        //移动一段距离后(3 * defaultDragWidth)，显示箭头
        if (Math.abs(point4.x - origin_x) > 3f * defaultDragWidth) {
            float circleCenterX = point4.x - 1.5f * defaultDragWidth * direction;

            mArrowPath.moveTo(circleCenterX - 0.3f * defaultDragWidth * direction, point4.y - 0.66f * defaultDragWidth);
            mArrowPath.lineTo(circleCenterX + 0.6f * defaultDragWidth * direction, point4.y);
            mArrowPath.lineTo(circleCenterX - 0.3f * defaultDragWidth * direction, point4.y + 0.66f * defaultDragWidth);

            mArrowPath.addCircle(circleCenterX, point4.y, defaultDragWidth, Path.Direction.CCW);
        }

        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(mArrowPath, mArrowPaint);
        //绘制完当前路径，删除之前的路径
        mPath.reset();
        mArrowPath.reset();
    }

    /**
     * 刷新各绘制点的位置
     * <p>
     * direction 移动方向: ← {@link SingleFlipView#LEFT}；→ {@link SingleFlipView#RIGHT}
     */
    private void updatePointLocation() {
        float origin_x = direction == LEFT ? 2 * center_x : 0;
        float current_y = pointFinger.y;

        mPath.moveTo(origin_x, 0);

        //p1(0,0), p2(0,100)，p3(90,75)，p4(98,150)
        // p1(origin_x,currentY - 150), p2(origin_x, currentY - 50),
        // p3(origin_x + 90, currentY - 75), p4(origin_x + 98, currentY)
        point1.x = origin_x;
        point1.y = current_y - 3 * dragWidth;

        point2.x = origin_x;//origin_x, current_y - 50
        point2.y = current_y - dragWidth;

        point3.x = origin_x + 1.8f * dragWidth * direction;//origin_x + 90, current_y - 75
        point3.y = current_y - 1.5f * dragWidth;

        point4.x = origin_x + 1.93f * dragWidth * direction;//origin_x + 100, current_y
        point4.y = current_y;

        //p1(98, 150), p2(90, 225), p3(0, 200), p4(0, 300)
        // p1(origin_x + 98, currentY), p2(origin_x + 90, currentY + 75),
        // p3(origin_x, currentY + 50), p4(origin_x, currentY + 150)
        point5.x = origin_x + 1.8f * dragWidth * direction;//origin_x + 90, current_y + 75
        point5.y = current_y + 1.5f * dragWidth;

        point6.x = origin_x;//origin_x, current_y + 50
        point6.y = current_y + dragWidth;

        point7.x = origin_x;//origin_x, current_y + 150
        point7.y = current_y + 3 * dragWidth;
    }
}
