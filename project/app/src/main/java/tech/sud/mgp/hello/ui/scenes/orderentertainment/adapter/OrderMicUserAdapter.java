package tech.sud.mgp.hello.ui.scenes.orderentertainment.adapter;

import android.graphics.Color;
import android.view.View;
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

    public boolean hasAnchor = false;//麦上是否有主播

    public OrderMicUserAdapter() {
        super(R.layout.item_order_mic_user);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, OrderMicModel item) {
        RoundedImageView headerIv = baseViewHolder.getView(R.id.avatar_riv);
        ImageView checkedIv = baseViewHolder.getView(R.id.checked_iv);
        if (item.isFake) {
            ImageLoader.loadDrawable(headerIv, R.drawable.icon_order_empty_mic);
            checkedIv.setVisibility(View.GONE);
            headerIv.setBorderColor(Color.TRANSPARENT);
        } else {
            checkedIv.setSelected(item.checked);
            if (item.checked) {
                headerIv.setBorderColor(Color.BLACK);
            } else {
                headerIv.setBorderColor(Color.TRANSPARENT);
            }
            ImageLoader.loadAvatar(headerIv, item.userInfo.avatar);
        }
    }
}
