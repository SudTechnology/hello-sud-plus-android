package tech.sud.mgp.hello.ui.scenes.danmaku.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseProviderMultiAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.widget.adapter.EmptyProvider;
import tech.sud.mgp.hello.service.room.resp.DanmakuListResp;

/**
 * 弹幕场景内的弹幕列表
 */
public class DanmakuListView extends ConstraintLayout {

    private RecyclerView recyclerView = new RecyclerView(getContext());
    private MyAdapter adapter = new MyAdapter();
    private DanmakuOnClickListener danmakuOnClickListener;

    public DanmakuListView(@NonNull Context context) {
        this(context, null);
    }

    public DanmakuListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DanmakuListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DanmakuListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
        setListeners();
    }

    private void initView() {
        setBackgroundColor(Color.parseColor("#000000"));
//        inflate(getContext(), R.layout.view_danmaku_list, this);
//        recyclerView = findViewById(R.id.recycler_view);
        addView(recyclerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void setListeners() {
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter, @NonNull View view, int position) {
                Object item = adapter.getItem(position);
                if (item instanceof DanmakuListResp.Prop) {
                    if (danmakuOnClickListener != null) {
                        danmakuOnClickListener.onClickProp((DanmakuListResp.Prop) item);
                    }
                }
            }
        });
    }

    /** 设置数据集 */
    public void setDatas(DanmakuListResp resp) {
        List<Object> list = new ArrayList<>();
        if (resp != null) {
            if (resp.joinTeamList != null) {
                list.add(resp.joinTeamList);
            }
            if (resp.propList != null) {
                list.addAll(resp.propList);
            }
        }
        adapter.setList(list);
    }

    /** 设置点击监听器 */
    public void setDanmakuOnClickListener(DanmakuOnClickListener danmakuOnClickListener) {
        this.danmakuOnClickListener = danmakuOnClickListener;
    }

    private class MyAdapter extends BaseProviderMultiAdapter<Object> {

        public static final int TEAM_ITEM = 1;
        public static final int PROP_ITEM = 2;

        public MyAdapter() {
            addItemProvider(new TeamItemProvider());
            addItemProvider(new PropProvider());
            addItemProvider(new EmptyProvider());
        }

        @Override
        protected int getItemType(@NonNull List<?> list, int position) {
            Object item = list.get(position);
            if (item instanceof List) {
                return TEAM_ITEM;
            } else if (item instanceof DanmakuListResp.Prop) {
                return PROP_ITEM;
            }
            return EmptyProvider.TYPE_EMPTY;
        }
    }

    private class TeamItemProvider extends BaseItemProvider<Object> {

        @Override
        public int getItemViewType() {
            return MyAdapter.TEAM_ITEM;
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_danmaku_team;
        }

        @Override
        public void convert(@NonNull BaseViewHolder holder, Object o) {
            List<DanmakuListResp.JoinTeam> list = (List<DanmakuListResp.JoinTeam>) o;
            RecyclerView recyclerView = holder.getView(R.id.recycler_view);
            RecyclerView.Adapter recyclerViewAdapter = recyclerView.getAdapter();
            if (recyclerViewAdapter instanceof TeamItemAdapter) {
                ((TeamItemAdapter) recyclerViewAdapter).setList(list);
            } else {
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.HORIZONTAL, false));
                TeamItemAdapter adapter = new TeamItemAdapter();
                recyclerView.setAdapter(adapter);
                adapter.setList(list);
                adapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter, @NonNull View view, int position) {
                        if (danmakuOnClickListener != null) {
                            danmakuOnClickListener.onClickTeam(adapter.getItem(position));
                        }
                    }
                });
            }
        }

        private class TeamItemAdapter extends BaseQuickAdapter<DanmakuListResp.JoinTeam, BaseViewHolder> {

            public TeamItemAdapter() {
                super(R.layout.item_danmaku_team_item);
            }

            @Override
            protected void convert(@NonNull BaseViewHolder holder, DanmakuListResp.JoinTeam model) {
                View view = holder.getView(R.id.root_view);
                ImageLoader.loadXStretchBackground(view, model.buttonPic);

                holder.setText(R.id.tv_name, model.name);
            }
        }
    }

    private class PropProvider extends BaseItemProvider<Object> {

        private final int iconSize = DensityUtils.dp2px(30);
        private final int iconMarginStart = DensityUtils.dp2px(10);

        @Override
        public int getItemViewType() {
            return MyAdapter.PROP_ITEM;
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_danmaku_prop;
        }

        @Override
        public void convert(@NonNull BaseViewHolder holder, Object o) {
            DanmakuListResp.Prop model = (DanmakuListResp.Prop) o;
            TextView tvTitle = holder.getView(R.id.tv_title);
            try {
                tvTitle.setTextColor(Color.parseColor(model.titleColor));
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvTitle.setText(model.title);

            LinearLayout llIconContaier = holder.getView(R.id.ll_icon_container);
            llIconContaier.removeAllViews();
            if (model.warcraftImageList != null) {
                for (int i = 0; i < model.warcraftImageList.size(); i++) {
                    String url = model.warcraftImageList.get(i);
                    ImageView iv = new ImageView(getContext());
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(iconSize, iconSize);
                    if (i > 0) {
                        params.setMarginStart(iconMarginStart);
                    }
                    llIconContaier.addView(iv, params);
                    ImageLoader.loadAvatar(iv, url);
                }
            }

            TextView tvDanmakubtn = holder.getView(R.id.tv_danmaku_btn);
            View viewGiftBtn = holder.getView(R.id.ll_gift_btn);
            ImageView ivGiftIcon = holder.getView(R.id.iv_gift_icon);
            if (model.callMode == DanmakuListResp.CALL_MODE_DANMAKU) {
                tvDanmakubtn.setVisibility(View.VISIBLE);
                viewGiftBtn.setVisibility(View.GONE);
                tvDanmakubtn.setText(model.name);
            } else if (model.callMode == DanmakuListResp.CALL_MODE_GIFT) {
                tvDanmakubtn.setVisibility(View.GONE);
                viewGiftBtn.setVisibility(View.VISIBLE);
                holder.setText(R.id.tv_gift_count, "x" + model.giftAmount);
                holder.setText(R.id.tv_gift_money, getContext().getString(R.string.gold_coin_count, model.giftPrice + ""));
                ImageLoader.loadAvatar(ivGiftIcon, model.giftUrl);
            } else {
                tvDanmakubtn.setVisibility(View.GONE);
                viewGiftBtn.setVisibility(View.GONE);
            }
        }
    }

    public interface DanmakuOnClickListener {
        void onClickTeam(DanmakuListResp.JoinTeam model);

        void onClickProp(DanmakuListResp.Prop model);
    }

}
