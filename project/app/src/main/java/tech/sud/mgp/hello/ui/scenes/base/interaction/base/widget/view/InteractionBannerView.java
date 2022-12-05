package tech.sud.mgp.hello.ui.scenes.base.interaction.base.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import tech.sud.mgp.core.view.RoundImageView;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.ui.scenes.base.interaction.base.model.InteractionGameModel;

/**
 * 互动游戏 轮播图
 */
public class InteractionBannerView extends LinearLayout {

    private final MyPagerAdapter pagerAdapter = new MyPagerAdapter();
    private final ViewPager2 viewPager = new ViewPager2(getContext());
    private final InteractionBannerIndicatorView indicatorView = new InteractionBannerIndicatorView(getContext());
    private List<InteractionGameModel> datas;
    private OnPagerClickListener onPagerClickListener;

    public InteractionBannerView(@NonNull Context context) {
        this(context, null);
    }

    public InteractionBannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InteractionBannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public InteractionBannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
        setListeners();
    }

    private void initView() {
        setOrientation(VERTICAL);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        int width = DensityUtils.dp2px(getContext(), 80);
        addView(viewPager, width, DensityUtils.dp2px(getContext(), 110));
        viewPager.setAdapter(pagerAdapter);

        LayoutParams indicatorParams = new LayoutParams(width, DensityUtils.dp2px(getContext(), 6));
        indicatorParams.topMargin = DensityUtils.dp2px(getContext(), 3);
        addView(indicatorView, indicatorParams);
    }

    private void setListeners() {
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int size = datas.size();
                if (size > 0) {
                    indicatorView.updatePosition(position % size);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    /** 设置数据 */
    public void setDatas(List<InteractionGameModel> list) {
        if (list != null) {
            datas = list;
        }
        pagerAdapter.notifyDataSetChanged();
        startChangeTask();

        indicatorView.initIndicator(getIndicatorCount());

        int size = datas.size();
        if (size > 0) {
            indicatorView.updatePosition(viewPager.getCurrentItem() % size);
        }
    }

    public int getIndicatorCount() {
        if (datas != null) {
            return datas.size();
        }
        return 0;
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
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
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
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RoundImageView roundImageView = new RoundImageView(getContext());
            roundImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            roundImageView.setRadius(DensityUtils.dp2px(8));
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            roundImageView.setLayoutParams(layoutParams);
            return new MyViewHolder(roundImageView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            position %= datas.size();
            InteractionGameModel bannerModel = datas.get(position);
            ImageLoader.loadDrawable(holder.imageView, bannerModel.iconResId);
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
            public InteractionGameModel bannerModel;

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
        void onPagerClick(InteractionGameModel model);
    }

}
