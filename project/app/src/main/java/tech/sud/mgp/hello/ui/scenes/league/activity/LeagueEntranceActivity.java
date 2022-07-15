package tech.sud.mgp.hello.ui.scenes.league.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ShapeUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.ui.common.widget.HSTopBar;

/**
 * 联赛场景 入口
 */
public class LeagueEntranceActivity extends BaseActivity {

    private HSTopBar topBar;
    private TextView tvExample;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_league_entrance;
    }

    @Override
    protected void setStatusBar() {
        ImmersionBar.with(this).statusBarColor(R.color.transparent).statusBarDarkFont(false).init();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        topBar = findViewById(R.id.top_bar);
        tvExample = findViewById(R.id.tv_example);

        TextView tvAwardInfo = findViewById(R.id.tv_award_info);
        tvAwardInfo.setBackground(ShapeUtils.createShape(null, (float) DensityUtils.dp2px(20),
                null, GradientDrawable.RECTANGLE, null, Color.parseColor("#4d000000")));

        int statusBarHeight = ImmersionBar.getStatusBarHeight(this);
        ViewUtils.addMarginTop(topBar, statusBarHeight);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        tvExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, LeagueExampleActivity.class));
            }
        });
    }

}
