package tech.sud.mgp.audio.gift.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.audio.R;
import tech.sud.mgp.audio.gift.model.MicUserInfoModel;

public class GiftMicUserAdapter extends BaseQuickAdapter<MicUserInfoModel, BaseViewHolder> {

    public GiftMicUserAdapter(@Nullable List<MicUserInfoModel> data) {
        super(R.layout.audio_mic_user, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, MicUserInfoModel micUserInfoModel) {
        ImageView headerIv = baseViewHolder.getView(R.id.avatar_riv);
        baseViewHolder.setText(R.id.avatar_text_tv, micUserInfoModel.indexMic);
        if (micUserInfoModel.checked) {
            baseViewHolder.setVisible(R.id.checked_iv, true);
        } else {
            baseViewHolder.setVisible(R.id.checked_iv, false);
        }
    }
}
