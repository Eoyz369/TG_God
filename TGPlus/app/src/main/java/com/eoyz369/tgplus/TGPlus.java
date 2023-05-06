package com.eoyz369.tgplus;

//  导入需要的类

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.app.AndroidAppHelper;
import android.text.TextPaint;
import android.view.Window;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class TGPlus implements IXposedHookLoadPackage {

    // 定义一个常量，表示目标应用程序的包名集合
    private static final Set<String> PACKAGE_NAMES = new HashSet<>(Arrays.asList(
            "org.telegram.messenger",
            "org.telegram.messenger.web",
            "org.telegram.messenger.beta",
            // 第三方包名
            "org.telegram.plus"
    ));

    // 定义一个方法，处理加载包的事件
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // 获取当前的包名
        String packageName = AndroidAppHelper.currentPackageName();
        // 判断当前的包名是否在集合中
        if (!PACKAGE_NAMES.contains(packageName)) {
            return;
        }

        // 打印日志信息，显示当前进程是目标应用程序
        XposedBridge.log("【TGPlus 2.2.05 ！】");
        XposedBridge.log("【Current process is target app:[ " + packageName + " ]Success！】");

        ClassLoader classLoader = lpparam.classLoader;
        // 在这里编写你想要执行的代码，例如 hook 某个方法或者修改某个变量等等
        // ...

        // HOOK Telegram Local Premium
        hookIsPremium(classLoader);

        // HOOK Telegram Remove read receipts
        hookCompleteReadTask(classLoader);

        // HOOK Telegram Message removed
        hookProcessUpdateArray(classLoader);

        // HOOK Telegram No screenshots
        hookIsSecuredNow(classLoader);

        // HOOK Telegram Prohibit Copying
        hookGetMessageObject(classLoader);

        // HOOK Telegram Show Msg Id.
        hookMeasureTime(classLoader);
    }

    // 定义一个方法，用于 hook isPremium 方法，返回 true
    // HOOK 电报 本地会员
    private void hookIsPremium(ClassLoader classLoader) {
        findAndHookMethod("org.telegram.messenger.UserConfig", classLoader, "isPremium", XC_MethodReplacement.returnConstant(true));
        XposedBridge.log("【HOOK Telegram Local Premium Success！】 ");
    }

    // 定义一个方法，用于 hook processUpdateArray 方法，过滤掉删除消息的更新
    // HOOK 电报 消息被删除
    private void hookProcessUpdateArray(ClassLoader classLoader) {
        Class<?> tlUpdateDeleteChannelMessages = XposedHelpers.findClass("org.telegram.tgnet.TLRPC$TL_updateDeleteChannelMessages", classLoader);
        Class<?> tlUpdateDeleteMessages = XposedHelpers.findClass("org.telegram.tgnet.TLRPC$TL_updateDeleteMessages", classLoader);
        findAndHookMethod("org.telegram.messenger.MessagesController", classLoader, "processUpdateArray", ArrayList.class, ArrayList.class, ArrayList.class, boolean.class, int.class,
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
    }



    // 定义一个方法，用于 hook isSecuredNow 方法，返回 false
    // HOOK 电报 解除禁止截图
    private void hookIsSecuredNow(ClassLoader classLoader) {
        findAndHookMethod("org.telegram.messenger.FlagSecureReason", classLoader, "isSecuredNow", Window.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                param.setResult(false);
            }
        });
        XposedBridge.log("【HOOK Telegram No screenshots Success！】 ");
    }

    // 定义一个方法，用于 hook getMessageObject 方法，取消复制限制
    // HOOK 电报 取消复制限制
    private void hookGetMessageObject(ClassLoader classLoader) {
        // 获取 ChatMessageCell 类
        Class<?> chatMessageCellClass = XposedHelpers.findClass("org.telegram.ui.Cells.ChatMessageCell", classLoader);
        // hook getMessageObject 方法
        findAndHookMethod(chatMessageCellClass, "getMessageObject", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                // 获取当前对象和消息对象
                Object ts = param.thisObject;
                Object messageObjectToSet = XposedHelpers.getObjectField(ts, "messageObjectToSet");
                Object currentMessageObject = XposedHelpers.getObjectField(ts, "currentMessageObject");
                // 判断消息对象是否为空，如果不为空，就修改其 noforwards 字段为 false
                if (messageObjectToSet != null) {
                    XposedHelpers.setBooleanField(XposedHelpers.getObjectField(messageObjectToSet, "messageOwner"), "noforwards", false);
                    param.setResult(messageObjectToSet);
                } else {
                    XposedHelpers.setBooleanField(XposedHelpers.getObjectField(currentMessageObject, "messageOwner"), "noforwards", false);
                    param.setResult(currentMessageObject);
                }
            }
        });
        // 获取 TLRPC$Chat 类
        Class<?> tlrpcChatClass = XposedHelpers.findClass("org.telegram.tgnet.TLRPC$Chat", classLoader);
        // hook isChatNoForwards 方法，返回 false
        findAndHookMethod("org.telegram.messenger.MessagesController", classLoader, "isChatNoForwards", tlrpcChatClass, XC_MethodReplacement.returnConstant(false));
        XposedBridge.log("【HOOK Telegram Prohibit Copying Success！】 ");
    }

    // 定义一个方法，用于 hook completeReadTask 方法，取消已读回执
    // HOOK 电报 取消已读回执
    private void hookCompleteReadTask(ClassLoader classLoader) {
        // 获取 ReadTask 类
        Class<?> readTaskClass = XposedHelpers.findClass("org.telegram.messenger.MessagesController$ReadTask", classLoader);
        // hook completeReadTask 方法
        findAndHookMethod("org.telegram.messenger.MessagesController", classLoader, "completeReadTask", readTaskClass, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                // 直接返回 null，不执行原方法
                param.setResult(null);
            }
        });
        XposedBridge.log("【 HOOK Telegram Remove read receipts Success！】 ");
    }

    // 定义一个方法，用于 hook MeasureTime 方法，显示信息ID
    // HOOK 电报 显示信息ID
    private void hookMeasureTime(ClassLoader classLoader) {
        // 获取 MessageObject ChatMessageCell Theme TLRPC$Message 类
        Class<?> messageObjectClass = XposedHelpers.findClass("org.telegram.messenger.MessageObject", classLoader);
        Class<?> chatMessageCellClass = XposedHelpers.findClass("org.telegram.ui.Cells.ChatMessageCell", classLoader);
        Class<?> actionBarThemeClass = XposedHelpers.findClass("org.telegram.ui.ActionBar.Theme", classLoader);
        Class<?> tLRPC_MessageClass = XposedHelpers.findClass("org.telegram.tgnet.TLRPC$Message", classLoader);

        // 获取measureTime方法中的一些Field对象
        Field currentTimeString = XposedHelpers.findField(chatMessageCellClass, "currentTimeString");
        Field chatTimePaint = XposedHelpers.findField(actionBarThemeClass, "chat_timePaint");
        Field timeTextWidth = XposedHelpers.findField(chatMessageCellClass, "timeTextWidth");
        Field timeWidth = XposedHelpers.findField(chatMessageCellClass, "timeWidth");
        Field messageOwner = XposedHelpers.findField(messageObjectClass, "messageOwner");
        Field msgId = XposedHelpers.findField(tLRPC_MessageClass, "id");

        findAndHookMethod("org.telegram.ui.Cells.ChatMessageCell", classLoader, "measureTime", messageObjectClass,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        String time = (String) currentTimeString.get(param.thisObject);
                        Object messageObject = param.args[0];
                        Object owner = messageOwner.get(messageObject);
                        int id = msgId.getInt(owner);
                        String msgId = " ["+id+"]";
                        time = msgId + " | " + time ;
                        currentTimeString.set(param.thisObject, time);
                        TextPaint paint = (TextPaint) chatTimePaint.get(null);
                        assert paint != null;
                        int deltaWidth = (int) Math.ceil(paint.measureText(msgId));
                        int oldTimeTextWidth = timeTextWidth.getInt(param.thisObject);
                        timeTextWidth.setInt(param.thisObject, oldTimeTextWidth + deltaWidth);
                        int oldTimeWidth = timeWidth.getInt(param.thisObject);
                        timeWidth.setInt(param.thisObject, oldTimeWidth + deltaWidth);
                    }
                });
        XposedBridge.log("【 HOOK Telegram Show Msg Id Success！】 ");
    }
    
}











