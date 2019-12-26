package com.chen.coolandroid.learndrawview.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Apple on 2019/12/26.
 */
public class FollowView extends View {
    private int lastX;
    private int lastY;

    public FollowView(Context context) {
        super(context);
    }

    public FollowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FollowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //触摸屏幕的位置
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                performClick();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                //手指移动的距离
                int xOffset = x - lastX;
                int yOffset = y - lastY;
                //重新布局view，实现跟随手势移动
                //方法1：layout
                /*layout(getLeft() + xOffset, getTop() + yOffset,
                        getRight() + xOffset, getBottom() + yOffset);*/
                //方法2：offsetLeftAndRight, offsetTopAndBottom
                offsetLeftAndRight(xOffset);
                offsetTopAndBottom(yOffset);
                //方法3：scrollBy，需要注意传负值（scrollTo、scrollBy移动的是View的内容）
                //方法4：layoutParams
                //方法5：属性动画
                break;
        }
        return true;
    }
}
