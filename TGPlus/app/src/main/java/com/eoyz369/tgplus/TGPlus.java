package com.eoyz369.tgplus;

import android.util.Log;

import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class TGPlus implements IXposedHookLoadPackage {

    //声明
    //HOOK 包名
    private static final List<String> hookPackages = Arrays.asList("org.telegram.messenger", "org.telegram.messenger.web");

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lPParam) throws Throwable {
        if (hookPackages.contains(lPParam.packageName)) {
            //HOOK Telegram Premium
            //HOOK 电报 会员
            XposedHelpers.findAndHookMethod("org.telegram.messenger.UserConfig", lPParam.classLoader, "isPremium", XC_MethodReplacement.returnConstant(true));

        }
    }
}