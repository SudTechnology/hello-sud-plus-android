package tech.sud.mgp.hello.ui.scenes.ad.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.resp.GiAdModel;
import tech.sud.mgp.hello.ui.scenes.ad.fragment.CocosGameFragment;

public class AdRoomActivity extends BaseActivity {

    private ViewPager2 mViewPager2;
    private List<GiAdModel> mDatas = new ArrayList<>();
    private MyAdapter mAdapter;
    private boolean isFirstPageCompleted;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ad_room;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mViewPager2 = findViewById(R.id.view_pager2);
        mViewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
    }

    @Override
    protected void initData() {
        super.initData();
        mAdapter = new MyAdapter(this);
        mViewPager2.setAdapter(mAdapter);
        initAdList();
    }

    private void initAdList() {
        RoomRepository.getGiAdConfig(this, new Observer<List<GiAdModel>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
            }

            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<GiAdModel> giAdModels) {
                refreshList(giAdModels);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                ThreadUtils.runOnUiThreadDelayed(() -> initAdList(), 5000);
            }

            @Override
            public void onComplete() {
            }
        });
    }

    private void refreshList(List<GiAdModel> list) {
        mDatas.clear();
        if (list != null) {
            mDatas.addAll(list);
        }
        mAdapter.notifyDataSetChanged();
        mViewPager2.postDelayed(() -> firstPageGameStarted(), 5000);
    }

    private class MyAdapter extends FragmentStateAdapter {
        public MyAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            int srcPosition = position;
            position %= mDatas.size();
            GiAdModel giAdModel = mDatas.get(position);
            return CocosGameFragment.newInstance(srcPosition, giAdModel.gameInfo);
        }

        @Override
        public int getItemCount() {
            if (mDatas.size() == 0) {
                return 0;
            }
            return Integer.MAX_VALUE;
        }
    }

    @Override
    protected void setStatusBar() {
        ImmersionBar.with(this).statusBarDarkFont(false).init();
    }

    public void setUserInputEnabled(boolean enabled) {
        mViewPager2.setUserInputEnabled(enabled);
    }

    public void firstPageGameStarted() {
        if (isFirstPageCompleted) {
            return;
        }
        isFirstPageCompleted = true;
        mViewPager2.post(() -> {
//            mViewPager2.setOffscreenPageLimit(2);
//            mAdapter.notifyDataSetChanged();
        });
        LogUtils.d("firstPageGameStarted");
    }

}
