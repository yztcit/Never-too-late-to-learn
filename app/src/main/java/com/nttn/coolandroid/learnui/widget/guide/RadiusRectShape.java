package com.nttn.coolandroid.learnui.widget.guide;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by Apple.
 * Desc: 示例，自定义高亮区域形状
 */
public class RadiusRectShape implements Shape{
    private float radius;

    public RadiusRectShape(float radius) {
        this.radius = radius;
    }

    @Override
    public void drawShape(Canvas canvas, Paint paint, Hollow hollow) {
        canvas.drawRoundRect(new RectF(hollow.targetRect.left, hollow.targetRect.top,
                hollow.targetRect.right, hollow.targetRect.bottom), radius, radius, paint);
    }
}
