package tech.sud.mgp.hello.ui.main.settings.activity;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.AppUtils;

import tech.sud.gip.core.SudGIP;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.utils.DensityUtils;

/**
 * 版本信息页面
 */
public class VersionInfoActivity extends BaseActivity {

    private LinearLayout containerVersion;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_version_info;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        containerVersion = findViewById(R.id.container_version);
    }

    @Override
    protected void initData() {
        super.initData();
        addVersionItem("HelloSud App", "V" + AppUtils.getAppVersionName() + "." + AppUtils.getAppVersionCode());
        addLine();
        addVersionItem("SudGIP SDK", "V" + SudGIP.getVersion());
//        addLine();
//        addVersionItem("Zego SDK", "V" + ZegoExpressEngine.getVersion());
//        addLine();
//        addVersionItem("Agora SDK", "V" + RtcEngine.getSdkVersion());
    }

    private void addLine() {
        View view = new View(this);
        view.setBackgroundResource(R.color.c_dddddd);
        containerVersion.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, DensityUtils.dp2px(this, 0.5f));
    }

    private void addVersionItem(String name, String version) {
        // 容器
        FrameLayout frameLayout = new FrameLayout(this);
        containerVersion.addView(frameLayout, LinearLayout.LayoutParams.MATCH_PARENT, DensityUtils.dp2px(this, 56));

        // 名称
        TextView tvName = new TextView(this);
        tvName.setTextSize(16);
        tvName.setTextColor(ContextCompat.getColor(this, R.color.c_1a1a1a));
        tvName.setText(name);
        FrameLayout.LayoutParams nameParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        nameParams.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
        frameLayout.addView(tvName, nameParams);

        // 版本
        TextView tvVersion = new TextView(this);
        tvVersion.setTextSize(14);
        tvVersion.setTextColor(ContextCompat.getColor(this, R.color.c_8a8a8e));
        tvVersion.setText(version);
        FrameLayout.LayoutParams versionParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        versionParams.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
        frameLayout.addView(tvVersion, versionParams);
    }

}
