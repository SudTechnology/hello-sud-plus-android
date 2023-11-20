package tech.sud.mgp.hello.ui.scenes.base.widget.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.jessyan.autosize.AutoSizeCompat;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.service.room.resp.GiftListResp;
import tech.sud.mgp.hello.service.room.resp.GiftListResp.BackGiftModel;
import tech.sud.mgp.hello.ui.scenes.base.model.MonopolyCardGiftModel;

/** 加载大富翁游戏时，展示的卡片View */
public class MonopolyCardContainer extends LinearLayout {

    private View rootView;
    private View topView;
    private TextView tvTitle;
    private ViewPager2 viewPager;
    private MonopolyBannerIndicatorView indicatorView;
    private View viewScale;
    private View viewBg;

    private final MyPagerAdapter pagerAdapter = new MyPagerAdapter();
    private boolean isPutAway; // 是否是收起的

    private List<MonopolyCardPageModel> datas = new ArrayList<>();
    private List<BackGiftModel> danmakuList = new ArrayList<>();

    private OnClickListener mOnCustomClickListener;

    public MonopolyCardContainer(@NonNull Context context) {
        this(context, null);
    }

    public MonopolyCardContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonopolyCardContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MonopolyCardContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
        setListeners();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_monopoly_card_container, this);
        rootView = findViewById(R.id.root_view);
        topView = findViewById(R.id.top_view);
        tvTitle = findViewById(R.id.tv_title);
        viewPager = findViewById(R.id.view_pager2);
        indicatorView = findViewById(R.id.indicator_view);
        viewScale = findViewById(R.id.view_scale);
        viewBg = findViewById(R.id.view_bg);

        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setAdapter(pagerAdapter);
