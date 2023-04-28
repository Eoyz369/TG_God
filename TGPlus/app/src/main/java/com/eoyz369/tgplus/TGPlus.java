package com.eoyz369.tgplus;

// 导入Xposed相关的类

import android.app.AndroidAppHelper;
import android.view.Window;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
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
        XposedBridge.log("【Current process is target app:[ " + packageName + " ]Success！】");
        ClassLoader classLoader = lpparam.classLoader;
        // 在这里编写你想要执行的代码，例如 hook 某个方法或者修改某个变量等等
        // ...

        
        
        /**
         *     HOOK Telegram Local Premium
         *     HOOK 电报 本地会员
         *     isPremium
         */

        XposedHelpers.findAndHookMethod("org.telegram.messenger.UserConfig", lpparam.classLoader, "isPremium", XC_MethodReplacement.returnConstant(true));
        XposedBridge.log("【HOOK Telegram Local Premium Success！】 ");

        
        
        /**
         *     HOOK Telegram No Screenshots
         *     HOOK 电报 禁止截图
         *     isSecuredNow
         */
        XposedHelpers.findAndHookMethod("org.telegram.messenger.FlagSecureReason", lpparam.classLoader, "isSecuredNow", Window.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                param.setResult(false);
                XposedBridge.log("【HOOK Telegram No Screenshots Success！】 ");
            }
        });

        

        /**
         *      HOOK Telegram Message removed
         *      HOOK 电报 消息被删除
         *      processUpdateArray
         */
        XposedHelpers.findAndHookMethod("org.telegram.messenger.MessagesController", lpparam.classLoader, "processUpdateArray", ArrayList.class, ArrayList.class, ArrayList.class, boolean.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> TL_updateDeleteChannelMessages = lpparam.classLoader.loadClass("org.telegram.tgnet.TLRPC$TL_updateDeleteChannelMessages");
                ArrayList<?> messageArr = (ArrayList<?>) param.args[0];
                Iterator<?> iterator = messageArr.iterator();
                while (iterator.hasNext()) {
                    Object item = iterator.next();
                    if (item.getClass().equals(TL_updateDeleteChannelMessages)) {
                        iterator.remove();
                    }
                }
                param.args[0] = messageArr;
                XposedBridge.log("【HOOK Telegram Message removed！】 ");
            }
        });






    }
}


