package zyz.hero.capture_library.utils.statusbar;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.view.ViewCompat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class StatusBarUtils {
    /**
     * 改状态栏背景色为backgroundColor，图标为iconColorBlack
     *
     * @param activity
     * @param backgroundColor 背景色
     * @param iconColorBlack  状态栏图标颜色 true黑色 false白色
     */
    public static void setStatusBarColor(Activity activity, int backgroundColor, boolean iconColorBlack) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //如果是6.0以上将状态栏文字改为黑色，并设置状态栏颜色
            activity.getWindow().getDecorView().setSystemUiVisibility(iconColorBlack ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            activity.getWindow().setStatusBarColor(backgroundColor);

            //fitsSystemWindow 为 false, 不预留系统栏位置.
            ViewGroup mContentView = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                mChildView.setFitsSystemWindows(true);
                ViewCompat.requestApplyInsets(mChildView);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //判断是否为小米或魅族手机，如果是则将状态栏文字改为黑色
            if (MIUISetStatusBarLightMode(activity, true) || FlymeSetStatusBarLightMode(activity, true)) {
                //设置状态栏为指定颜色
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0
                    activity.getWindow().setStatusBarColor(backgroundColor);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4
                    //调用修改状态栏颜色的方法
                    setStatusBarColor(activity, backgroundColor);
                }
            }
        }
    }

    /**
     * 改状态栏背景色为backgroundColor，图标为iconColorBlack，并设置沉浸式
     *
     * @param activity
     * @param backgroundColor 背景色
     * @param iconColorBlack  状态栏图标颜色 true黑色 false白色
     */
    public static void setStatusBarImmersiveColor(Activity activity, int backgroundColor, boolean iconColorBlack) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //如果是6.0以上将状态栏文字改为黑色，并设置状态栏颜色
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    (iconColorBlack ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : View.SYSTEM_UI_FLAG_LAYOUT_STABLE));
            activity.getWindow().setStatusBarColor(backgroundColor);

            //fitsSystemWindow 为 false, 不预留系统栏位置.
            ViewGroup mContentView = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                mChildView.setFitsSystemWindows(true);
                ViewCompat.requestApplyInsets(mChildView);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //判断是否为小米或魅族手机，如果是则将状态栏文字改为黑色
            if (MIUISetStatusBarLightMode(activity, true) || FlymeSetStatusBarLightMode(activity, true)) {
                //设置状态栏为指定颜色
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0
                    activity.getWindow().setStatusBarColor(backgroundColor);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4
                    //调用修改状态栏颜色的方法
                    setStatusBarColor(activity, backgroundColor);
                }
            }
        }
    }

    //单纯修改状态栏文字颜色
    public static void setStatusBarIconColorBlack(Activity activity, boolean textColorBlack) {
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                (textColorBlack ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : View.SYSTEM_UI_FLAG_LAYOUT_STABLE));
    }

    /**
     * 改状态栏背景颜色
     *
     * @param activity
     * @param backgroundColor 背景色
     */
    private static void setStatusBarColor(Activity activity, int backgroundColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusLollipop.setStatusBarColor(activity, backgroundColor);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StatusKitKat.setStatusBarColor(activity, backgroundColor);
        }
    }

    /**
     * MIUI的沉浸支持透明白色字体和透明黑色字体
     * https://dev.mi.com/console/doc/detail?pId=1159
     */
    private static boolean MIUISetStatusBarLightMode(Activity activity, boolean darkmode) {
        try {
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");

            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            Class<? extends Window> clazz = activity.getWindow().getClass();
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格，Flyme4.0以上
     */
    private static boolean FlymeSetStatusBarLightMode(Activity activity, boolean darkmode) {
        try {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class
                    .getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (darkmode) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            activity.getWindow().setAttributes(lp);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}