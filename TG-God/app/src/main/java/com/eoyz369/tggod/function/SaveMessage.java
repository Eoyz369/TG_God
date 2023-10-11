package com.eoyz369.tggod.function;

//import de.robv.android.xposed.XposedHelpers;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class SaveMessage {

    public void saveMessage(ClassLoader classLoader) {

        XposedHelpers.findAndHookMethod("org.telegram.messenger.MessageObject", classLoader, "canForwardMessage", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.args[0] = true;
            }
        });
        XposedHelpers.findAndHookMethod("org.telegram.messenger.MessageObject", classLoader, "isSecretMedia", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.args[0] = false;
            }
        });
        XposedHelpers.findAndHookMethod("org.telegram.ui.ChatActivity", classLoader, "hasSelectedNoforwardsMessage", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.args[0] = false;
            }
        });
        XposedHelpers.findAndHookMethod("org.telegram.ui.Components.SharedMediaLayout", classLoader, "hasNoforwardsMessage", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.args[0] = false;
            }
        });
        // 获取 MessageObject ChatMessageCell Theme TLRPC$Message 类
        Class<?> classTLRPCChat = XposedHelpers.findClass("org.telegram.tgnet.TLRPC$Chat", classLoader);

        XposedHelpers.findAndHookMethod("org.telegram.messenger.MessagesController", classLoader, "isChatNoForwards", classTLRPCChat, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                // Get the chat object from the parameter
                Object chat = param.args[0];

                // Check if the chat is null or has migrated to another chat
                if (chat == null || XposedHelpers.getObjectField(chat, "migrated_to") != null) {
                    return;
                }

                // Get and modify the noforwards field value from the chat object
                boolean noforwards = XposedHelpers.getBooleanField(chat, "noforwards");
                noforwards = false; // Set it to false to allow forwarding
                XposedHelpers.setBooleanField(chat, "noforwards", noforwards);
            }
        });

        XposedBridge.log("【 Message Save Success！】 ");


    }

}
