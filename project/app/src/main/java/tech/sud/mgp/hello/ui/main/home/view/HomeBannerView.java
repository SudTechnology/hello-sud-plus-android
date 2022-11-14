package tech.sud.mgp.hello.ui.main.home.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

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
    private ViewPager2 viewPager;
    private List<GetBannerResp.BannerModel> datas;
    private int mPosition;
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
        viewPager = new ViewPager2(getContext());
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
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
        if (datas == null || datas.size() < 2) {
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
            mPosition++;
            viewPager.setCurrentItem(mPosition);
            startChangeTask();
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean dispatch = super.dispatchTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (dispatch) {
                    stopChangeTask();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startChangeTask();
                break;
        }
        return dispatch;
    }

    private class MyPagerAdapter extends RecyclerView.Adapter<MyPagerAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyPagerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RoundImageView roundImageView = new RoundImageView(getContext());
            roundImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            roundImageView.setRadius(DensityUtils.dp2px(8));
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            roundImageView.setLayoutParams(layoutParams);
            return new MyViewHolder(roundImageView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyPagerAdapter.MyViewHolder holder, int position) {
            position %= datas.size();
            GetBannerResp.BannerModel bannerModel = datas.get(position);
            ImageLoader.loadImage(holder.imageView, bannerModel.image);
            holder.bannerModel = bannerModel;
        }

        @Override
        public int getItemCount() {
            if (datas != null) {
                if (datas.size() > 1) {
                    return Integer.MAX_VALUE;
                } else {
                    return datas.size();
                }
            }
            return 0;
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public GetBannerResp.BannerModel bannerModel;

            public MyViewHolder(@NonNull RoundImageView itemView) {
                super(itemView);
                imageView = itemView;
                imageView.setOnClickListener((v) -> {
                    if (onPagerClickListener != null) {
                        onPagerClickListener.onPagerClick(bannerModel);
                    }
                });
            }

        }
    }

    public void setOnPagerClickListener(OnPagerClickListener onPagerClickListener) {
        this.onPagerClickListener = onPagerClickListener;
    }

    public interface OnPagerClickListener {
        void onPagerClick(GetBannerResp.BannerModel model);
    }

}