//        viewPager.setUserInputEnabled(false);
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
                    position = position % size;
                    indicatorView.updatePosition(position);

                    MonopolyCardPageModel pageModel = datas.get(position);
                    boolean roleType = isRoleType(pageModel);
                    if (roleType) {
                        tvTitle.setText(R.string.role_gift_card_list);
                    } else {
                        tvTitle.setText(R.string.gift_card_list);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        viewScale.setOnClickListener(v -> {
            isPutAway = !isPutAway;
            if (isPutAway) {
                viewPager.setVisibility(View.GONE);
                indicatorView.setVisibility(View.GONE);
                viewBg.setVisibility(View.GONE);
                viewScale.setRotation(180);
                int paddingBottom = DensityUtils.dp2px(getContext(), 30);
                setPadding(0, 0, 0, paddingBottom);
                topView.setBackgroundResource(R.drawable.shape_monopoly_card_top_shrink);
            } else {
                viewPager.setVisibility(View.VISIBLE);
                indicatorView.setVisibility(View.VISIBLE);
                viewBg.setVisibility(View.VISIBLE);
                viewScale.setRotation(0);
                setPadding(0, 0, 0, 0);
                topView.setBackgroundResource(R.drawable.shape_monopoly_card_top);
            }
        });
    }

    private boolean isRoleType(MonopolyCardPageModel model) {
        if (model.list != null && model.list.size() > 0) {
            return model.list.get(0).getCardType() == 1;
        }
        return false;
    }

    /** 设置数据 */
    @SuppressLint("NotifyDataSetChanged")
    public void setDatas(GiftListResp model) {
        if (model == null || model.giftList == null || model.giftList.size() == 0) {
            datas.clear();
            pagerAdapter.notifyDataSetChanged();
            indicatorView.initIndicator(0);
            return;
        }

        // 整理数据，排序
        List<BackGiftModel> giftList = new ArrayList<>(model.giftList);
        Collections.sort(giftList, new Comparator<BackGiftModel>() {
            @Override
            public int compare(BackGiftModel o1, BackGiftModel o2) {
                if (o1.details != null && o2.details != null) {
                    if (o1.details.cardType != o2.details.cardType) {
                        if (o1.details.cardType == 1) {
                            return 1;
                        }
                        if (o2.details.cardType == 1) {
                            return -1;
                        }
//                        return o2.details.card_type - o1.details.card_type;
                    }
                }
                return 0;
            }
        });

        // 把弹幕内容提取出来
        danmakuList.clear();
        for (BackGiftModel backGiftModel : giftList) {
            if (backGiftModel.details != null && !TextUtils.isEmpty(backGiftModel.details.content)) {
                danmakuList.add(backGiftModel);
            }
        }

        // 处理弹幕数据的分页
        MonopolyCardPageModel pageModel = new MonopolyCardPageModel();
        for (BackGiftModel backGiftModel : danmakuList) {
            MonopolyCardGiftModel monopolyCardGiftModel = new MonopolyCardGiftModel();
            monopolyCardGiftModel.isDanmaku = true;
            monopolyCardGiftModel.giftModel = backGiftModel;
            pageModel.list.add(monopolyCardGiftModel);
            if (pageModel.getFullLineCount() >= 5) {
                pageModel.isGiftCardType = true;
                datas.add(pageModel);
                pageModel = new MonopolyCardPageModel();
            }
        }

        // 处理其他数据的分页
        int lastCardType = -1;
        boolean isFirstModel = true;
        for (BackGiftModel backGiftModel : giftList) {
            if (backGiftModel.details == null) {
                continue;
            }
            if (isFirstModel) {
                isFirstModel = false;
                if (backGiftModel.details.cardType == 1 && pageModel.list.size() > 0) {
                    // 角色卡不和弹幕混在一起，换一页
                    pageModel.isGiftCardType = true;
                    datas.add(pageModel);
                    pageModel = new MonopolyCardPageModel();
                }
                lastCardType = backGiftModel.details.cardType;
            }

            // 判断是否能成为一页了，分两个类型，角色和其他
            if (isGroupCardType(lastCardType, backGiftModel.details.cardType)) {
                if (pageModel.getFullLineCount() >= (lastCardType == 1 ? 3 : 5)) {
                    pageModel.isGiftCardType = lastCardType != 1;
                    datas.add(pageModel);
                    pageModel = new MonopolyCardPageModel();
                }
            } else {
                pageModel.isGiftCardType = lastCardType != 1;
                datas.add(pageModel);
                pageModel = new MonopolyCardPageModel();
            }

            MonopolyCardGiftModel monopolyCardGiftModel = new MonopolyCardGiftModel();
            monopolyCardGiftModel.giftModel = backGiftModel;
            pageModel.list.add(monopolyCardGiftModel);
            lastCardType = backGiftModel.details.cardType;
        }

        if (pageModel.list.size() > 0) {
            pageModel.isGiftCardType = lastCardType != 1;
            datas.add(pageModel);
        }

        pagerAdapter.notifyDataSetChanged();
        indicatorView.initIndicator(datas.size());

        int size = datas.size();
        if (size > 0) {
            indicatorView.updatePosition(viewPager.getCurrentItem() % size);
        }
        startChangeTask();
    }

    // 是否可以归为同一种类型
    private static boolean isGroupCardType(int type1, int type2) {
        int virtualType1 = type1 == 1 ? 1 : 2;
        int virtualType2 = type2 == 1 ? 1 : 2;
        return virtualType1 == virtualType2;
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

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @NonNull
        @Override
        public MyPagerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 部分机型，如鸿蒙3.0.0 存在适配问题，这里重新设置一下
            AutoSizeCompat.autoConvertDensityOfGlobal(getResources());

            MonopolyCardItemView itemView = new MonopolyCardItemView(getContext());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            itemView.setLayoutParams(layoutParams);
            itemView.setOnClickListener(v -> {
                if (mOnCustomClickListener != null) {
                    mOnCustomClickListener.onClick(v);
                }
            });
            return new MyPagerAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyPagerAdapter.MyViewHolder holder, int position) {
            position %= datas.size();
            MonopolyCardPageModel bannerModel = datas.get(position);
            holder.itemView.setDatas(bannerModel);
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
            public MonopolyCardItemView itemView;

            public MyViewHolder(@NonNull MonopolyCardItemView itemView) {
                super(itemView);
                this.itemView = itemView;
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopChangeTask();
    }

    public static class MonopolyCardPageModel {
        public boolean isGiftCardType; // 是否是礼物卡类型
        public List<MonopolyCardGiftModel> list = new ArrayList<>();

        public int getFullLineCount() {
            int lineCount = 0;
            int normalCardCount = 0;
            for (int i = 0; i < list.size(); i++) {
                MonopolyCardGiftModel model = list.get(i);
                if (model.isDanmaku) {
                    lineCount++;
                } else if (model.getCardType() == 1) {
                    lineCount++;
                } else {
                    normalCardCount++;
                }
            }
            lineCount += normalCardCount / 2;
//            lineCount += normalCardCount % 2 > 0 ? 1 : 0;
            return lineCount;
        }
    }

    public List<BackGiftModel> getDanmakuList() {
        return danmakuList;
    }

    public void setCustomClickListener(OnClickListener listener) {
        mOnCustomClickListener = listener;
        viewBg.setOnClickListener(listener);
    }

}
