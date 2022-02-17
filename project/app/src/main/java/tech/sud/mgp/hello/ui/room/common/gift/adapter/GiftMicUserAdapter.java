package tech.sud.mgp.hello.ui.room.common.gift.adapter;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.widget.view.round.RoundedImageView;
import tech.sud.mgp.hello.ui.room.common.gift.model.MicUserInfoModel;

public class GiftMicUserAdapter extends BaseQuickAdapter<MicUserInfoModel, BaseViewHolder> {

    public GiftMicUserAdapter(@Nullable List<MicUserInfoModel> data) {
        super(R.layout.mic_user, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, MicUserInfoModel micUserInfoModel) {
        RoundedImageView headerIv = baseViewHolder.getView(R.id.avatar_riv);
        ConstraintLayout textCl = baseViewHolder.getView(R.id.avatar_text_bg_cl);
        if (micUserInfoModel.userInfo.roleType == 1) {
            baseViewHolder.setText(R.id.avatar_text_tv, headerIv.getContext().getString(R.string.audio_room_admin));
        } else {
            baseViewHolder.setText(R.id.avatar_text_tv, headerIv.getContext().getString(R.string.audio_mic_index, micUserInfoModel.indexMic + 1));
        }
        if (micUserInfoModel.checked) {
            baseViewHolder.setVisible(R.id.checked_iv, true);
            textCl.setBackgroundColor(Color.WHITE);
            headerIv.setBorderColor(Color.WHITE);
        } else {
            baseViewHolder.setVisible(R.id.checked_iv, false);
            textCl.setBackgroundColor(Color.GRAY);
            headerIv.setBorderColor(Color.TRANSPARENT);
        }
        ImageLoader.loadAvatar(headerIv, micUserInfoModel.userInfo.avatar);
    }
}
