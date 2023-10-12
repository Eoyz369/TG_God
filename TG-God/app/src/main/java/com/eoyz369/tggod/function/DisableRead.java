package com.eoyz369.tggod.function;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class DisableRead {


    public void disableRead(ClassLoader classLoader) {
        // 获取 ReadTask 类
        Class<?> classReadTask = XposedHelpers.findClass("org.telegram.messenger.MessagesController$ReadTask", classLoader);
        // hook completeReadTask 方法
        XposedHelpers.findAndHookMethod("org.telegram.messenger.MessagesController", classLoader, "completeReadTask", classReadTask, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param)  {
                // 直接返回 null，不执行原方法
                param.setResult(null);
            }
        });

        Class<?> classTLRPC$StoryItem = XposedHelpers.findClass("org.telegram.tgnet.TLRPC$StoryItem", classLoader);

        XposedHelpers.findAndHookMethod("org.telegram.ui.Stories.StoriesController", classLoader, "markStoryAsRead", long.class, classTLRPC$StoryItem, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(false);
            }
        });


        XposedBridge.log("【 HOOK Telegram Remove read receipts Success！】 ");
    }
}
