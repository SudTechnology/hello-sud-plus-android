package tech.sud.mgp.hello.ui.scenes.audio3d.widget.dialog;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.gip.SudGIPWrapper.state.SudGIPAPPState;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;

public class PutOtherUpMicDialog extends BaseDialogFragment {

    private MyAdapter mAdapter;
    public List<SudGIPAPPState.AppCustomCrSetSeats.CrSeatModel> seatList;
    public List<AudioRoomMicModel> micList;
    public int seatIndex;

    private OnPutOtherUpMicClickListener mOnPutOtherUpMicClickListener;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_put_other_up_mic;
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setWindowAnimations(R.style.BottomToTopAnim);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new MyAdapter();
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        mAdapter.setList(micList);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        mAdapter.addChildClickViewIds(R.id.tv_operate);
        mAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                AudioRoomMicModel item = mAdapter.getItem(position);
                if (existsAudio3D(item)) {
                    return;
                }
                if (mOnPutOtherUpMicClickListener != null) {
                    mOnPutOtherUpMicClickListener.onClick(seatIndex, item);
                }
                dismiss();
            }
        });
    }

    private class MyAdapter extends BaseQuickAdapter<AudioRoomMicModel, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_put_other_up_mic);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, AudioRoomMicModel model) {
            ImageView ivIcon = holder.getView(R.id.iv_icon);
            ImageLoader.loadAvatar(ivIcon, model.avatar);

            holder.setText(R.id.tv_name, model.nickName);

            TextView tvOperate = holder.getView(R.id.tv_operate);
            if (existsAudio3D(model)) {
                tvOperate.setText(R.string.already_in_the_scene);
                tvOperate.setTextColor(Color.WHITE);
                tvOperate.setBackgroundColor(Color.parseColor("#4c000000"));
            } else {
                tvOperate.setText(R.string.put_other_up_mic);
                tvOperate.setTextColor(Color.WHITE);
                tvOperate.setBackgroundColor(Color.BLACK);
            }
        }
    }

    private boolean existsAudio3D(AudioRoomMicModel model) {
        if (seatList != null) {
            for (SudGIPAPPState.AppCustomCrSetSeats.CrSeatModel crSeatModel : seatList) {
                if ((model.userId + "").equals(crSeatModel.userId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setOnPutOtherUpMicClickListener(OnPutOtherUpMicClickListener onPutOtherUpMicClickListener) {
        mOnPutOtherUpMicClickListener = onPutOtherUpMicClickListener;
    }

    public interface OnPutOtherUpMicClickListener {
        void onClick(int seatIndex, AudioRoomMicModel model);
    }

}
