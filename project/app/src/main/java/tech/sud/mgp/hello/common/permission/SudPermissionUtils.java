package tech.sud.mgp.hello.common.permission;

import android.content.Context;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * 权限申请
 */
public class SudPermissionUtils {

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
