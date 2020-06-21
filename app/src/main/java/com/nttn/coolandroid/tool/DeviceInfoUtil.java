package com.nttn.coolandroid.tool;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.Utils;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Apple on 2020/6/11.
 * Desc: 获取设备信息
 */
public class DeviceInfoUtil {

    /**
     * 获取设备宽度（px）
     *
     */
    public static int getDeviceWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取设备高度（px）
     */
    public static int getDeviceHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取设备的唯一标识， 需要 “android.permission.READ_Phone_STATE”权限
     */
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        String deviceId = tm.getDeviceId();
        if (deviceId == null) {
            return "UnKnown";
        } else {
            return deviceId;
        }
    }

    /**
     * 获取厂商名
     * **/
    public static String getDeviceManufacturer() {
        return android.os.Build.MANUFACTURER;
    }

    /**
     * 获取产品名
     * **/
    public static String getDeviceProduct() {
        return android.os.Build.PRODUCT;
    }

    /**
     * 获取手机品牌
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机型号
     */
    public static String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机主板名
     */
    public static String getDeviceBoard() {
        return android.os.Build.BOARD;
    }

    /**
     * 设备名
     * **/
    public static String getDeviceDevice() {
        return android.os.Build.DEVICE;
    }

    /**
     *
     *
     * fingerprit 信息
     * **/
    public static String getDeviceFubgerprint() {
        return android.os.Build.FINGERPRINT;
    }

    /**
     * 硬件名
     *
     * **/
    public static String getDeviceHardware() {
        return android.os.Build.HARDWARE;
    }

    /**
     * 主机
     *
     * **/
    public static String getDeviceHost() {
        return android.os.Build.HOST;
    }

    /**
     *
     * 显示ID
     * **/
    public static String getDeviceDisplay() {
        return android.os.Build.DISPLAY;
    }

    /**
     * ID
     *
     * **/
    public static String getDeviceId() {
        return android.os.Build.ID;
    }

    /**
     * 获取手机用户名
     *
     * **/
    public static String getDeviceUser() {
        return android.os.Build.USER;
    }

    /**
     * 获取手机 硬件序列号
     * **/
    public static String getDeviceSerial() {
        return android.os.Build.SERIAL;
    }

    /**
     * 获取手机Android 系统SDK
     *
     * @return
     */
    public static int getDeviceSDK() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机Android 版本
     *
     * @return
     */
    public static String getDeviceAndroidVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取当前手机系统语言。
     */
    public static String getDeviceDefaultLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     */
    public static String getDeviceSupportLanguage() {
        return Arrays.toString(Locale.getAvailableLocales());
    }

    public static String getDeviceAllInfo(Context context) {

        return "\n\n1. IMEI:\n\t\t" + getIMEI(context)

                + "\n\n2. 设备宽度:\n\t\t" + getDeviceWidth(context)

                + "\n\n3. 设备高度:\n\t\t" + getDeviceHeight(context)

                + "\n\n4. 是否有内置SD卡:\n\t\t" + SDCardUtils.isSDCardMount()

                + "\n\n5. RAM 信息:\n\t\t" + SDCardUtils.getRAMInfo(context)

                + "\n\n6. 内部存储信息\n\t\t" + SDCardUtils.getStorageInfo(context, 0)

                + "\n\n7. SD卡 信息:\n\t\t" + SDCardUtils.getStorageInfo(context, 1)

                + "\n\n8. 是否联网:\n\t\t" + NetworkUtils.isConnected()

                + "\n\n9. 网络类型:\n\t\t" + NetworkUtils.getNetworkType()

                + "\n\n10. 系统默认语言:\n\t\t" + getDeviceDefaultLanguage()

                + "\n\n11. 硬件序列号(设备名):\n\t\t" + android.os.Build.SERIAL

                + "\n\n12. 手机型号:\n\t\t" + android.os.Build.MODEL

                + "\n\n13. 生产厂商:\n\t\t" + android.os.Build.MANUFACTURER

                + "\n\n14. 手机Fingerprint标识:\n\t\t" + android.os.Build.FINGERPRINT

                + "\n\n15. Android 版本:\n\t\t" + android.os.Build.VERSION.RELEASE

                + "\n\n16. Android SDK版本:\n\t\t" + android.os.Build.VERSION.SDK_INT

                + "\n\n17. 安全patch 时间:\n\t\t" + android.os.Build.VERSION.SECURITY_PATCH

                + "\n\n18. 发布时间:\n\t\t" + DateUtil.utc2Local(android.os.Build.TIME)

                + "\n\n19. 版本类型:\n\t\t" + android.os.Build.TYPE

                + "\n\n20. 用户名:\n\t\t" + android.os.Build.USER

                + "\n\n21. 产品名:\n\t\t" + android.os.Build.PRODUCT

                + "\n\n22. ID:\n\t\t" + android.os.Build.ID

                + "\n\n23. 显示ID:\n\t\t" + android.os.Build.DISPLAY

                + "\n\n24. 硬件名:\n\t\t" + android.os.Build.HARDWARE

                + "\n\n25. 产品名:\n\t\t" + android.os.Build.DEVICE

                + "\n\n26. Bootloader:\n\t\t" + android.os.Build.BOOTLOADER

                + "\n\n27. 主板名:\n\t\t" + android.os.Build.BOARD

                + "\n\n28. CodeName:\n\t\t" + android.os.Build.VERSION.CODENAME
                + "\n\n29. 语言支持:\n\t\t" + getDeviceSupportLanguage();

    }
}
