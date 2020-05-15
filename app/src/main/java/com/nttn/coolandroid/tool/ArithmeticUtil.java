package com.nttn.coolandroid.tool;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Apple.
 * Desc: 提供精确的四则运算
 *
 * <p>Java中的简单浮点数类型float和double运算会丢失精度，
 * 因此，float和double一般用来做科学计算或者是工程计算。
 * 不光是Java，在其它很多编程语言中也有这样的问题。
 * 在商业计算中我们要用 {@link java.math.BigDecimal}。</p>
 */
public class ArithmeticUtil {
    /**
     * 默认除法运算精度（小数点后保留的位数）
     */
    private static final int DEFAULT_DIV_SCALE = 10;

    /**
     * 精确的加法运算
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 和
     */
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 精确的减法
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 差
     */
    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 精确的乘法
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 积
     */
    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 默认精确度{@link #DEFAULT_DIV_SCALE}的除法
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 商（四舍五入处理 进一法）
     */
    public static double div(double v1, double v2) {
        return div(v1, v2, DEFAULT_DIV_SCALE);
    }

    /**
     * 自定义精确度的除法
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 精确度（小数点后保留的位数）
     * @return 商（四舍五入处理 进一法）
     */
    public static double div(double v1, double v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    /**
     * 自定义精确度和四舍五入进位方法{@link RoundingMode}的除法
     *
     * @param v1           被除数
     * @param v2           除数
     * @param scale        精确度（小数点后保留的位数）
     * @param roundingMode 自定义进位法{@link RoundingMode}
     * @return 商（四舍五入处理 自定义进位法{）
     */
    public static double div(double v1, double v2, int scale, RoundingMode roundingMode) {
        checkScale(scale);
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, roundingMode).doubleValue();
    }

    /**
     * 精确的小数位四舍五入处理，默认进一法{@link RoundingMode#HALF_UP}
     *
     * @param v     需要四舍五入处理的数字
     * @param scale 精确度（小数点后保留的位数）
     * @return 四舍五入后的结果
     */
    public static double round(double v, int scale) {
        return round(v, scale, RoundingMode.HALF_UP);
    }

    /**
     * 精确的小数位四舍五入处理，自定义进位法{@link RoundingMode}
     *
     * @param v            需要四舍五入处理的数字
     * @param scale        精确度（小数点后保留的位数）
     * @param roundingMode 自定义进位法{@link RoundingMode}
     * @return 四舍五入后的结果
     */
    public static double round(double v, int scale, RoundingMode roundingMode) {
        checkScale(scale);
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, roundingMode).doubleValue();
    }

    /**
     * 检查精度的有效性
     *
     * @param scale 精度
     */
    private static void checkScale(int scale) {
        if (scale < 0)
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
    }
}
