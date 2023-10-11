package com.eoyz369.tggod.function;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class AntiRevoke {
    // 定义一个方法，用于 hook processUpdateArray 方法，过滤掉删除消息的更新
    // HOOK 电报 防撤回
    public void antiRevoke(ClassLoader classLoader) {
        Class<?> tlUpdateDeleteChannelMessages = XposedHelpers.findClass("org.telegram.tgnet.TLRPC$TL_updateDeleteChannelMessages", classLoader);
        Class<?> tlUpdateDeleteMessages = XposedHelpers.findClass("org.telegram.tgnet.TLRPC$TL_updateDeleteMessages", classLoader);
        XposedHelpers.findAndHookMethod("org.telegram.messenger.MessagesController", classLoader, "processUpdateArray", ArrayList.class, ArrayList.class, ArrayList.class, boolean.class, int.class,
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

        XposedBridge.log("【 Anti Revoke Success ！】 ");
    }


}
