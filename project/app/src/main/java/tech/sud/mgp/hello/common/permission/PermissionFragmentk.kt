package tech.sud.mgp.hello.common.permission

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.AppUtils
import tech.sud.mgp.common.R
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog

/**
 * Description:用于获取权限的fragment
 */
class PermissionFragmentk : Fragment() {

    private var mPermissionSuber: ((Boolean) -> Unit)? = null
    private lateinit var mPermissions: Array<String>
    private var isSuccess = false
    private var isCallbacked = false

    companion object {
        private const val REQUEST_PERMISSION: Int = 0x666

        /**
         * 外部调用此方法进行获取权限
         * @param permissions 需要获取的权限列表
         */
        fun requirePermission(fm: FragmentManager, permissions: Array<String>, suber: ((Boolean) -> Unit)?) {
            if (permissions.isNullOrEmpty()) return
            val fragment = PermissionFragmentk()
            fragment.mPermissionSuber = suber
            fragment.mPermissions = permissions
            try {
                val beginTransaction = fm.beginTransaction()
                beginTransaction.add(fragment, null)
                beginTransaction.commitAllowingStateLoss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        start()
    }

    private fun start() {
        if (::mPermissions.isInitialized) {
            requestPermissions(mPermissions, REQUEST_PERMISSION)
        } else {
            dismiss()
        }
    }

    private fun onDenied() {
        showPermissionDialog(getPermissionName())
    }

    private fun getPermissionName(): String {
        val permissions = LinkedHashSet<String>()
        mPermissions.forEach {
            when (it) {
                Manifest.permission.CAMERA -> {
                    permissions.add(getString(R.string.common_camera))
                }
                Manifest.permission.READ_PHONE_STATE -> {
                    permissions.add(getString(R.string.common_phone))
                }
                Manifest.permission.RECORD_AUDIO -> {
                    permissions.add(getString(R.string.common_record_audio))
                }
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                    permissions.add(getString(R.string.common_storage))
                }
            }
        }
        val sb = StringBuilder()
        permissions.forEach {
            if (sb.isNotEmpty()) {
                sb.append(",")
            }
            sb.append(it)
        }
        if (sb.isEmpty()) {
            sb.append(getString(R.string.common_correlation))
        }
        return sb.toString()
    }

    private fun onNeverAskAgain() {
        showPermissionDialog(getPermissionName())
    }

    private fun showPermissionDialog(permissionName: String) {
        val info = getString(R.string.common_setting_permission_info, permissionName, AppUtils.getAppName())
        val dialog = SimpleChooseDialog(requireContext(), info, getString(R.string.common_cancel), getString(R.string.common_go_setting))
        dialog.setOnChooseListener {
            when (it) {
                0 -> {
                    dismiss()
                }
                1 -> {
                    com.blankj.utilcode.util.PermissionUtils.launchAppDetailsSettings()
                }
            }
        }
        dialog.setOnDestroyListener { dismiss() }
        dialog.show()
    }

    private fun dismiss() {
        try {
            if (!isCallbacked) {
                isCallbacked = true
                mPermissionSuber?.invoke(isSuccess)
            }
            val fm = getParentFragmentManager() ?: return
            val beginTransaction = fm.beginTransaction()
            beginTransaction.remove(this)
            beginTransaction.commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getParentFragmentManager(): FragmentManager? {
        return fragmentManager
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.isEmpty()) { //调用速度快的时候，这里会返回空
                if (::mPermissions.isInitialized && PermissionUtils.hasSelfPermissions(requireContext(), *mPermissions)) {
                    isSuccess = true
                }
                dismiss()
                return
            }
            if (PermissionUtils.verifyPermissions(*grantResults)) {
                isSuccess = true
                dismiss()
            } else {
                if (!PermissionUtils.shouldShowRequestPermissionRationale(this, *mPermissions)) {
                    onNeverAskAgain()
                } else {
                    onDenied()
                }
            }
        }
    }

}

