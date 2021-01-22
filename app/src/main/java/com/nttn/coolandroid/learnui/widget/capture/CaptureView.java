package com.nttn.coolandroid.learnui.widget.capture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * Desc: 圆角矩形框
 */
public class CaptureView extends View {
    private int screenWidth, screenHeight;
    private Paint mPaint, mOvalPaint;

    private RectF rLT, rRT, rLB, rRB;
    private Rect findRect;

    private int horizontalSpace = 45;
    private float r = 75f;
    private final float leftPercent = 0.03f, rPercent = 0.05f, topPercent = 0.28f, bottomPercent = 0.72f;

    public float getTopPercent() {
        return topPercent;
    }

    public float getLeftPercent() {
        return leftPercent;
    }

    public CaptureView(Context context) {
        super(context);
        init(context);
    }

    public CaptureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CaptureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(6f);
        mOvalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOvalPaint.setColor(Color.WHITE);
        mOvalPaint.setStyle(Paint.Style.STROKE);
        mOvalPaint.setStrokeCap(Paint.Cap.ROUND);
        mOvalPaint.setStrokeWidth(15f);


        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return;
        DisplayMetrics screenMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(screenMetrics);

        screenWidth = screenMetrics.widthPixels;
        screenHeight = screenMetrics.heightPixels;
        horizontalSpace = (int) (screenWidth * leftPercent);
        r = (int) (screenWidth * rPercent);

        rLT = new RectF(horizontalSpace, screenHeight * topPercent,
          horizontalSpace + r, screenHeight * topPercent + r);
        rRT = new RectF(screenWidth - horizontalSpace - r, screenHeight * topPercent,
          screenWidth - horizontalSpace, screenHeight * topPercent + r);
        rLB = new RectF(horizontalSpace, screenHeight * bottomPercent - r,
          horizontalSpace + r, screenHeight * bottomPercent);
        rRB = new RectF(screenWidth - horizontalSpace - r, screenHeight * bottomPercent - r,
                screenWidth - horizontalSpace, screenHeight * bottomPercent);
        findRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        if (measuredWidth < screenWidth || measuredHeight < screenHeight) {
            Log.e("captureView", "onMeasure: "
                    + "screen("+screenWidth+", "+ screenHeight +"), measure("+ measuredWidth + ","+ measuredHeight +")");

            screenWidth = measuredWidth;
            screenHeight = measuredHeight;
            updateRect();
        }
    }

    private void updateRect(){
        horizontalSpace = (int) (screenWidth * leftPercent);
        r = (int) (screenWidth * rPercent);

        rLT.top = screenHeight * topPercent;
        rLT.bottom = screenHeight * topPercent + r;

        rRT.left = screenWidth - horizontalSpace - r;
        rRT.top = screenHeight * topPercent;
        rRT.right = screenWidth - horizontalSpace;
        rRT.bottom = screenHeight * topPercent + r;

        rLB.top = screenHeight * bottomPercent - r;
        rLB.bottom = screenHeight * bottomPercent;

        rRB.left = screenWidth - horizontalSpace - r;
        rRB.top = screenHeight * bottomPercent - r;
        rRB.right = screenWidth - horizontalSpace;
        rRB.bottom = screenHeight * bottomPercent;

        findRect.left = horizontalSpace;
        findRect.top = (int) (screenHeight * topPercent);
        findRect.right = screenWidth - horizontalSpace;
        findRect.bottom = (int) (screenHeight * bottomPercent);

        findRect.left = 0;
        findRect.top = 0;
        findRect.right = screenWidth;
        findRect.bottom = screenHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawFindRect(canvas);
    }

    private void drawFindRect(Canvas canvas) {
        // 左上 圆角
        canvas.drawArc(rLT, -180, 90, false, mOvalPaint);
        // 上左 短粗线
        canvas.drawLine(horizontalSpace + r / 2, screenHeight * topPercent,
                horizontalSpace + r, screenHeight * topPercent, mOvalPaint);
        // 上 细长线
        canvas.drawLine(horizontalSpace + r, screenHeight * topPercent,
                screenWidth - horizontalSpace - r, screenHeight * topPercent, mPaint);
        // 上右 短粗线
        canvas.drawLine(screenWidth - horizontalSpace - r, screenHeight * topPercent,
                screenWidth - horizontalSpace - r / 2, screenHeight * topPercent, mOvalPaint);
        // 右上 圆角
        canvas.drawArc(rRT, -90, 90, false, mOvalPaint);
        // 右上 短粗线
        canvas.drawLine(screenWidth - horizontalSpace, screenHeight * topPercent + r / 2,
                screenWidth - horizontalSpace, screenHeight * topPercent + r, mOvalPaint);
        // 右 细长线
        canvas.drawLine(screenWidth - horizontalSpace, screenHeight * topPercent + r,
                screenWidth - horizontalSpace, screenHeight * bottomPercent - r, mPaint);
        // 右下 短粗线
        canvas.drawLine(screenWidth - horizontalSpace, screenHeight * bottomPercent - r,
                screenWidth - horizontalSpace, screenHeight * bottomPercent - r / 2, mOvalPaint);
        // 右下 圆角
        canvas.drawArc(rRB, 0, 90, false, mOvalPaint);
        // 下右 短粗线
        canvas.drawLine(screenWidth - horizontalSpace - r / 2, screenHeight * bottomPercent,
                screenWidth - horizontalSpace - r, screenHeight * bottomPercent, mOvalPaint);
        // 下 细长线
        canvas.drawLine(horizontalSpace + r, screenHeight * bottomPercent,
                screenWidth - horizontalSpace - r, screenHeight * bottomPercent, mPaint);
        // 下左 短粗线
        canvas.drawLine(horizontalSpace + r, screenHeight * bottomPercent,
                horizontalSpace + r / 2, screenHeight * bottomPercent, mOvalPaint);
        // 下左 圆角
        canvas.drawArc(rLB, 90, 90, false, mOvalPaint);
        // 左下 短粗线
        canvas.drawLine(horizontalSpace, screenHeight * bottomPercent - r / 2,
                horizontalSpace, screenHeight * bottomPercent - r, mOvalPaint);
        // 左 细长线
        canvas.drawLine(horizontalSpace, screenHeight * topPercent + r,
                horizontalSpace, screenHeight * bottomPercent - r, mPaint);
        // 左上 短粗线
        canvas.drawLine(horizontalSpace, screenHeight * topPercent + r / 2,
                horizontalSpace, screenHeight * topPercent + r, mOvalPaint);
    }
}
