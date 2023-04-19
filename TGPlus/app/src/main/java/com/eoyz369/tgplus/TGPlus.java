package com.eoyz369.tgplus;

// 导入Xposed相关的类

import android.app.AndroidAppHelper;

import java.util.HashSet;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;



public class TestHOOk implements IXposedHookLoadPackage {


    private static final HashSet<String> PACKAGE_NAMES = new HashSet<>();

    static {
        // 定义一个包名列表，用于过滤目标应用程序
        PACKAGE_NAMES.add("org.telegram.messenger");
        PACKAGE_NAMES.add("org.telegram.messenger.web");
        PACKAGE_NAMES.add("org.telegram.messenger.beta");
    }


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // 获取当前的包名
        String packageName = AndroidAppHelper.currentPackageName();
        // 判断当前的包名是否在集合中
        if (!PACKAGE_NAMES.contains(packageName)) {
            return;
        }

        // 打印日志信息，显示当前进程是目标应用程序
        XposedBridge.log("【current process is target app: " + packageName + "】");

        // 在这里编写你想要执行的代码，例如 hook 某个方法或者修改某个变量等等
        // ...
        
        /**
         *     HOOK Telegram Local Premium
         *     HOOK 电报 本地会员
         *     unlockPremium
         */

        XposedHelpers.findAndHookMethod("org.telegram.messenger.UserConfig", lpparam.classLoader, "isPremium", XC_MethodReplacement.returnConstant(true));
        XposedBridge.log("【HOOK Telegram Local Premium Sucess】 ");


    }


}


