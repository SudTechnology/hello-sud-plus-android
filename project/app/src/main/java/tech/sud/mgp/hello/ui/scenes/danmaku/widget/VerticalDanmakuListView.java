package tech.sud.mgp.hello.ui.scenes.danmaku.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import tech.sud.mgp.hello.common.utils.ShapeUtils;
import tech.sud.mgp.hello.common.widget.adapter.EmptyProvider;
import tech.sud.mgp.hello.service.room.resp.DanmakuListResp;

/**
 * 竖版弹幕场景内的弹幕列表
 */
public class VerticalDanmakuListView extends ConstraintLayout {

    private final RecyclerView recyclerView = new RecyclerView(getContext());
    private final MyAdapter adapter = new MyAdapter();

    private final RecyclerView mSecondRecyclerView = new RecyclerView(getContext());
    private final SecondAdapter mSecondAdapter = new SecondAdapter();

    private DanmakuOnClickListener danmakuOnClickListener;

    public VerticalDanmakuListView(@NonNull Context context) {
        this(context, null);
    }

    public VerticalDanmakuListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalDanmakuListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public VerticalDanmakuListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
        setListeners();
    }

    private void initView() {
        LayoutParams secondParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        secondParams.topToTop = LayoutParams.PARENT_ID;
        addView(mSecondRecyclerView, secondParams);
        mSecondRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mSecondRecyclerView.setAdapter(mSecondAdapter);
        mSecondRecyclerView.setVisibility(View.GONE);
        int secondPaddingHorizontal = DensityUtils.dp2px(getContext(), 1);
        mSecondRecyclerView.setPadding(secondPaddingHorizontal, 0, secondPaddingHorizontal, 0);
        mSecondRecyclerView.setClipToPadding(false);
        mSecondRecyclerView.setId(View.generateViewId());

        recyclerView.setId(View.generateViewId());
        LayoutParams firstParams = new LayoutParams(LayoutParams.MATCH_PARENT, DensityUtils.dp2px(getContext(), 63));
        firstParams.topToBottom = mSecondRecyclerView.getId();
        addView(recyclerView, firstParams);
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
        mSecondAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter, @NonNull View view, int position) {
                DanmakuListResp.Prop item = mSecondAdapter.getItem(position);
                if (danmakuOnClickListener != null) {
                    danmakuOnClickListener.onClickProp(item);
                }
            }
        });
    }

    /** 设置数据集 */
    public void setDatas(DanmakuListResp resp) {
        setFirstDatas(resp);
        setSecondDatas(resp);
    }

    private void setFirstDatas(DanmakuListResp resp) {
        List<Object> list = new ArrayList<>();
        if (resp != null) {
            if (resp.joinTeamList != null && resp.joinTeamList.size() > 0) {
                list.add(resp.joinTeamList);
            } else if (resp.propList != null) {
                list.addAll(resp.propList);
            }
        }
        if (list.size() == 0) {
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }
        adapter.setList(list);
    }

    private void setSecondDatas(DanmakuListResp resp) {
        List<DanmakuListResp.Prop> list = resp.actionList;
        if (list == null || list.size() == 0) {
            mSecondRecyclerView.setVisibility(View.GONE);
        } else {
            mSecondRecyclerView.setVisibility(View.VISIBLE);
            mSecondAdapter.setList(list);
        }
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
            return R.layout.item_vertical_danmaku_team;
        }

        @Override
        public void convert(@NonNull BaseViewHolder holder, Object o) {
            List<DanmakuListResp.JoinTeam> list = (List<DanmakuListResp.JoinTeam>) o;
            LinearLayout viewRoot = holder.getView(R.id.view_root);
            viewRoot.removeAllViews();
            if (list == null || list.size() == 0) {
                return;
            }
            int paddingHorizonta;
            int width;
            int height = DensityUtils.dp2px(getContext(), 30);
            int marginStart = DensityUtils.dp2px(getContext(), 15);
            if (list.size() == 1) {
                paddingHorizonta = DensityUtils.dp2px(getContext(), 20);
                width = DensityUtils.dp2px(getContext(), 335);
            } else {
                paddingHorizonta = DensityUtils.dp2px(getContext(), 20);
                width = DensityUtils.dp2px(getContext(), 160);
            }
            viewRoot.setPadding(paddingHorizonta, 0, paddingHorizonta, 0);
            for (int i = 0; i < list.size(); i++) {
                DanmakuListResp.JoinTeam joinTeam = list.get(i);
                TextView tv = new TextView(getContext());
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(14);
                tv.setTextColor(Color.WHITE);
                try {
                    tv.setBackground(ShapeUtils.createShape(null, (float) height, null,
                            GradientDrawable.RECTANGLE, null, Color.parseColor(joinTeam.backgroundColor)));
                } catch (Exception e) {
                    e.printStackTrace();
//                    tv.setBackground(ShapeUtils.createShape(null, (float) height, null,
//                            GradientDrawable.RECTANGLE, null, Color.parseColor("#ff0000")));
                }
                tv.setText(joinTeam.name);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                if (i > 0) {
                    params.setMarginStart(marginStart);
                }
                viewRoot.addView(tv, params);
                tv.setOnClickListener(v -> {
                    if (danmakuOnClickListener != null) {
                        danmakuOnClickListener.onClickTeam(joinTeam);
                    }
                });
            }
        }
    }

    private class PropProvider extends BaseItemProvider<Object> {

        @Override
        public int getItemViewType() {
            return MyAdapter.PROP_ITEM;
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_vertical_danmaku_prop;
        }

        @Override
        public void convert(@NonNull BaseViewHolder holder, Object o) {
            DanmakuListResp.Prop model = (DanmakuListResp.Prop) o;
            ImageView ivIcon = holder.getView(R.id.iv_icon);
            String iconUrl = model.warcraftImageList == null || model.warcraftImageList.size() == 0 ? null : model.warcraftImageList.get(0);
            ImageLoader.loadImage(ivIcon, iconUrl);

            holder.setText(R.id.tv_title, model.title);

            if (model.callMode == DanmakuListResp.CALL_MODE_GIFT) {
                holder.setText(R.id.tv_name, getContext().getString(R.string.gold_coin_count, model.giftPrice + ""));
            } else {
                holder.setText(R.id.tv_name, model.name);
            }
        }
    }

    public interface DanmakuOnClickListener {
        void onClickTeam(DanmakuListResp.JoinTeam model);

        void onClickProp(DanmakuListResp.Prop model);
    }

    private class SecondAdapter extends BaseQuickAdapter<DanmakuListResp.Prop, BaseViewHolder> {

        public SecondAdapter() {
            super(R.layout.item_vertical_danmaku_second);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, DanmakuListResp.Prop model) {
            ImageView ivIcon = holder.getView(R.id.iv_icon);
            ImageLoader.loadImage(ivIcon, model.backgroundUrl);

            TextView tvName = holder.getView(R.id.tv_name);
            tvName.setText(model.title);
            try {
                tvName.setTextColor(Color.parseColor(model.titleColor));
            } catch (Exception e) {
                tvName.setTextColor(Color.WHITE);
            }
        }
    }

}
