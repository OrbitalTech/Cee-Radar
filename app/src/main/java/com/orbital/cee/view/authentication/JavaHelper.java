package com.orbital.cee.view.authentication;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class JavaHelper {
     static void printHashKey(Context context){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "com.orbital.cee",PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("keyHash:", Base64.encodeToString(md.digest(),Base64.DEFAULT));

            }
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }


    }
}
