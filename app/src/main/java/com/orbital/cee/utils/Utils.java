package com.orbital.cee.utils;

import static android.content.Context.ACTIVITY_SERVICE;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.util.NumberUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

public class Utils {
    public static int buildNumber(Context context){
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return  pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static String currentVersion(Context context){
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return  pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "0";
        }
    }
    public static Boolean isMockLocationEnabled(Location location) {
        return location.isFromMockProvider();
    }
//    public static boolean checkApp(Context context){
//        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//        ComponentName componentInfo = taskInfo.get(0).topActivity;
//        return componentInfo.getPackageName().equalsIgnoreCase(context.getPackageName());
//    }
    public static boolean googlePlayServiceEnabled(Context context) {
        try {
            GoogleApiAvailability instance = GoogleApiAvailability.getInstance();
            int responseCode = instance.isGooglePlayServicesAvailable(context);
            if (responseCode == ConnectionResult.SUCCESS) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }
    public static Float reverseDirection(Float bearing){
        if (bearing >=180f){
            bearing -= 180f;
        }else {
            bearing += 180f;
        }
        return bearing;
    }


    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }
    public static String getDeviceUniqueID(Activity activity){
        return Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
    public static class Toaster {
        private static final int SHORT_TOAST_DURATION = 2000;

        private Toaster() {}

        public static void makeLongToast(Context context,String text, long durationInMillis) {
            final Toast t = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);

            new CountDownTimer(Math.max(durationInMillis - SHORT_TOAST_DURATION, 1000), 1000) {
                @Override
                public void onFinish() {
                    t.show();
                }

                @Override
                public void onTick(long millisUntilFinished) {
                    t.show();
                }
            }.start();
        }

        public static void fix() {
            try {
                Class<?> c = Class.forName("java.lang.Daemons");
                Field maxField = c.getDeclaredField("MAX_FINALIZE_NANOS");
                maxField.setAccessible(true);
                maxField.set(null, Long.MAX_VALUE);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
    private static String detectCountryBySIM(Context context){
        try {
            TelephonyManager country = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Log.d("COUNTRYCODE","SIM: "+country.getNetworkCountryIso());
            return country.getSimCountryIso();
        }catch (Exception e){
            e.printStackTrace();
            return "uae";
        }
    }
    private static String detectCountryByNetwork(Context context){
        try {
            TelephonyManager country = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return country.getNetworkCountryIso();
        }catch (Exception e){
            e.printStackTrace();
            return "uae";
        }
    }
    public static String getCountryCode(Context context){
        try {
            TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telMgr.getSimState() == TelephonyManager.SIM_STATE_UNKNOWN){
                return  detectCountryByNetwork(context);
            }else{
                return detectCountryBySIM(context);
            }
        }catch (Exception e){
            e.printStackTrace();
            return "uae";
        }

    }


}
