package tech.sud.mgp.hello.ui.scenes.disco.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.gyf.immersionbar.ImmersionBar;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.base.BaseFragmentStateAdapter;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.ui.common.widget.ViewPager2Helper;
import tech.sud.mgp.hello.ui.scenes.disco.fragment.DiscoRankingFragment;

/**
 * 蹦迪 排行榜页面
 */
public class DiscoRankingActivity extends BaseActivity {

    private MagicIndicator magicIndicator;
    private ViewPager2 viewPager;
    private final List<String> tabs = new ArrayList<>();

    @Override
    protected void setStatusBar() {
        ImmersionBar.with(this).statusBarColor(R.color.transparent).statusBarDarkFont(false).init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_disco_ranking;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        magicIndicator = findViewById(R.id.magic_indicator);
        viewPager = findViewById(R.id.view_pager2);

        View topBar = findViewById(R.id.top_bar);
        int statusBarHeight = ImmersionBar.getStatusBarHeight(this);
        ViewUtils.addMarginTop(topBar, statusBarHeight);
    }

    @Override
    protected void initData() {
        super.initData();
        tabs.add(getString(R.string.focus_king));
        tabs.add(getString(R.string.popular_anchors_list));
        initMagicIndicator();
        initViewPager();
    }

    private void initMagicIndicator() {
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return tabs.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ClipPagerTitleView clipPagerTitleView = new ClipPagerTitleView(context);
                clipPagerTitleView.setText(tabs.get(index));
                clipPagerTitleView.setTextColor(Color.parseColor("#ccffffff"));
                clipPagerTitleView.setClipColor(Color.parseColor("#3f3228"));
                clipPagerTitleView.setTextSize(DensityUtils.sp2px(12));
                clipPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return clipPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                float navigatorHeight = DensityUtils.dp2px(context, 24);
                float borderWidth = 0;
                float lineHeight = navigatorHeight - 2 * borderWidth;
                indicator.setLineHeight(lineHeight);
                indicator.setRoundRadius(DensityUtils.dp2px(context, 12));
                indicator.setColors(Color.parseColor("#fff2e3"));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPager2Helper.bind(magicIndicator, viewPager);
    }

    private void initViewPager() {
        MyAdapter adapter = new MyAdapter(this);
        viewPager.setAdapter(adapter);
        adapter.setDatas(tabs);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.tv_explain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, DiscoRankingExplainActivity.class));
            }
        });
    }

    private static class MyAdapter extends BaseFragmentStateAdapter<String> {

        public MyAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return DiscoRankingFragment.newInstance(position);
        }
    }

}
