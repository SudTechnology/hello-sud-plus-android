package tech.sud.mgp.hello.ui.main.home;

import android.text.TextUtils;
import android.widget.ImageView;

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
        helper.setText(R.id.game_name, item.getGameName());
        ImageView iconView = helper.getView(R.id.game_icon);
        if (!TextUtils.isEmpty(item.getGamePic())) {
            ImageLoader.loadImage(iconView, item.getGamePic());
        } else {
            iconView.setImageResource(R.drawable.icon_logo);
        }
    }
}