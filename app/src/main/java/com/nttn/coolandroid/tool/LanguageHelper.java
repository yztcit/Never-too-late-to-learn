package com.nttn.coolandroid.tool;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.support.annotation.NonNull;
import android.support.v4.os.ConfigurationCompat;
import android.support.v4.os.LocaleListCompat;
import android.text.TextUtils;

import java.util.Locale;

/**
 * Desc: 切换语言.
 */
public class LanguageHelper {
    /**
     * 获取应用语言
     *
     * @param context 上下文
     * @return Locale 语言
     */
    public static Locale getAppLocale(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
        return locale;
    }

    /**
     * 获取系统语言
     */
    public static LocaleListCompat getSystemLanguage() {
        Configuration configuration = Resources.getSystem().getConfiguration();
        return ConfigurationCompat.getLocales(configuration);
    }

    /**
     * 获取系统首选语言
     * 注意：该方法获取的是用户实际设置的不经API调整的系统首选语言
     *
     * @return Locale
     */
    public static Locale getSysPreferredLocale() {
        Locale locale;
        //7.0以下直接获取系统默认语言
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            // 等同于context.getResources().getConfiguration().locale;
            locale = Locale.getDefault();
        }
        // 7.0以上获取系统首选语言
        else {
            /*
             * 以下两种方法等价，都是获取经API调整过的系统语言列表（可能与用户实际设置的不同）
             * 1.context.getResources().getConfiguration().getLocales()
             * 2.LocaleList.getAdjustedDefault()
             */
            // 获取用户实际设置的语言列表
            locale = LocaleList.getDefault().get(0);
        }
        return locale;
    }

    /**
     * 判断与 app 中的多语言信息是否相同
     */
    public static boolean isSameWith(Context context, String language, String country) {
        Locale appLocale = getAppLocale(context);
        String appLanguage = appLocale.getLanguage();
        String appCountry = appLocale.getCountry();
        return TextUtils.equals(appLanguage, language) && TextUtils.equals(appCountry, country);
    }

    public static void changeLanguage(Context context) {
        changeLanguage(context, getSysPreferredLocale());
    }

    public static void changeLanguage(Context context, @NonNull Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale);
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
            context.createConfigurationContext(configuration);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //Android 4.1 以上方法
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}
