package com.nttn.coolandroid.learnui.widget.guide;

import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by Apple.
 * Desc: 高亮部分信息
 */
public class Hollow {
    /**
     * 目标View 用于定位透明区域
     */
    public View targetView;
    /**
     * 可自定义区域大小
     */
    public Rect targetRect;
    /**
     * 指定形状
     *
     */
    public Shape shape;
    /**
     * 内间距
     */
    public Padding padding;
    /**
     * x,y方向的偏移量
     */
    public Offset offset;
    /**
     * 是否按照View背景形状自适应, 默认自适应（true）
     */
    private boolean isAutoAdaptViewBackground = true;

    public Hollow(View targetView) {
        this.targetView = targetView;
        this.targetRect = new Rect();
        this.targetView.getDrawingRect(this.targetRect);
    }

    public boolean isAutoAdaptViewBackground() {
        return isAutoAdaptViewBackground;
    }

    public void setAutoAdaptViewBackground(boolean autoAdaptViewBackground) {
        isAutoAdaptViewBackground = autoAdaptViewBackground;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Hollow) {
            Hollow hollow = (Hollow) obj;
            return hollow.targetView == targetView;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * 内间距, padding优先级最高
     */
    public static class Padding{
        public int padding = 0;
        public int paddingLeft = 0;
        public int paddingRight = 0;
        public int paddingTop = 0;
        public int paddingBottom = 0;

        public Padding() {
        }

        public Padding(int padding) {
            this.padding = padding;
        }

        public Padding(int paddingLeft, int paddingRight, int paddingTop, int paddingBottom) {
            this.paddingLeft = paddingLeft;
            this.paddingRight = paddingRight;
            this.paddingTop = paddingTop;
            this.paddingBottom = paddingBottom;
        }
    }

    /**
     * x,y方向的偏移量
     */
    public static class Offset{
        public int offsetX = 0;
        public int offsetY = 0;

        public Offset() {
        }

        public Offset(int offsetX, int offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }
    }
}
