package tech.sud.mgp.hello.ui.scenes.orderentertainment.adapter;

import android.graphics.Color;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.widget.view.round.RoundedImageView;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.model.OrderGameModel;

public class OrderGameAdapter extends BaseQuickAdapter<OrderGameModel, BaseViewHolder> {

    public OrderGameAdapter() {
        super(R.layout.item_order_game);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, OrderGameModel item) {
        FrameLayout gameBg = baseViewHolder.getView(R.id.game_bg);
        RoundedImageView coverIv = baseViewHolder.getView(R.id.game_cover_iv);
        TextView gameNameTv = baseViewHolder.getView(R.id.game_name_tv);
        TextView gamePriceTv = baseViewHolder.getView(R.id.game_price_tv);
        if (item.checked) {
            gameBg.setBackgroundResource(R.drawable.shape_r8_ffffff_s1_000000);
        } else {
            gameBg.setBackgroundResource(R.drawable.shape_r8_ffffff);
        }
        ImageLoader.loadAvatar(coverIv, item.gameModel.gamePic);
        gameNameTv.setText(item.gameModel.gameName);
        gamePriceTv.setText(R.string.order_price_text);
    }
}
