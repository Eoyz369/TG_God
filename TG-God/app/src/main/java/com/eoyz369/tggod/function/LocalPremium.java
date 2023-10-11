package com.eoyz369.tggod.function;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class LocalPremium {
    // 定义一个方法，用于 hook isPremium 方法，返回 true
    // HOOK 电报 本地会员
    public void localPremium(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("org.telegram.messenger.UserConfig", classLoader, "isPremium", XC_MethodReplacement.returnConstant(true));
        XposedBridge.log("【 Local Premium Success！】 ");
    }
}
