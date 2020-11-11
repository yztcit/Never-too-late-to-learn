package com.nttn.coolandroid.learnui.widget.flip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * Description: 翻页效果    <br>
 * Version: 1.0     <br>
 * Update：2020/11/7   <br>
 * Created by Apple.
 */
public class FlipView extends ViewGroup {
    private Paint mPaint = new Paint();
    private Path mPath = new Path();

    private int CENTER_X, CENTER_Y;

    private float currentX, currentY;

    public FlipView(Context context) {
        super(context);
        init();
    }

    public FlipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        CENTER_X = w / 2;
        CENTER_Y = h / 2;
        invalidate();
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.lineTo(currentX, 0f);
        mPath.lineTo(currentX, CENTER_Y * 2);
        mPath.lineTo(0f, CENTER_Y * 2);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).layout(l, t, getChildAt(i).getMeasuredWidth(), getChildAt(i).getMeasuredHeight());
        }
    }
}
