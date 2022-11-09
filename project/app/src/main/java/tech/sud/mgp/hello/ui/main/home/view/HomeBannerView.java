package tech.sud.mgp.hello.ui.main.home.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import tech.sud.mgp.core.view.RoundImageView;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.main.resp.GetBannerResp;

/**
 * 首页轮播图的View
 */
public class HomeBannerView extends ConstraintLayout {

    private MyPagerAdapter pagerAdapter = new MyPagerAdapter();
    private ViewPager viewPager;
    private List<GetBannerResp.BannerModel> datas;
    private int position;
    private OnPagerClickListener onPagerClickListener;

    public HomeBannerView(@NonNull Context context) {
        this(context, null);
    }

    public HomeBannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeBannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public HomeBannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        viewPager = new ViewPager(getContext());
        addView(viewPager, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        viewPager.setAdapter(pagerAdapter);
    }

    public void setBannerInfo(GetBannerResp resp) {
        if (resp != null) {
            datas = resp.bannerInfoList;
        }
        pagerAdapter.notifyDataSetChanged();
        startChangeTask();
    }

    public void startChangeTask() {
        stopChangeTask();
        if (datas == null || datas.size() == 0) {
            return;
        }
        postDelayed(changeTask, 3000);
    }

    public void stopChangeTask() {
        removeCallbacks(changeTask);
    }

    private final Runnable changeTask = new Runnable() {
        @Override
        public void run() {
            position++;
            viewPager.setCurrentItem(position);
            startChangeTask();
        }
    };

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (datas != null && datas.size() > 0) {
                return Integer.MAX_VALUE;
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            int size = datas.size();
            if (position >= size) {
                position = position % size;
            }
            RoundImageView roundImageView = new RoundImageView(getContext());
            roundImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            roundImageView.setRadius(DensityUtils.dp2px(8));
            GetBannerResp.BannerModel bannerModel = datas.get(position);
            ImageLoader.loadImage(roundImageView, bannerModel.image);
            container.addView(roundImageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            roundImageView.setOnClickListener((v) -> {
                if (onPagerClickListener != null) {
                    onPagerClickListener.onPagerClick(bannerModel);
                }
            });
            return roundImageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public void setOnPagerClickListener(OnPagerClickListener onPagerClickListener) {
        this.onPagerClickListener = onPagerClickListener;
    }

    public interface OnPagerClickListener {
        void onPagerClick(GetBannerResp.BannerModel model);
    }

}
