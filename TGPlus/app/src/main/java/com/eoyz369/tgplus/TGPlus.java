package com.eoyz369.tgplus;

// 导入Xposed相关的类

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.app.AndroidAppHelper;
import android.view.Window;

import java.util.ArrayList;
import java.util.HashSet;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class TGPlus implements IXposedHookLoadPackage {



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
        //  ClassLoader classLoader = lpparam.classLoader;
        // 在这里编写你想要执行的代码，例如 hook 某个方法或者修改某个变量等等
        // ...

        /**
         *     HOOK Telegram Local Premium
         *     HOOK 电报 本地会员
         *     isPremium
         */
        findAndHookMethod("org.telegram.messenger.UserConfig", lpparam.classLoader, "isPremium", XC_MethodReplacement.returnConstant(true));
        XposedBridge.log("【HOOK Telegram Local Premium Success！】 ");



        /**
         *      HOOK Telegram Message removed
         *      HOOK 电报 消息被删除
         *      processUpdateArray
         */
        // 解密类和方法名
        Class<?> tlUpdateDeleteChannelMessages = XposedHelpers.findClass("org.telegram.tgnet.TLRPC$TL_updateDeleteChannelMessages", lpparam.classLoader);
        Class<?> tlUpdateDeleteMessages = XposedHelpers.findClass("org.telegram.tgnet.TLRPC$TL_updateDeleteMessages", lpparam.classLoader);

        findAndHookMethod("org.telegram.messenger.MessagesController", lpparam.classLoader, "processUpdateArray", ArrayList.class, ArrayList.class, ArrayList.class, boolean.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        ArrayList<?> messageArr = (ArrayList<?>) param.args[0];
                        ArrayList<Object> newMessageArr = new ArrayList<>();
                        for (Object item : messageArr) {
                            if (!(item.getClass().equals(tlUpdateDeleteChannelMessages)) && !(item.getClass().equals(tlUpdateDeleteMessages))) {
                                newMessageArr.add(item);
                            }
                        }
                        param.args[0] = newMessageArr;
                    }
                });
        XposedBridge.log("【HOOK Telegram Message removed！】 ");



        /**
         *     HOOK Telegram No screenshots
         *     HOOK 电报 禁止截图
         *     isSecuredNow
         */

        findAndHookMethod("org.telegram.messenger.FlagSecureReason", lpparam.classLoader, "isSecuredNow", Window.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                param.setResult(false);
                XposedBridge.log("【HOOK Telegram No screenshots Success！】 ");
            }
        });



        /**
         *     HOOK Telegram Prohibit Copying
         *     HOOK 电报 禁止复制
         *     getMessageObject
         */
        // 解密类和方法名
        Class<?> chatMessageCellClass = XposedHelpers.findClass("org.telegram.ui.Cells.ChatMessageCell", lpparam.classLoader);
        findAndHookMethod(chatMessageCellClass, "getMessageObject", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object ts = param.thisObject;
                Object messageObjectToSet = XposedHelpers.getObjectField(ts, "messageObjectToSet");
                Object currentMessageObject = XposedHelpers.getObjectField(ts, "currentMessageObject");
                if (messageObjectToSet != null) {
                    XposedHelpers.setBooleanField(XposedHelpers.getObjectField(messageObjectToSet, "messageOwner"), "noforwards", false);
                    param.setResult(messageObjectToSet);
                } else {
                    XposedHelpers.setBooleanField(XposedHelpers.getObjectField(currentMessageObject, "messageOwner"), "noforwards", false);
                    param.setResult(currentMessageObject);
                }

            }
        });
        // 解密类和方法名
        Class<?> tlrpcChatClass = XposedHelpers.findClass("org.telegram.tgnet.TLRPC$Chat", lpparam.classLoader);
        findAndHookMethod("org.telegram.messenger.MessagesController", lpparam.classLoader, "isChatNoForwards", tlrpcChatClass, XC_MethodReplacement.returnConstant(false));
        XposedBridge.log("【HOOK Telegram Prohibit Copying Success！】 ");



        /**
         *     HOOK Telegram Remove read receipts
         *     HOOK 电报 删除已读回执
         *     completeReadTask
         */

        ClassLoader classLoader=lpparam.classLoader;
        Class ReadTaskClass=classLoader.loadClass("org.telegram.messenger.MessagesController$ReadTask");
        XposedHelpers.findAndHookMethod("org.telegram.messenger.MessagesController", classLoader, "completeReadTask", ReadTaskClass, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(null);
            }
        });
        XposedBridge.log("【 HOOK Telegram Remove read receipts Success！】 ");




    }
}





