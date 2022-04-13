package tech.sud.mgp.hello.ui.main.roomlist;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.main.manager.HomeManager;
import tech.sud.mgp.hello.ui.main.constant.SceneType;
import tech.sud.mgp.hello.ui.main.home.RoomItemModel;

public class RoomListAdapter extends BaseQuickAdapter<RoomItemModel, BaseViewHolder> {

    public RoomListAdapter(@Nullable List<RoomItemModel> data) {
        super(R.layout.item_room_list, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RoomItemModel item) {
        ImageView cover = helper.getView(R.id.room_cover);
        TextView sceneNameTv = helper.getView(R.id.room_scene);
        if (item.getSceneType() == SceneType.TICKET) {
            helper.setText(R.id.room_name, item.getRoomName() + "-" + item.getGameLevelDesc());
        } else {
            helper.setText(R.id.room_name, item.getRoomName());
        }
        helper.setText(R.id.room_id, cover.getContext().getString(R.string.room_list_roomid, item.getRoomId() + ""));
        helper.setText(R.id.room_online, cover.getContext().getString(R.string.room_list_online, item.getMemberCount() + ""));
        helper.setText(R.id.rtc_name, AppData.getInstance().getRtcNameByRtcType(item.getRtcType()));
        sceneNameTv.setText(item.getSceneTag());
        HomeManager.SceneTagColor sceneTagColor = HomeManager.getInstance().sceneTagResId(item.getSceneType());
        sceneNameTv.setBackgroundColor(sceneTagColor.colorBg);
        sceneNameTv.setTextColor(sceneTagColor.colorText);
        if (!TextUtils.isEmpty(item.getRoomPic())) {
            ImageLoader.loadImage(cover, item.getRoomPic());
        } else {
            cover.setImageResource(R.drawable.icon_logo);
        }
    }
}