package tech.sud.mgp.hello.ui.scenes.orderentertainment.adapter;

import android.graphics.Color;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.widget.view.round.RoundedImageView;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.model.OrderMicModel;

public class OrderMicUserAdapter extends BaseQuickAdapter<OrderMicModel, BaseViewHolder> {

    public OrderMicUserAdapter() {
        super(R.layout.item_order_mic_user);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, OrderMicModel item) {
        RoundedImageView headerIv = baseViewHolder.getView(R.id.avatar_riv);
        ImageView checkedIv = baseViewHolder.getView(R.id.checked_iv);
        checkedIv.setSelected(item.checked);
        if (item.checked) {
            headerIv.setBorderColor(Color.WHITE);
        } else {
            headerIv.setBorderColor(Color.TRANSPARENT);
        }
        ImageLoader.loadAvatar(headerIv, item.userInfo.avatar);
    }
}
