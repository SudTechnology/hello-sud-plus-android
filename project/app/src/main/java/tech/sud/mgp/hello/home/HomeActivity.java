package tech.sud.mgp.hello.home;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.common.base.BaseActivity;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.home.callback.TabClickCallback;
import tech.sud.mgp.hello.home.fragment.IndexFragment;
import tech.sud.mgp.hello.home.fragment.RoomListFragment;
import tech.sud.mgp.hello.home.fragment.SetFragment;
import tech.sud.mgp.hello.home.model.TabModel;
import tech.sud.mgp.hello.home.view.HomeTabView;

public class HomeActivity extends BaseActivity implements TabClickCallback {

    private ViewPager2 viewPager;
    private LinearLayout tabLayout;
    private HomeTabView currentTabView;
    private List<TabModel> tabs = new ArrayList<TabModel>();
    private List<HomeTabView> tabViews = new ArrayList<HomeTabView>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tab_layout);
        initTabs();
    }

    /**
     * 初始化Tab数据
     */
    private void initTabs() {
        tabs.add(new TabModel(0, getString(R.string.tabs_index), R.mipmap.icon_home_index));
        tabs.add(new TabModel(1, getString(R.string.tabs_room), R.mipmap.icon_home_room));
        tabs.add(new TabModel(2, getString(R.string.tabs_set), R.mipmap.icon_home_set));
        for (int i = 0; i < tabs.size(); i++) {
            HomeTabView tabView = new HomeTabView(this);
            tabView.setData(tabs.get(i));
            tabView.setTabClickCallback(this);
            if (i == 0) {
                tabView.setViewState(true);
                currentTabView = tabView;
            } else {
                tabView.setViewState(false);
            }
            tabLayout.addView(tabView, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            tabViews.add(tabView);
        }
        HomeTabAdapter adapter = new HomeTabAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(tabs.size());
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (currentTabView != null) {
                    currentTabView.setViewState(false);
                }
                tabViews.get(position).setViewState(true);
                currentTabView = tabViews.get(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        viewPager.setCurrentItem(0);
    }

    @Override
    public void changePage(int index) {
        viewPager.setCurrentItem(index);
    }

    class HomeTabAdapter extends FragmentStateAdapter {
        public HomeTabAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: {
                    return IndexFragment.newInstance();
                }
                case 1: {
                    return RoomListFragment.newInstance();
                }
                default: {
                    return SetFragment.newInstance();
                }
            }
        }

        @Override
        public int getItemCount() {
            return tabs.size();
        }
    }
}