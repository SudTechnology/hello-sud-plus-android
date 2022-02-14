package tech.sud.mgp.hello.home.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.home.manager.HomeManager;
import tech.sud.mgp.hello.home.model.RoomItemModel;

public class RoomListAdapter extends BaseQuickAdapter<RoomItemModel, BaseViewHolder> {

    public RoomListAdapter(@Nullable List<RoomItemModel> data) {
        super(R.layout.item_room_list, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RoomItemModel item) {
        ImageView cover = helper.getView(R.id.room_cover);
        helper.setText(R.id.room_name, item.getRoomName());
        helper.setText(R.id.room_id, cover.getContext().getString(R.string.room_list_roomid, item.getRoomId() + ""));
        helper.setText(R.id.room_online, cover.getContext().getString(R.string.room_list_online, item.getMemberCount() + ""));
        helper.setText(R.id.room_scene, HomeManager.getInstance().sceneName(item.getSceneType()));
        if (!TextUtils.isEmpty(item.getRoomPic())) {
            ImageLoader.loadImage(cover, item.getRoomPic());
        } else {
            cover.setImageResource(R.mipmap.icon_logo);
        }
    }
}