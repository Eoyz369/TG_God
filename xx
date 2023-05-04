package com.alex193a.tguseridviewer;

import c.d;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.lang.reflect.Field;

/* loaded from: classes.dex */
public final class Module implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    public long a;

    /* loaded from: classes.dex */
    public static final class a extends XC_MethodHook {
        public final /* synthetic */ Field a;

        /* renamed from: b  reason: collision with root package name */
        public final /* synthetic */ Module f0b;

        public a(Field field, Module module) {
            this.a = field;
            this.f0b = module;
        }

        public final void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            super.afterHookedMethod(methodHookParam);
            if (methodHookParam != null) {
                Field field = this.a;
                Module module = this.f0b;
                Object obj = field.get(methodHookParam.thisObject);
                if (obj != null) {
                    Long l = (Long) obj;
                    l.longValue();
                    module.a = l.longValue();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class b extends XC_MethodHook {
        public b() {
        }

        public final void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            super.beforeHookedMethod(methodHookParam);
            if (methodHookParam != null) {
                Module module = Module.this;
                Object obj = methodHookParam.args[0];
                if (obj == null) {
                    NullPointerException nullPointerException = new NullPointerException("null cannot be cast to non-null type kotlin.String");
                    b.a.b(nullPointerException);
                    throw nullPointerException;
                }
                if (d.c((String) obj, "@")) {
                    Object[] objArr = methodHookParam.args;
                    objArr[0] = methodHookParam.args[0] + " (ID: " + module.a + ')';
                }
                Object obj2 = methodHookParam.args[0];
                if (obj2 == null) {
                    NullPointerException nullPointerException2 = new NullPointerException("null cannot be cast to non-null type kotlin.String");
                    b.a.b(nullPointerException2);
                    throw nullPointerException2;
                } else if (d.c((String) obj2, "Unknown")) {
                    Object[] objArr2 = methodHookParam.args;
                    objArr2[0] = methodHookParam.args[0] + " (ID: " + module.a + ')';
                }
            }
        }
    }

    public final void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        b.a.a(loadPackageParam, "lpparam");
        if (loadPackageParam.packageName.equals("org.telegram.messenger.beta") || loadPackageParam.packageName.equals("org.telegram.messenger.web") || loadPackageParam.packageName.equals("org.telegram.messenger")) {
            try {
                XposedHelpers.findAndHookMethod("org.telegram.ui.ProfileActivity", loadPackageParam.classLoader, "onFragmentCreate", new Object[]{new a(XposedHelpers.findFieldIfExists(XposedHelpers.findClass("org.telegram.ui.ProfileActivity", loadPackageParam.classLoader), "userId"), this)});
                XposedHelpers.findAndHookMethod("org.telegram.ui.Cells.TextDetailCell", loadPackageParam.classLoader, "setTextAndValue", new Object[]{CharSequence.class, CharSequence.class, Boolean.TYPE, new b()});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
        boolean z = true;
        if ((startupParam == null || !startupParam.startsSystemServer) ? false : false) {
            XposedBridge.log("+++ Welcome to TG UserID Viewer Module +++");
        }
    }
}
