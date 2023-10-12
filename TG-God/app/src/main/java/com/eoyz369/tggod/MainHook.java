package com.eoyz369.tggod;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.app.AndroidAppHelper;
import android.text.TextPaint;

import com.eoyz369.tggod.function.AntiRevoke;
import com.eoyz369.tggod.function.DisableRead;
import com.eoyz369.tggod.function.LocalPremium;
import com.eoyz369.tggod.function.SaveMessage;
import com.eoyz369.tggod.ui.Settings;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class MainHook implements IXposedHookLoadPackage {

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
        XposedBridge.log("【Be the God of Telegram！】");
        XposedBridge.log("【Current process is target app:[ " + packageName + " ]Success！】");

        ClassLoader classLoader = lpparam.classLoader;
        // 在这里编写你想要执行的代码，例如 hook 某个方法或者修改某个变量等等
        // ...
        Settings uiSettings = new Settings();
        // 创建  类的实例
        LocalPremium localPremium = new LocalPremium();
        AntiRevoke antiRevoke = new AntiRevoke();
        SaveMessage saveMessage = new SaveMessage();
        DisableRead disableRead = new DisableRead();


;



        // 调用  实例的  方法
        localPremium.localPremium(classLoader);
        antiRevoke.antiRevoke(classLoader);
        saveMessage.saveMessage(classLoader);
        disableRead.disableRead(classLoader);
        // HOOK Telegram Show Msg Id.
//        hookMeasureTime(classLoader);

        uiSettings.uiSettings(classLoader);



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