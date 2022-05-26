package tech.sud.mgp.hello.common.utils.permission;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * 权限申请
 */
public class SudPermissionUtils {

    /**
     * 检测其是否有悬浮窗权限
     *
     * @return true 表示有权限
     */
    public static boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        } else { //23以下的版本不需要动态权限
            return true;
        }
    }

    /** 跳到系统设置当中去设置悬浮窗权限 */
    public static void setFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Intent intent = getWindowPermissionIntent(context);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** 获取悬浮窗权限设置界面意图 */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static Intent getWindowPermissionIntent(Context context) {
        // 在 Android 11 上面不能加包名跳转，因为就算加了也没有效果
        // 反馈在某些系统上会出现崩溃的情况
        // https://bugly.qq.com/v2/crash-reporting/crashes/61382e3799/74006?pid=1
        // https://developer.android.google.cn/reference/android/provider/Settings#ACTION_MANAGE_OVERLAY_PERMISSION
        try {
            return new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
        } catch (Exception e) {
            return getApplicationDetailsIntent();
        }
    }

    /** 获取应用详情界面意图 */
    private static Intent getApplicationDetailsIntent() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package::${context.packageName}"));
        return intent;
    }

    /** 请求权限 */
    public static void requirePermission(Context context, FragmentManager fm, String[] permissions, PermissionFragment.OnPermissionListener listener) {
        if (PermissionUtils.hasSelfPermissions(context, permissions)) {
            listener.onPermission(true);
            return;
        }
        requirePermission(fm, permissions, listener);
    }

    private static void requirePermission(FragmentManager fm, String[] permissions, PermissionFragment.OnPermissionListener listener) {
        PermissionFragment fragment = PermissionFragment.newInstance(permissions, listener);
        try {
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.add(fragment, null);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
