package tech.sud.mgp.hello.ui.common.utils;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import tech.sud.mgp.hello.common.base.BaseDialog;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;

public class DialogUtils {

    /**
     * dialog布局为wrap_content时，在某些手机上，如果页面没有显示或者熄屏的话，会产生无法显示该控件大小的问题
     * 这里监听应用是否在前台显示，如果不在前台，则监听，到了前台之后再进行显示dialog
     */
    public static void safeShowDialog(LifecycleOwner owner, BaseDialog dialog) {
        LifecycleUtils.safeLifecycle(owner, new CompletedListener() {
            @Override
            public void onCompleted() {
                dialog.show();
            }
        });
    }

    /**
     * dialog布局为wrap_content时，在某些手机上，如果页面没有显示或者熄屏的话，会产生无法显示该控件大小的问题
     * 这里监听应用是否在前台显示，如果不在前台，则监听，到了前台之后再进行显示dialog
     */
    public static void safeShowDialog(LifecycleOwner owner, BaseDialogFragment dialog, FragmentManager manager, String tag) {
        LifecycleUtils.safeLifecycle(owner, new CompletedListener() {
            @Override
            public void onCompleted() {
                dialog.show(manager, tag);
            }
        });
    }

}
