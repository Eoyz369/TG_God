package com.eoyz369.tggod.function;

//import de.robv.android.xposed.XposedHelpers;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class SaveMessage {

    public void saveMessage(ClassLoader classLoader) {

        XposedHelpers.findAndHookMethod("org.telegram.messenger.MessageObject", classLoader, "canForwardMessage", new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.args[0] = true;
            }
        });
        XposedHelpers.findAndHookMethod("org.telegram.messenger.MessageObject", classLoader, "isSecretMedia", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
//                super.beforeHookedMethod(param);
                param.args[0] = false;
            }
        });
        XposedHelpers.findAndHookMethod("org.telegram.ui.ChatActivity", classLoader, "hasSelectedNoforwardsMessage", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                param.args[0] = false;
            }
        });
        XposedHelpers.findAndHookMethod("org.telegram.ui.Components.SharedMediaLayout", classLoader, "hasNoforwardsMessage", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                param.args[0] = false;
            }
        });
        // 获取 MessageObject ChatMessageCell Theme TLRPC$Message 类
        Class<?> classTLRPCChat = XposedHelpers.findClass("org.telegram.tgnet.TLRPC$Chat", classLoader);

        XposedHelpers.findAndHookMethod("org.telegram.messenger.MessagesController", classLoader, "isChatNoForwards", classTLRPCChat, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Object chat = param.args[0];

                if (chat == null || XposedHelpers.getObjectField(chat, "migrated_to") != null) {
                    return;
                }
//                XposedHelpers.getBooleanField(chat, "noforwards");
                boolean noforwards;
                noforwards = false;
                XposedHelpers.setBooleanField(chat, "noforwards", noforwards);
            }
        });

        XposedBridge.log("【 Message Save Success！】 ");


    }

}