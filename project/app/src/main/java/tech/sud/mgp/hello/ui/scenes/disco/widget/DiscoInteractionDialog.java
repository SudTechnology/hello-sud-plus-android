package tech.sud.mgp.hello.ui.scenes.disco.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.base.BaseFragmentStateAdapter;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.common.widget.ScaleTransitionPagerTitleView;
import tech.sud.mgp.hello.ui.common.widget.ViewPager2Helper;
import tech.sud.mgp.hello.ui.scenes.disco.model.DiscoInteractionModel;

/**
 * 蹦迪互动弹窗
 */
public class DiscoInteractionDialog extends BaseDialogFragment {

    private MagicIndicator magicIndicator;
    private ViewPager2 viewPager;
    private final List<String> tabs = new ArrayList<>();
    private long roomId;
    private OnActionListener onActionListener;

    public static DiscoInteractionDialog newInstance(long roomId) {
        Bundle args = new Bundle();
        args.putLong("roomId", roomId);
        DiscoInteractionDialog fragment = new DiscoInteractionDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            roomId = arguments.getLong("roomId");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_disco_interaction;
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getHeight() {
        return DensityUtils.dp2px(267);
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setDimAmount(0f);
        window.setWindowAnimations(R.style.BottomToTopAnim);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        magicIndicator = findViewById(R.id.magic_indicator);
        viewPager = findViewById(R.id.view_pager2);
    }

    @Override
    protected void initData() {
        super.initData();
        tabs.add(getString(R.string.disco_interaction_tab_1));
        tabs.add(getString(R.string.disco_interaction_tab_2));
        initMagicIndicator();
        initViewPager();
    }

    private void initMagicIndicator() {
        CommonNavigator commonNavigator = new CommonNavigator(requireContext());
        int paddingHorizontal = DensityUtils.dp2px(requireContext(), 24);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return tabs.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ScaleTransitionPagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setMinScale(0.875f);
                simplePagerTitleView.setText(tabs.get(index));
                simplePagerTitleView.setTextSize(16);
                simplePagerTitleView.setNormalColor(Color.parseColor("#80ffffff"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#ffffff"));
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
                indicator.setLineWidth(DensityUtils.dp2px(context, 20));
                indicator.setLineHeight(DensityUtils.dp2px(context, 2));
//                indicator.setYOffset(DensityUtils.dp2px(context, 5));
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
            DiscoInteractionFragment fragment = DiscoInteractionFragment.newInstance(position, roomId);
            fragment.setOnActionListener(onActionListener);
            return fragment;
        }
    }

    public void setOnActionListener(OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
        if (isAdded()) {
            List<Fragment> fragments = getChildFragmentManager().getFragments();
            for (Fragment fragment : fragments) {
                if (fragment instanceof DiscoInteractionFragment) {
                    DiscoInteractionFragment discoInteractionFragment = (DiscoInteractionFragment) fragment;
                    discoInteractionFragment.setOnActionListener(onActionListener);
                }
            }
        }
    }

    public interface OnActionListener {
        void onAction(DiscoInteractionModel model);
    }

    public void setAnchor(boolean anchor) {
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof DiscoInteractionFragment) {
                DiscoInteractionFragment discoInteractionFragment = (DiscoInteractionFragment) fragment;
                discoInteractionFragment.setAnchor(anchor);
            }
        }
    }

}
