package tech.sud.mgp.audio.gift.adapter;

import android.graphics.Color;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.audio.R;
import tech.sud.mgp.audio.gift.model.MicUserInfoModel;
import tech.sud.mgp.common.utils.ImageLoader;

public class GiftMicUserAdapter extends BaseQuickAdapter<MicUserInfoModel, BaseViewHolder> {

    public GiftMicUserAdapter(@Nullable List<MicUserInfoModel> data) {
        super(R.layout.audio_mic_user, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, MicUserInfoModel micUserInfoModel) {
        ImageView headerIv = baseViewHolder.getView(R.id.avatar_riv);
        ConstraintLayout textCl = baseViewHolder.getView(R.id.avatar_text_bg_cl);
        if (micUserInfoModel.userInfo.roleType == 1) {
            baseViewHolder.setText(R.id.avatar_text_tv, headerIv.getContext().getString(R.string.audio_room_admin));
        } else {
            baseViewHolder.setText(R.id.avatar_text_tv, headerIv.getContext().getString(R.string.audio_mic_index, micUserInfoModel.indexMic + 1));
        }
        if (micUserInfoModel.checked) {
            baseViewHolder.setVisible(R.id.checked_iv, true);
            textCl.setBackgroundColor(Color.WHITE);
        } else {
            baseViewHolder.setVisible(R.id.checked_iv, false);
            textCl.setBackgroundColor(Color.GRAY);
        }
        ImageLoader.loadAvatar(headerIv, micUserInfoModel.userInfo.avatar);
    }
}
