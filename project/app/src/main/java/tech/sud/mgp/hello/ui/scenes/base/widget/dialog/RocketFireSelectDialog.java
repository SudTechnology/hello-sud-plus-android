package tech.sud.mgp.hello.ui.scenes.base.widget.dialog;

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
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;

/**
 * 火箭发射时，选择接收人的弹窗
 */
public class RocketFireSelectDialog extends BaseDialogFragment {

    private ImageView ivClose;
    private RecyclerView recyclerView;
    private TextView tvConfirm;
    private List<AudioRoomMicModel> micList;
    private final List<AudioRoomMicModel> selectedList = new ArrayList<>();
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
        tvConfirm.setText(getString(R.string.confirm_fire_price, (selectedList.size() * APPConfig.ROCKET_FIRE_PRICE) + ""));
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
                AudioRoomMicModel item = adapter.getItem(position);
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
            if (selectedList.size() == 0) {
                return;
            }
            onConfirmListener.onConfirm(selectedList);
            dismiss();
        });
    }

    /** 设置确认监听 */
    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    /** 设置麦位列表数据 */
    public void setMicList(List<AudioRoomMicModel> micList) {
        this.micList = micList;
    }

    public interface OnConfirmListener {
        void onConfirm(List<AudioRoomMicModel> list);
    }

    private class MyAdapter extends BaseQuickAdapter<AudioRoomMicModel, BaseViewHolder> {
        public MyAdapter() {
            super(R.layout.item_rocket_fire_select);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, AudioRoomMicModel model) {
            ImageView ivIcon = holder.getView(R.id.iv_icon);
            ImageLoader.loadAvatar(ivIcon, model.avatar);
            holder.setText(R.id.tv_name, model.nickName);
            holder.setVisible(R.id.container_selected, isSelected(model));
        }
    }

    /**
     * 是否已选择
     */
    private boolean isSelected(AudioRoomMicModel model) {
        return selectedList.contains(model);
    }

}
