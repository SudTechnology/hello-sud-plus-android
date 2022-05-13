package tech.sud.mgp.hello.ui.performance;

import android.content.Context;

import com.blankj.utilcode.util.Utils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.performance.PerformanceDataManager;
import tech.sud.mgp.hello.common.permission.SudPermissionUtils;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;

/** 性能检测管理类 */
public class PerformanceManager {

    private float cpuValue;
    private float memoryValue;
    private float fpsValue;
    private PerformanceFloatingView floatingView;

    private static class Holder {
        private static final PerformanceManager INSTANCE = new PerformanceManager();
    }

    private PerformanceManager() {
        init();
    }

    public static PerformanceManager getInstance() {
        return PerformanceManager.Holder.INSTANCE;
    }

    private void init() {
        PerformanceDataManager manager = PerformanceDataManager.getInstance();
        manager.init();
        manager.setCallback(new PerformanceDataManager.Callback() {
            @Override
            public void onChange(float value, int performanceType) {
                if (performanceType == PerformanceDataManager.PERFORMANCE_TYPE_CPU) {
                    cpuValue = value;
                } else if (performanceType == PerformanceDataManager.PERFORMANCE_TYPE_MEMORY) {
                    memoryValue = value;
                } else {
                    fpsValue = value;
                }
                updateContent();
            }
        });
    }

    private void startMonitor() {
        PerformanceDataManager manager = PerformanceDataManager.getInstance();
        manager.startMonitorCPUInfo();
        manager.startMonitorMemoryInfo();
        manager.startMonitorFrameInfo();
        showFloatingView();
    }

    private void stopMonitor() {
        PerformanceDataManager manager = PerformanceDataManager.getInstance();
        manager.stopMonitorCPUInfo();
        manager.stopMonitorMemoryInfo();
        manager.stopMonitorFrameInfo();
        dismissFloatingView();
    }

    /** 开始检测 */
    public void start(Context context) {
        if (SudPermissionUtils.checkFloatPermission(context)) {
            startMonitor();
        } else {
            SimpleChooseDialog dialog = new SimpleChooseDialog(context,
                    context.getString(R.string.test_floating_permission),
                    context.getString(R.string.test_i_have_set),
                    context.getString(R.string.go_setting));
            dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
                @Override
                public void onChoose(int index) {
                    if (index == 0) {
                        if (SudPermissionUtils.checkFloatPermission(context)) {
                            startMonitor();
                        }
                    } else if (index == 1) {
                        SudPermissionUtils.setFloatPermission(context);
                    }
                }
            });
            dialog.show();
        }
    }

    /** 展示悬浮窗 */
    private void showFloatingView() {
        if (floatingView != null) return;
        floatingView = new PerformanceFloatingView(Utils.getApp());
        floatingView.show();
        floatingView.setShutdownListener(v -> stopMonitor());
    }

    /** 移除悬浮窗 */
    private void dismissFloatingView() {
        if (floatingView == null) return;
        floatingView.dismiss();
        floatingView = null;
    }

    /** 更新内容 */
    private void updateContent() {
        if (floatingView == null) return;
        String cpuContent = "cpu:" + cpuValue + "%";
        String memoryContent = "mem:" + ((int) memoryValue) + "M";
        String fpsContent = "fps:" + ((int) fpsValue);
        String content = fpsContent + "\n" + cpuContent + "\n" + memoryContent;
        floatingView.setContent(content);
    }

}
