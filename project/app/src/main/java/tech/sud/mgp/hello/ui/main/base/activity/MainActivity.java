package tech.sud.mgp.hello.ui.main.base.activity;

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
import com.blankj.utilcode.util.ThreadUtils;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.BaseConfigResp;
import tech.sud.mgp.hello.ui.common.utils.channel.NotifyChannelHelper;
import tech.sud.mgp.hello.ui.main.base.constant.GameIdCons;
import tech.sud.mgp.hello.ui.main.home.HomeFragment;
import tech.sud.mgp.hello.ui.main.roomlist.RoomListFragment;
import tech.sud.mgp.hello.ui.main.settings.fragment.SettingsFragment;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;

/**
 * 首页
 */
public class MainActivity extends BaseActivity implements MainTabView.TabClickListener {

    private ViewPager2 viewPager;
    private LinearLayout tabLayout;
    private MainTabView currentTabView;
    private final List<TabModel> tabs = new ArrayList<>();
    private final List<MainTabView> tabViews = new ArrayList<>();
    private AppGameViewModel gameViewModel = new AppGameViewModel();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void setStatusBar() {
        ImmersionBar.with(this).statusBarColor(R.color.transparent).statusBarDarkFont(true).init();
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
    }

    @Override
    protected void initData() {
        super.initData();
        new NotifyChannelHelper().initChannel(this);
        getBaseConfig();
        gameViewModel.preloadMG(this, GameIdCons.CUSTOM_ROCKET);
    }

    private void getBaseConfig() {
        HomeRepository.getBaseConfig(this, new RxCallback<BaseConfigResp>() {
            @Override
            public void onNext(BaseResponse<BaseConfigResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    AppData.getInstance().setBaseConfigResp(t.getData());
                } else {
                    delayGetBaseConfig();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                delayGetBaseConfig();
            }
        });
    }

    private void delayGetBaseConfig() {
        ThreadUtils.runOnUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                if (isDestroyed()) {
                    return;
                }
                getBaseConfig();
            }
        }, 3000);
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
        tabs.add(new TabModel(2, getString(R.string.mine), R.drawable.icon_mine_tabbar_select_24));
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
                    return HomeFragment.newInstance();
                }
                case 1: {
                    return RoomListFragment.newInstance();
                }
                default: {
                    return SettingsFragment.newInstance();
                }
            }
        }

        @Override
        public int getItemCount() {
            return tabs.size();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                KeyboardUtils.hideSoftInput(v);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

}