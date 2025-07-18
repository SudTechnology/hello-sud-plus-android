package tech.sud.mgp.hello.ui.main.home.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.main.resp.GameModel;

public class GameAdapter extends BaseQuickAdapter<GameModel, BaseViewHolder> {

    public GameAdapter(@Nullable List<GameModel> data) {
        super(R.layout.item_game_view, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GameModel item) {
        TextView nameTv = helper.getView(R.id.game_name);
        nameTv.setText(item.gameName);
        ImageView iconView = helper.getView(R.id.game_icon);
        if (!TextUtils.isEmpty(item.homeGamePic)) {
            nameTv.setTextColor(Color.WHITE);
            ImageLoader.loadGameCover(iconView, item.homeGamePic);
            nameTv.setShadowLayer(3.0f, 0, 0, Color.parseColor("#66000000"));
        } else {
            nameTv.setTextColor(Color.parseColor("#AAAAAA"));
            iconView.setImageResource(R.drawable.icon_game_empty);
            nameTv.setShadowLayer(0.0f, 0, 0, 0);
        }
        helper.setVisible(R.id.iv_llm_bot, item.supportLlm == 1);
    }
}