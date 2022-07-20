package tech.sud.mgp.hello.ui.scenes.disco.widget;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ShapeUtils;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.GetAccountResp;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.resp.DiscoAnchorListResp;
import tech.sud.mgp.hello.ui.common.utils.FormatUtils;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.GiftHelper;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;

/**
 * 约主播跳舞 弹窗
 */
public class InviteDanceDialog extends BaseDialogFragment {

    private TextView tvBalance;
    private TextView tvDance;
    private AnchorAdapter anchorAdapter;
    private DurationAdapter durationAdapter;
    private final TreeSet<Integer> selectedAnchorSet = new TreeSet<>();
    private int selectedDuration = 1;
    private int singleGiftPrice; // 单个礼物价格
    private long roomId;
    private OnDanceListener onDanceListener;
    private GiftModel giftModel;

    public static InviteDanceDialog newInstance(long roomId) {
        Bundle args = new Bundle();
        args.putLong("roomId", roomId);
        InviteDanceDialog fragment = new InviteDanceDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            roomId = arguments.getLong("roomId");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_invite_dance;
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setWindowAnimations(R.style.BottomToTopAnim);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        tvBalance = findViewById(R.id.tv_balance);
        tvDance = findViewById(R.id.tv_dance);
        RecyclerView recyclerViewAnchor = findViewById(R.id.recycler_view_anchor);
        RecyclerView recyclerViewDuration = findViewById(R.id.recycler_view_duration);

        recyclerViewAnchor.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        anchorAdapter = new AnchorAdapter();
        recyclerViewAnchor.setAdapter(anchorAdapter);
        recyclerViewAnchor.setItemAnimator(null);

        recyclerViewDuration.setLayoutManager(new GridLayoutManager(requireContext(), 3, LinearLayoutManager.VERTICAL, false));
        durationAdapter = new DurationAdapter();
        recyclerViewDuration.setAdapter(durationAdapter);
        recyclerViewDuration.setItemAnimator(null);
    }

    @Override
    protected void initData() {
        super.initData();
        initAnchorList();
        initDurationList();
        initBalance();
        initPrice();
        updateDanceInfo();
    }

    private void initPrice() {
        giftModel = GiftHelper.getInstance().getGift(5);
        if (giftModel != null) {
            singleGiftPrice = giftModel.giftPrice;
        }
    }

    private void initAnchorList() {
        RoomRepository.discoAnchorList(this, roomId, new RxCallback<DiscoAnchorListResp>() {
            @Override
            public void onSuccess(DiscoAnchorListResp resp) {
                super.onSuccess(resp);
                if (resp != null) {
                    anchorAdapter.setList(resp.userInfoList);
                }
            }
        });
    }

    private void initDurationList() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(3);
        list.add(10);
        durationAdapter.setList(list);
    }

    private void initBalance() {
        HomeRepository.getAccount(this, new RxCallback<GetAccountResp>() {
            @Override
            public void onSuccess(GetAccountResp resp) {
                super.onSuccess(resp);
                if (resp != null) {
                    tvBalance.setText(FormatUtils.formatMoney(resp.coin));
                }
            }
        });
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        anchorAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (selectedAnchorSet.contains(position)) {
                    selectedAnchorSet.remove(position);
                } else {
                    selectedAnchorSet.add(position);
                }
                anchorAdapter.notifyItemChanged(position);
                updateDanceInfo();
            }
        });
        durationAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                selectedDuration = durationAdapter.getItem(position);
                updateDanceInfo();
                durationAdapter.notifyDataSetChanged();
            }
        });
        tvDance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDanceListener != null) {
                    onDanceListener.onDance(giftModel, getSelectedUser(), selectedDuration);
                }
                dismiss();
            }
        });
    }

    private List<UserInfoResp> getSelectedUser() {
        List<UserInfoResp> list = new ArrayList<>();
        for (Integer position : selectedAnchorSet) {
            list.add(anchorAdapter.getItem(position));
        }
        return list;
    }

    // 更新约TA砂舞按钮信息
    private void updateDanceInfo() {
        int price = selectedDuration * selectedAnchorSet.size() * singleGiftPrice;
        tvDance.setText(getString(R.string.invite_dance_him, price + ""));
    }

    private class AnchorAdapter extends BaseQuickAdapter<UserInfoResp, BaseViewHolder> {

        public AnchorAdapter() {
            super(R.layout.item_anchor);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, UserInfoResp userInfo) {
            ImageView ivIcon = holder.getView(R.id.iv_icon);
            ImageLoader.loadAvatar(ivIcon, userInfo.avatar);

            int position = holder.getAdapterPosition();
            boolean isSelected = selectedAnchorSet.contains(position);
            View viewSelectedStroke = holder.getView(R.id.view_selected_stroke);
            viewSelectedStroke.setBackground(ShapeUtils.createShape(DensityUtils.dp2px(2), (float) DensityUtils.dp2px(40),
                    null, GradientDrawable.RECTANGLE, Color.WHITE, null));

            holder.setVisible(R.id.view_selected_stroke, isSelected);
            holder.setVisible(R.id.view_selected, isSelected);
        }
    }

    private class DurationAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {
        public DurationAdapter() {
            super(R.layout.item_dance_duration);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, Integer duration) {
            TextView tvDuration = holder.getView(R.id.tv_duration);
            tvDuration.setText(getContext().getString(R.string.number_minute, duration + ""));
            if (selectedDuration == duration) {
                tvDuration.setBackground(ShapeUtils.createShape(null, (float) DensityUtils.dp2px(4),
                        null, GradientDrawable.RECTANGLE, null, Color.WHITE));
                tvDuration.setTextColor(Color.BLACK);
            } else {
                tvDuration.setBackground(ShapeUtils.createShape(null, (float) DensityUtils.dp2px(4),
                        null, GradientDrawable.RECTANGLE, null, Color.parseColor("#33000000")));
                tvDuration.setTextColor(Color.WHITE);
            }
        }
    }

    public void setOnDanceListener(OnDanceListener onDanceListener) {
        this.onDanceListener = onDanceListener;
    }

    public interface OnDanceListener {
        /**
         * 跳舞回调
         *
         * @param list   主播集合
         * @param minute 分钟数
         */
        void onDance(GiftModel giftModel, List<UserInfoResp> list, int minute);
    }

}
