package tech.sud.mgp.hello.ui.main.discover;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.main.manager.HomeManager;
import tech.sud.mgp.hello.service.main.resp.AuthRoomModel;
import tech.sud.mgp.hello.service.main.resp.GameModel;

/**
 * 发现页房间列表
 */
public class DiscoverRoomAdapter extends BaseQuickAdapter<AuthRoomModel, BaseViewHolder> implements LoadMoreModule {

    public DiscoverRoomAdapter() {
        super(R.layout.item_discover_room);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, AuthRoomModel model) {
        GameModel gameModel = HomeManager.getInstance().getGameModel(model.mgId);

        String gamePic;
        String gameName;
        if (gameModel == null) {
            gamePic = null;
            gameName = null;
        } else {
            gamePic = gameModel.gamePic;
            gameName = gameModel.gameName;
        }

        ImageView ivIcon = holder.getView(R.id.iv_icon);
        ImageLoader.loadGameCover(ivIcon, gamePic);

        holder.setText(R.id.tv_id, "ID " + model.roomId);
        holder.setText(R.id.tv_count, getContext().getString(R.string.play_count, model.playerTotal + model.obTotal + ""));
        holder.setText(R.id.tv_name, gameName);
    }

    @NonNull
    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(baseQuickAdapter);
    }

}
