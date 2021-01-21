package com.nttn.coolandroid.learnui.widget.flip;

import android.graphics.Path;
import android.graphics.PointF;

/**
 * Description: 管理绘及移动方向制点<br>
 * Version: 1.0     <br>
 * Update：2020/12/10   <br>
 * Created by Apple.
 */
public class CubicPointsManager {
    /**
     * 贝赛尔曲线各点及方向
     */
    public static final int LEFT = -1;
    public static final int RIGHT = 1;
    public int direction = 0;

    public final PointF pointOrigin = new PointF(0, 0);//起始手指按下的点
    public final PointF pointFinger = new PointF(0, 0);//手指按下位置

    public final PointF point1 = new PointF(0, 0);
    public final PointF point2 = new PointF(0, 0);
    public final PointF point3 = new PointF(0, 0);

    public final PointF point4 = new PointF(0, 0);

    public final PointF point5 = new PointF(0, 0);
    public final PointF point6 = new PointF(0, 0);
    public final PointF point7 = new PointF(0, 0);

    private final Path cubicPath;
    private final Path arrowPath;
    private final int center_x;
    private final int center_y;
    private final int defaultDragWidth;

    public CubicPointsManager(Path cubicPath, Path arrowPath, int center_x, int center_y,
                              int defaultDragWidth) {
        this.cubicPath = cubicPath;
        this.arrowPath = arrowPath;
        this.center_x = center_x;
        this.center_y = center_y;
        this.defaultDragWidth = defaultDragWidth;
    }

    /**
     * 判断是否向左移动
     *
     * @return true 向左移动
     */
    public boolean isTowardLeft() {
        return direction == LEFT;
    }

    /**
     * 更新手指移动时的位置
     *
     * @param x 触摸点 x
     * @param y 触摸点 y
     */
    public void updatePointFinger(float x, float y) {
        pointFinger.x = x;
        pointFinger.y = y;
    }

    /**
     * 记录手指按下的位置
     *
     * @param x 触摸点 x
     * @param y 触摸点 y
     */
    public void updatePointOrigin(float x, float y) {
        pointOrigin.x = x;
        pointOrigin.y = y;
    }

    /**
     * 更新移动方向
     */
    private void updateDirection() {
        // → toward right
        if (pointFinger.x > pointOrigin.x) {
            direction = RIGHT;
        } else {// ←
            direction = LEFT;
        }
    }

    /**
     * 计算 x 方向移动距离
     *
     * @return x 方向移动距离
     */
    public float getXOffset() {
        return pointFinger.x - pointOrigin.x;
    }

    /**
     * 获取曲线起始点 x 坐标
     *
     * @param center_x 屏幕中心点 x 坐标
     * @return 曲线起始点 x 坐标
     */
    public float getOriginX(int center_x){
        return direction == LEFT ? 2 * center_x : 0;
    }

    /**
     * 更新移动方向和曲线上各控制点
     *
     * @param dragWidth 手指翻动的距离
     */
    public void updateOnMove(float dragWidth){
        updateDirection();
        updatePointLocation(dragWidth);
    }

    /**
     * 刷新各绘制点的位置
     * <p>
     * direction 移动方向: ← {@link CubicPointsManager#LEFT}；→ {@link CubicPointsManager#RIGHT}
     */
    public void updatePointLocation(float dragWidth) {
        float origin_x = getOriginX(center_x);
        float current_y = pointFinger.y;

        cubicPath.moveTo(origin_x, 0);

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

    /**
     * 绘制曲线
     */
    public void drawCubic(){
        float origin_x = getOriginX(center_x);

        cubicPath.lineTo(point1.x, 0);
        cubicPath.lineTo(point1.x, point1.y);

        //绘制贝赛尔曲线
        cubicPath.cubicTo(point2.x, point2.y, point3.x, point3.y, point4.x, point4.y);

        cubicPath.cubicTo(point5.x, point5.y, point6.x, point6.y, point7.x, point7.y);

        cubicPath.lineTo(point7.x, center_y * 2);
        cubicPath.lineTo(origin_x, center_y * 2);
        cubicPath.close();
    }

    /**
     * 绘制箭头
     */
    public void drawArrow(){
        float origin_x = getOriginX(center_x);
        if (Math.abs(point4.x - origin_x) <= 3f * defaultDragWidth) return;

        float circleCenterX = point4.x - 1.5f * defaultDragWidth * direction;

        arrowPath.moveTo(circleCenterX - 0.3f * defaultDragWidth * direction, point4.y - 0.66f * defaultDragWidth);
        arrowPath.lineTo(circleCenterX + 0.6f * defaultDragWidth * direction, point4.y);
        arrowPath.lineTo(circleCenterX - 0.3f * defaultDragWidth * direction, point4.y + 0.66f * defaultDragWidth);

        arrowPath.addCircle(circleCenterX, point4.y, defaultDragWidth, Path.Direction.CCW);
    }
}
