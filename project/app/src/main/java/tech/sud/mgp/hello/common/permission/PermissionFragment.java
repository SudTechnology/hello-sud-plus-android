package tech.sud.mgp.hello.common.permission;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.blankj.utilcode.util.AppUtils;

import java.util.LinkedHashSet;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialog;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;

public class PermissionFragment extends Fragment {

    private OnPermissionListener mOnPermissionListener;
    private String[] mPermissions;
    private static final int REQUEST_PERMISSION = 0x666;
    private boolean isSuccess;
    private boolean isCallbacked;

    public static PermissionFragment newInstance(String[] permissions, OnPermissionListener listener) {
        PermissionFragment fragment = new PermissionFragment();
        fragment.mPermissions = permissions;
        fragment.mOnPermissionListener = listener;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startCheck();
    }

    private void startCheck() {
        if (mPermissions == null || mPermissions.length == 0) {
            dismiss();
        } else {
            requestPermissions(mPermissions, REQUEST_PERMISSION);
        }
    }

    private void dismiss() {
        try {
            if (!isCallbacked) {
                isCallbacked = true;
                callbackResult();
            }
            FragmentManager fm = getFragmentManager();
            if (fm != null) {
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.remove(this);
                fragmentTransaction.commitAllowingStateLoss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callbackResult() {
        OnPermissionListener listerner = mOnPermissionListener;
        if (listerner != null) {
            listerner.onPermission(isSuccess);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (mPermissions == null || mPermissions.length == 0) {
                dismiss();
                return;
            }
            if (grantResults.length == 0) {
                if (PermissionUtils.hasSelfPermissions(getContext(), mPermissions)) {
                    isSuccess = true;
                }
                dismiss();
                return;
            }
            if (PermissionUtils.verifyPermissions(grantResults)) {
                isSuccess = true;
                dismiss();
            } else {
                if (PermissionUtils.shouldShowRequestPermissionRationale(this, mPermissions)) {
                    onDenied();
                } else {
                    onNeverAskAgain();
                }
            }
        }
    }

    private void onDenied() {
        showPermissionDialog(getPermissionName());
    }

    private void onNeverAskAgain() {
        showPermissionDialog(getPermissionName());
    }

    private String getPermissionName() {
        LinkedHashSet<String> permissions = new LinkedHashSet<>();
        for (String permission : mPermissions) {
            switch (permission) {
                case Manifest.permission.CAMERA:
                    permissions.add(getString(R.string.common_camera));
                    break;
                case Manifest.permission.READ_PHONE_STATE:
                    permissions.add(getString(R.string.common_phone));
                    break;
                case Manifest.permission.RECORD_AUDIO:
                    permissions.add(getString(R.string.common_record_audio));
                    break;
                case Manifest.permission.READ_EXTERNAL_STORAGE:
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    permissions.add(getString(R.string.common_storage));
                    break;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String permission : permissions) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(permission);
        }
        if (sb.length() == 0) {
            sb.append(getString(R.string.common_correlation));
        }
        return sb.toString();
    }

    private void showPermissionDialog(String permissionName) {
        String info = getString(R.string.common_setting_permission_info, permissionName, AppUtils.getAppName());
        SimpleChooseDialog dialog = new SimpleChooseDialog(requireContext(), info,
                getString(R.string.common_cancel),
                getString(R.string.common_go_setting));
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                if (index == 0) {
                    dismiss();
                } else if (index == 1) {
                    com.blankj.utilcode.util.PermissionUtils.launchAppDetailsSettings();
                }
                dialog.dismiss();
            }
        });
        dialog.setOnDestroyListener(new BaseDialog.OnDestroyListener() {
            @Override
            public void onDestroy() {
                dismiss();
            }
        });
        dialog.show();
    }

    public void setOnPermissionListener(OnPermissionListener listener) {
        mOnPermissionListener = listener;
    }

    public interface OnPermissionListener {
        void onPermission(boolean success);
    }

}
