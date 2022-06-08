package tech.sud.mgp.hello.ui.scenes.quiz.activity;

import android.content.Context;
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
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.base.BaseFragmentStateAdapter;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.ui.common.widget.ViewPager2Helper;
import tech.sud.mgp.hello.ui.scenes.quiz.fragment.QuizRankingTwoStageFragment;
import tech.sud.mgp.hello.ui.scenes.quiz.widget.CustomLinePagerIndicator;

/**
 * 竞猜排行榜页面
 */
public class QuizRankingActivity extends BaseActivity {

    private MagicIndicator magicIndicator;
    private ViewPager2 viewPager;
    private final List<String> tabs = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_quiz_ranking;
    }

    @Override
    protected void setStatusBar() {
        ImmersionBar.with(this).statusBarColor(R.color.transparent).statusBarDarkFont(false).init();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        magicIndicator = findViewById(R.id.magic_indicator);
        viewPager = findViewById(R.id.view_pager2);
        View topBar = findViewById(R.id.top_bar);
        int statusBarHeight = ImmersionBar.getStatusBarHeight(this);
        ViewUtils.addMarginTop(magicIndicator, statusBarHeight);
        ViewUtils.addMarginTop(topBar, statusBarHeight);
    }

    @Override
    protected void initData() {
        super.initData();
        tabs.add(getString(R.string.ranking_of_quiz));
        tabs.add(getString(R.string.ranking_of_big_god));
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
                clipPagerTitleView.setTextColor(Color.WHITE);
                clipPagerTitleView.setClipColor(Color.parseColor("#1a1a1a"));
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
                CustomLinePagerIndicator indicator = new CustomLinePagerIndicator(context);
                float navigatorHeight = DensityUtils.dp2px(context, 36);
                float borderWidth = 0;
                float lineHeight = navigatorHeight - 2 * borderWidth;
                indicator.setLineHeight(lineHeight);
                indicator.setRoundRadius(DensityUtils.dp2px(context, 4));
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

    private class MyAdapter extends BaseFragmentStateAdapter<String> {

        public MyAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return QuizRankingTwoStageFragment.newInstance(position);
        }
    }

}
