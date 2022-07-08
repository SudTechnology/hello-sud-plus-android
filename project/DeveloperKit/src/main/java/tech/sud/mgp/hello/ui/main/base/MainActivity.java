package tech.sud.mgp.hello.ui.main.base;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.KeyboardUtils;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.ui.main.home.HomeFragment;

/**
 * 主页
 */
public class MainActivity extends BaseActivity implements MainTabView.TabClickListener {

    private ViewPager2 viewPager;
    private LinearLayout tabLayout;
    private MainTabView currentTabView;
    private final List<TabModel> tabs = new ArrayList<>();
    private final List<MainTabView> tabViews = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tab_layout);
        View view = viewPager.getChildAt(0);
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        viewPager.setSaveEnabled(false);
        viewPager.setUserInputEnabled(false);
//        PerformanceManager.getInstance().start(this);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        initTabs();
    }

    /**
     * 初始化Tab数据
     */
    private void initTabs() {
        tabs.add(new TabModel(0, getString(R.string.tabs_index), R.drawable.icon_home_index));
        tabs.add(new TabModel(1, getString(R.string.tabs_room), R.drawable.icon_home_room));
        tabs.add(new TabModel(2, getString(R.string.tabs_set), R.drawable.icon_home_set));
        for (int i = 0; i < tabs.size(); i++) {
            MainTabView tabView = new MainTabView(this);
            tabView.setData(tabs.get(i));
            tabView.setTabClickListener(this);
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
    public void onChangePage(int index) {
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
                    return new HomeFragment();
                }
                case 1: {
                    return new HomeFragment();
                }
                default: {
                    return new HomeFragment();
                }
            }
        }

        @Override
        public int getItemCount() {
            return tabs.size();
        }
    }

    // region 点击屏幕空白区域隐藏软键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                KeyboardUtils.hideSoftInput(this);
                return false;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // Return whether touch the view.
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (!KeyboardUtils.isSoftInputVisible(this)) return false;
        if ((v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationOnScreen(l);
            int left = l[0];
            int top = l[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            float rawX = event.getRawX();
            float rawY = event.getRawY();
            return !(rawX > left && rawX < right && rawY > top && rawY < bottom);
        }
        return false;
    }
    // endregion 点击屏幕空白区域隐藏软键盘

}
