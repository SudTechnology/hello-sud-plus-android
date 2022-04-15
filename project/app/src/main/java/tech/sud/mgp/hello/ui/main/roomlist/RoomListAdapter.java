package tech.sud.mgp.hello.ui.main.roomlist;

import android.graphics.Color;
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
import tech.sud.mgp.hello.ui.main.constant.SceneType;
import tech.sud.mgp.hello.ui.main.home.model.RoomItemModel;

public class RoomListAdapter extends BaseQuickAdapter<RoomItemModel, BaseViewHolder> {

    public RoomListAdapter(@Nullable List<RoomItemModel> data) {
        super(R.layout.item_room_list, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RoomItemModel item) {
        ImageView cover = helper.getView(R.id.room_cover);
        TextView sceneNameTv = helper.getView(R.id.room_scene);
        if (item.getSceneType() == SceneType.TICKET) {
            helper.setText(R.id.room_name, item.getRoomName() + "·" + item.getGameLevelDesc());
        } else {
            helper.setText(R.id.room_name, item.getRoomName());
        }
        helper.setText(R.id.room_id, cover.getContext().getString(R.string.room_list_roomid, item.getRoomNumber()));
        helper.setText(R.id.room_online, cover.getContext().getString(R.string.room_list_online, item.getMemberCount() + ""));
        helper.setText(R.id.rtc_name, AppData.getInstance().getRtcNameByRtcType(item.getRtcType()));
        sceneNameTv.setText(item.getSceneTag());
        SceneTagColor sceneTagColor = sceneTagResId(item.getSceneType());
        sceneNameTv.setBackgroundColor(sceneTagColor.colorBg);
        sceneNameTv.setTextColor(sceneTagColor.colorText);
        if (!TextUtils.isEmpty(item.getRoomPic())) {
            ImageLoader.loadImage(cover, item.getRoomPic());
        } else {
            cover.setImageResource(R.drawable.icon_logo);
        }
    }

    /**
     * 获取场景TAG
     * sceneType:场景id
     */
    public SceneTagColor sceneTagResId(int sceneType) {
        SceneTagColor color = new SceneTagColor();
        switch (sceneType) {
            case SceneType.ASR:
                color.colorBg = Color.parseColor("#9622C1");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.TICKET:
                color.colorBg = Color.parseColor("#E35017");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.TALENT:
                color.colorBg = Color.parseColor("#F7268B");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.CROSS_ROOM:
                color.colorBg = Color.parseColor("#504EEB");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.ONE_ONE:
                color.colorBg = Color.parseColor("#1378F1");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.ORDER_ENTERTAINMENT:
                color.colorBg = Color.parseColor("#27B7E8");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.QUIZ:
                color.colorBg = Color.parseColor("#FDAB26");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.SHOW:
                color.colorBg = Color.parseColor("#EC5420");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.AUDIO:
                color.colorBg = Color.parseColor("#8324DF");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            default:
                color.colorBg = Color.parseColor("#f5f5f5");
                color.colorText = Color.parseColor("#999999");
                break;
        }
        return color;
    }

    /**
     * 根据场景id，返回场景信息的model
     */
    public static class SceneTagColor {
        public int colorBg;
        public int colorText;
    }
}