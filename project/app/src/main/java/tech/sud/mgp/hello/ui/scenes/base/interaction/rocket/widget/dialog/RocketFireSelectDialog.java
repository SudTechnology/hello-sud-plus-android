package tech.sud.mgp.hello.ui.scenes.base.interaction.rocket.widget.dialog;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.app.APPConfig;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.utils.UserInfoConverter;

/**
 * 火箭发射时，选择接收人的弹窗
 */
public class RocketFireSelectDialog extends BaseDialogFragment {

    private ImageView ivClose;
    private RecyclerView recyclerView;
    private TextView tvConfirm;
    private List<UserInfo> micList;
    private final List<UserInfo> selectedList = new ArrayList<>();
    private final MyAdapter adapter = new MyAdapter();
    private OnConfirmListener onConfirmListener;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_rocket_fire_select;
    }

    @Override
    protected int getWidth() {
        return DensityUtils.dp2px(296);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        ivClose = findViewById(R.id.iv_close);
        recyclerView = findViewById(R.id.recycler_view);
        tvConfirm = findViewById(R.id.tv_confirm);

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        super.initData();
        adapter.setList(micList);
        updateConfirmText();
    }

    private void updateConfirmText() {
        if (tvConfirm == null) {
            return;
        }
        tvConfirm.setText(getString(R.string.confirm_fire_price, (getRealSelectedList().size() * APPConfig.ROCKET_FIRE_PRICE) + ""));
    }

    private List<UserInfo> getRealSelectedList() {
        List<UserInfo> list = new ArrayList<>();
        for (UserInfo userInfo : selectedList) {
            if (micList != null && micList.contains(userInfo)) {
                list.add(userInfo);
            }
        }
        return list;
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        ivClose.setOnClickListener((v) -> {
            dismiss();
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> quickAdapter, @NonNull View view, int position) {
                UserInfo item = adapter.getItem(position);
                if (isSelected(item)) {
                    selectedList.remove(item);
                } else {
                    selectedList.remove(item);
                    selectedList.add(item);
                }
                updateConfirmText();
                adapter.notifyItemChanged(position);
            }
        });
        tvConfirm.setOnClickListener((v) -> {
            List<UserInfo> realSelectedList = getRealSelectedList();
            if (realSelectedList.size() == 0) {
                return;
            }
            onConfirmListener.onConfirm(realSelectedList);
            dismiss();
        });
    }

    /** 设置确认监听 */
    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    /** 设置麦位列表数据 */
    public void setMicList(List<AudioRoomMicModel> micList) {
        List<UserInfo> list = filterRocketMicList(micList);
        this.micList = list;
        adapter.setList(list);
        updateConfirmText();
    }

    private List<UserInfo> filterRocketMicList(List<AudioRoomMicModel> micList) {
        if (micList == null) {
            return null;
        }
        List<UserInfo> list = new ArrayList<>();
        for (AudioRoomMicModel model : micList) {
            if (model.hasUser() && HSUserInfo.userId != model.userId) {
                UserInfo userInfo = UserInfoConverter.conver(model);
                list.add(userInfo);
            }
        }
        return list;
    }

    public interface OnConfirmListener {
        void onConfirm(List<UserInfo> list);
    }

    private class MyAdapter extends BaseQuickAdapter<UserInfo, BaseViewHolder> {
        public MyAdapter() {
            super(R.layout.item_rocket_fire_select);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, UserInfo model) {
            ImageView ivIcon = holder.getView(R.id.iv_icon);
            ImageLoader.loadAvatar(ivIcon, model.icon);
            holder.setText(R.id.tv_name, model.name);
            holder.setVisible(R.id.container_selected, isSelected(model));
        }
    }

    /**
     * 是否已选择
     */
    private boolean isSelected(UserInfo model) {
        return selectedList.contains(model);
    }

}
