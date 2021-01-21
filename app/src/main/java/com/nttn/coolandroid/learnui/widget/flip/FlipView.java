package com.nttn.coolandroid.learnui.widget.flip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.nttn.coolandroid.tool.LogUtil;

/**
 * Description: 翻页效果    <br>
 * Version: 1.0     <br>
 * Update：2020/11/7   <br>
 * Created by Apple.
 *
 * View加载完成之后(onFinishInflate)并未立即回调测量、布局、绘制
 * 而是在onResume之后View才会回调onAttachedToWindow-->onMeasure-->onSizeChanged-->onLayout-->onDraw
 *
 * ViewGroup在没有背景时不会走onDraw方法，但可以走dispatchDraw
 * 原因在于View对onDraw的控制时做了限定：[if (!dirtyOpaque) onDraw(canvas)]
 * 你可以使用onDraw，在之前设个透明色即可:setBackgroundColor(0x00000000);
 *
 * 作者：张风捷特烈
 * 链接：https://juejin.im/post/6844903780035608583
 */
public class FlipView extends ViewGroup {
    private static final String TAG = "FlipView";

    private Paint mPaint = new Paint();
    private Path mPath = new Path();

    private int CENTER_X, CENTER_Y;

    private float currentX, currentY;

    public FlipView(Context context) {
        this(context, null);
    }

    public FlipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        log("onFinishInflate");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        log("onAttachedToWindow");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        log("onMeasure>>> " + "(" + widthMeasureSpec + ", " + heightMeasureSpec + ")");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        log("onSizeChanged>>> " + "(" + oldw + ", " + oldh + ") →" + "(" + w + ", " + h + ")");
        CENTER_X = w / 2;
        CENTER_Y = h / 2;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            currentX = event.getX();
            currentY = event.getY();
            return true;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        log("onLayout>>> " + changed + "(" + l + ", " + t + ", " + r + ", " + b + ")");
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).layout(l, t, getChildAt(i).getMeasuredWidth(), getChildAt(i).getMeasuredHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        log("onDraw");
        mPath.lineTo(currentX, 0f);
        mPath.lineTo(currentX, CENTER_Y * 2);
        mPath.lineTo(0f, CENTER_Y * 2);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }

    private void log(String msg) {
        LogUtil.d(TAG, msg);
    }
}
