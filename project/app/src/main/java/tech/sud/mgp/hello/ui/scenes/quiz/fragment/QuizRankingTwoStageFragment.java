package tech.sud.mgp.hello.ui.scenes.quiz.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.gyf.immersionbar.ImmersionBar;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.base.BaseFragmentStateAdapter;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.ui.common.widget.ViewPager2Helper;

/**
 * 竞猜排行榜页面
 */
public class QuizRankingTwoStageFragment extends BaseFragment {

    private int index;
    private MagicIndicator magicIndicator;
    private ViewPager2 viewPager;
    private final List<String> tabs = new ArrayList<>();

    public static QuizRankingTwoStageFragment newInstance(int index) {
        QuizRankingTwoStageFragment fragment = new QuizRankingTwoStageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            index = arguments.getInt("index", 0);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_quiz_ranking_two_stage;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        magicIndicator = findViewById(R.id.magic_indicator);
        viewPager = findViewById(R.id.view_pager2);
        ViewUtils.addMarginTop(magicIndicator, ImmersionBar.getStatusBarHeight(this));
    }

    @Override
    protected void initData() {
        super.initData();
        tabs.add(getString(R.string.ranking_of_day));
        tabs.add(getString(R.string.ranking_of_week));
        initMagicIndicator();
        initViewPager();
    }

    private void initMagicIndicator() {
        CommonNavigator commonNavigator = new CommonNavigator(requireContext());
        int paddingHorizontal = DensityUtils.dp2px(requireContext(), 15);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return tabs.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(tabs.get(index));
                simplePagerTitleView.setNormalColor(Color.parseColor("#ccffffff"));
                simplePagerTitleView.setSelectedColor(Color.WHITE);
                simplePagerTitleView.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
                simplePagerTitleView.setTextSize(12);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(Color.WHITE);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineWidth(DensityUtils.dp2px(context, 8));
                indicator.setLineHeight(DensityUtils.dp2px(context, 2));
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

        public MyAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return QuizRankingFragment.newInstance(index, position);
        }
    }

}
