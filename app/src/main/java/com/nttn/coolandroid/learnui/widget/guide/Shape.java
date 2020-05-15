package com.nttn.coolandroid.learnui.widget.guide;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Apple.
 * Desc: 指定高亮形状
 *
 * Example: {@link RadiusRectShape}
 */
public interface Shape {
    /**
     * 绘制想要的形状
     *
     * @param canvas 画布
     * @param paint 画笔
     * @param hollow 高亮区域要素
     */
    void drawShape(Canvas canvas, Paint paint, Hollow hollow);
}
