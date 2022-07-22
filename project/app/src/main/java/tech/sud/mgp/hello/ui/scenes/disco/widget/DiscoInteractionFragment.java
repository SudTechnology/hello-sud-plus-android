package tech.sud.mgp.hello.ui.scenes.disco.widget;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.resp.DiscoAnchorListResp;
import tech.sud.mgp.hello.ui.common.utils.CompletedListener;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.scenes.disco.model.DiscoInteractionModel;
import tech.sud.mgp.hello.ui.scenes.disco.model.DiscoInteractionType;

/**
 * 蹦迪互动弹窗 列表页面
 */
public class DiscoInteractionFragment extends BaseFragment {

    private int pageIndex;
    private MyAdapter adapter;
    private long roomId;
    private boolean isAnchor;
    private DiscoInteractionDialog.OnActionListener onActionListener;

    public static DiscoInteractionFragment newInstance(int pageIndex, long roomId) {
        Bundle args = new Bundle();
        args.putInt("pageIndex", pageIndex);
        args.putLong("roomId", roomId);
        DiscoInteractionFragment fragment = new DiscoInteractionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            pageIndex = arguments.getInt("pageIndex");
            roomId = arguments.getLong("roomId");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_disco_interaction;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        if (pageIndex == 0) {
            recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 4, RecyclerView.VERTICAL, false));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false));
        }
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);
    }

    @Override
    protected void initData() {
        super.initData();
        RoomRepository.discoAnchorList(this, roomId, new RxCallback<DiscoAnchorListResp>() {
            @Override
            public void onSuccess(DiscoAnchorListResp resp) {
                super.onSuccess(resp);
                discoAnchorOnSuccess(resp);
            }
        });
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                onItemClickAction(position);
            }
        });
    }

    private void onItemClickAction(int position) {
        DiscoInteractionModel item = adapter.getItem(position);
        if (onActionListener != null) {
            onActionListener.onAction(item);
        }
        if (item.type == DiscoInteractionType.JOIN_ANCHOR) {
            isAnchor = true;
            updateAnchor();
        } else if (item.type == DiscoInteractionType.LEAVE_ANCHOR) {
            isAnchor = false;
            updateAnchor();
        }
    }

    // 主播列表返回
    private void discoAnchorOnSuccess(DiscoAnchorListResp resp) {
        LifecycleUtils.safeLifecycle(this, new CompletedListener() {
            @Override
            public void onCompleted() {
                checkIsAnchor(resp);
                adapter.setList(getList());
            }
        });
    }

    // 判断自己是否是主播
    private void checkIsAnchor(DiscoAnchorListResp resp) {
        if (resp != null && resp.userInfoList != null) {
            for (UserInfoResp info : resp.userInfoList) {
                if (info.userId == HSUserInfo.userId) {
                    isAnchor = true;
                    return;
                }
            }
        }
        isAnchor = false;
    }

    // 获取列表数据
    private List<DiscoInteractionModel> getList() {
        List<DiscoInteractionModel> list = new ArrayList<>();
        if (pageIndex == 0) {
            if (isAnchor) {
                list.add(buildModel(getString(R.string.leave_anchor), DiscoInteractionType.LEAVE_ANCHOR, null));
            } else {
                list.add(buildModel(getString(R.string.join_anchor), DiscoInteractionType.JOIN_ANCHOR, null));
            }
            list.add(buildModel(getString(R.string.up_dj), DiscoInteractionType.UP_DJ, 200));
            list.add(buildModel(getString(R.string.move), DiscoInteractionType.MOVE, 0));
            list.add(buildModel(getString(R.string.god), DiscoInteractionType.GOD, 1));
            list.add(buildModel(getString(R.string.largen), DiscoInteractionType.BIG, 5));
            list.add(buildModel(getString(R.string.change_role), DiscoInteractionType.CHANGE_ROLE, 6));
            list.add(buildModel(getString(R.string.feature), DiscoInteractionType.FOCUS, 10));
            list.add(buildModel(getString(R.string.title), DiscoInteractionType.TITLE, 30));
            list.add(buildModel(getString(R.string.effect), DiscoInteractionType.EFFECTS, 50));
        } else {
            list.add(buildModel(getString(R.string.pop_big_focus), DiscoInteractionType.POP_BIG_FOCUS, 1000));
            list.add(buildModel(getString(R.string.pop_big_focus_effect), DiscoInteractionType.POP_BIG_FOCUS_EFFECTS, 10000));
        }
        return list;
    }

    private DiscoInteractionModel buildModel(String name, DiscoInteractionType type, Integer price) {
        DiscoInteractionModel model = new DiscoInteractionModel();
        model.name = name;
        model.type = type;
        model.price = price;
        return model;
    }

    private static class MyAdapter extends BaseQuickAdapter<DiscoInteractionModel, BaseViewHolder> {
        public MyAdapter() {
            super(R.layout.item_disco_interaction);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void convert(@NonNull BaseViewHolder holder, DiscoInteractionModel model) {
            View viewRoot = holder.getView(R.id.view_root);
            ViewUtils.setViewClickAnim(viewRoot);
            
            holder.setText(R.id.tv_name, model.name);
            View viewPrice = holder.getView(R.id.view_price);
            View viewCoin = holder.getView(R.id.view_coin);
            TextView tvPrice = holder.getView(R.id.tv_price);

            if (model.price == null) {
                viewPrice.setVisibility(View.GONE);
            } else if (model.price == 0) {
                viewPrice.setVisibility(View.VISIBLE);
                viewCoin.setVisibility(View.GONE);
                tvPrice.setText(R.string.free);
            } else {
                viewPrice.setVisibility(View.VISIBLE);
                viewCoin.setVisibility(View.VISIBLE);
                tvPrice.setText(model.price + "");
            }
        }
    }

    public void setOnActionListener(DiscoInteractionDialog.OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
    }

    // 设置自己是不是主播
    public void setAnchor(boolean anchor) {
        isAnchor = anchor;
        updateAnchor();
    }

    private void updateAnchor() {
        if (adapter != null && adapter.getData().size() > 0) {
            DiscoInteractionModel item = adapter.getItem(0);
            if (item.type == DiscoInteractionType.JOIN_ANCHOR || item.type == DiscoInteractionType.LEAVE_ANCHOR) {
                if (isAnchor) {
                    item.type = DiscoInteractionType.LEAVE_ANCHOR;
                    item.name = getString(R.string.leave_anchor);
                } else {
                    item.type = DiscoInteractionType.JOIN_ANCHOR;
                    item.name = getString(R.string.join_anchor);
                }
            }
            adapter.notifyItemChanged(0);
        }
    }

}
