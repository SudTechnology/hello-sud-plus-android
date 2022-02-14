package tech.sud.mgp.hello.common.permission

import android.content.Context
import androidx.fragment.app.FragmentManager

object DtPermissionUtils {

    /**
     * 请求运行时权限
     * @param suber 回调，true表示拿到了权限，false表示没有
     */
    fun requirePermission(fm: FragmentManager, permissions: Array<String>, suber: ((Boolean) -> Unit)?) {
        PermissionFragmentk.requirePermission(fm, permissions, suber)
    }

    /**
     * 请求运行时权限
     * @param suber 回调，true表示拿到了权限，false表示没有
     */
    fun requirePermission(context: Context, fm: FragmentManager, permissions: Array<String>, suber: ((Boolean) -> Unit)?) {
        if (PermissionUtils.hasSelfPermissions(context, *permissions)) {
            suber?.invoke(true)
            return
        }
        PermissionFragmentk.requirePermission(fm, permissions, suber)
    }

}