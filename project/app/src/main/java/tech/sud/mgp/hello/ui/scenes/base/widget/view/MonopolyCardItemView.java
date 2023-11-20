package tech.sud.mgp.hello.ui.scenes.base.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseProviderMultiAdapter;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.MonopolyCardGiftModel;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.MonopolyCardContainer.MonopolyCardPageModel;

public class MonopolyCardItemView extends ConstraintLayout {

    public int typeDanamku = 1;
    public int typeGiftCard = 2;
    public int typeRoleCard = 3;
    private RecyclerView mRecyclerView = new NoTouchRecyclerView(getContext());
    private MyAdapter mAdapter = new MyAdapter();

    public MonopolyCardItemView(@NonNull Context context) {
        this(context, null);
    }

    public MonopolyCardItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonopolyCardItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        int spanCount = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), spanCount, LinearLayoutManager.VERTICAL, false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                MonopolyCardGiftModel item = mAdapter.getItem(position);
                int monopolyItemType = getMonopolyItemType(item);
                if (monopolyItemType == typeDanamku || monopolyItemType == typeRoleCard) {
                    return spanCount;
                }
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        addView(mRecyclerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        int paddingHorizonta = DensityUtils.dp2px(getContext(), 7);
        setPadding(paddingHorizonta, DensityUtils.dp2px(getContext(), 3), paddingHorizonta, 0);
    }

    public void setDatas(MonopolyCardPageModel model) {
        mAdapter.setList(model.list);
    }

    private class MyAdapter extends BaseProviderMultiAdapter<MonopolyCardGiftModel> {
        public MyAdapter() {
            addItemProvider(new MonopolyDanmakuProvider());
            addItemProvider(new MonopolyGiftCardProvider());
            addItemProvider(new MonopolyRoleCardProvider());
        }

        @Override
        protected int getItemType(@NonNull List<? extends MonopolyCardGiftModel> list, int position) {
            return getMonopolyItemType(getItem(position));
        }
    }

    public int getMonopolyItemType(MonopolyCardGiftModel model) {
        if (model.isDanmaku) {
            return typeDanamku;
        }
        if (model.getCardType() == 1) {
            return typeRoleCard;
        }
        return typeGiftCard;
    }

    public class MonopolyDanmakuProvider extends BaseItemProvider<MonopolyCardGiftModel> {
        @Override
        public int getItemViewType() {
            return typeDanamku;
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_monopoly_danmaku;
        }

        @Override
        public void convert(@NonNull BaseViewHolder holder, MonopolyCardGiftModel model) {
            View rootView = holder.getView(R.id.root_view);
            int indexOf = mAdapter.getData().indexOf(model);
            if (indexOf == 0) {
                ViewUtils.setMarginTop(rootView, DensityUtils.dp2px(3));
            } else {
                ViewUtils.setMarginTop(rootView, DensityUtils.dp2px(4));
            }

            if (model.getGiftDetails() != null) {
                holder.setText(R.id.tv_content, model.getGiftDetails().content);
                holder.setText(R.id.tv_name, model.getGiftName());
            } else {
                holder.setText(R.id.tv_content, "");
                holder.setText(R.id.tv_name, "");
            }
        }
    }

    public class MonopolyGiftCardProvider extends BaseItemProvider<MonopolyCardGiftModel> {
        @Override
        public int getItemViewType() {
            return typeGiftCard;
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_monopoly_gift_card;
        }

        @Override
        public void convert(@NonNull BaseViewHolder holder, MonopolyCardGiftModel model) {
            View rootView = holder.getView(R.id.root_view);
            int indexOf = mAdapter.getData().indexOf(model);
            if (indexOf == 0) {
                ViewUtils.setMarginTop(rootView, DensityUtils.dp2px(3));
            } else {
                ViewUtils.setMarginTop(rootView, DensityUtils.dp2px(4));
            }

            ImageView ivIcon = holder.getView(R.id.iv_icon);
            ImageLoader.loadImage(ivIcon, model.getGiftUrl());

            holder.setText(R.id.tv_name, model.getGiftName());
        }
    }

    public class MonopolyRoleCardProvider extends BaseItemProvider<MonopolyCardGiftModel> {
        @Override
        public int getItemViewType() {
            return typeRoleCard;
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_monopoly_role_card;
        }

        @Override
        public void convert(@NonNull BaseViewHolder holder, MonopolyCardGiftModel model) {
            View rootView = holder.getView(R.id.root_view);
            int indexOf = mAdapter.getData().indexOf(model);
            if (indexOf == 0) {
                ViewUtils.setMarginTop(rootView, DensityUtils.dp2px(6));
            } else {
                ViewUtils.setMarginTop(rootView, DensityUtils.dp2px(8));
            }

            ImageView ivIcon = holder.getView(R.id.iv_icon);
            ImageLoader.loadImage(ivIcon, model.getGiftUrl());

            holder.setText(R.id.tv_name, model.getDescription());
        }
    }

}
