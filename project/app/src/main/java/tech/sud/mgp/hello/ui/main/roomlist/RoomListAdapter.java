package tech.sud.mgp.hello.ui.main.roomlist;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ShapeUtils;
import tech.sud.mgp.hello.service.room.model.CrossAppMatchStatus;
import tech.sud.mgp.hello.service.room.model.PkStatus;
import tech.sud.mgp.hello.ui.main.base.constant.SceneType;
import tech.sud.mgp.hello.ui.main.home.model.RoomItemModel;

/**
 * 房间列表适配器
 */
public class RoomListAdapter extends BaseQuickAdapter<RoomItemModel, BaseViewHolder> implements LoadMoreModule {

    private int btnText; // 右下角按钮的文字
    private int radius = DensityUtils.dp2px(10);

    public RoomListAdapter() {
        this(0);
    }

    public RoomListAdapter(@StringRes int btnText) {
        super(R.layout.item_room_list);
        this.btnText = btnText;
    }

    @Override
    protected void convert(BaseViewHolder helper, RoomItemModel item) {
        // 封面
        ImageView cover = helper.getView(R.id.room_cover);
        if (!TextUtils.isEmpty(item.getRoomPic())) {
            ImageLoader.loadImage(cover, item.getRoomPic());
        } else {
            cover.setImageResource(R.drawable.icon_logo);
        }

        // 房间名称
        if (TextUtils.isEmpty(item.getGameLevelDesc())) {
            helper.setText(R.id.room_name, item.getRoomName());
        } else {
            helper.setText(R.id.room_name, item.getRoomName() + "·" + item.getGameLevelDesc());
        }

        helper.setText(R.id.room_id, getContext().getString(R.string.room_list_roomid, item.getRoomNumber()));
        helper.setText(R.id.room_online, getContext().getString(R.string.room_list_online, item.getMemberCount() + ""));
        helper.setText(R.id.rtc_name, AppData.getInstance().getRtcNameByRtcType(item.getRtcType()));

        // 场景名称
        TextView sceneNameTv = helper.getView(R.id.room_scene);
        sceneNameTv.setText(item.getSceneTag());
        SceneTagColor sceneTagColor = sceneTagResId(item.getSceneType());
        sceneNameTv.setBackgroundColor(sceneTagColor.colorBg);
        sceneNameTv.setTextColor(sceneTagColor.colorText);

        // 右下角按钮
        if (btnText > 0) {
            helper.setText(R.id.room_enter, btnText);
        }

        // 跨房比赛的状态
        TextView tvStatus = helper.getView(R.id.tv_status);
        tvStatus.setBackground(ShapeUtils.createShape(null, null,
                new float[]{radius, radius, 0, 0, 0, 0, 0, 0},
                GradientDrawable.RECTANGLE, null, Color.parseColor("#cc000000")));
        if (item.getSceneType() == SceneType.CROSS_ROOM) {
            switch (item.pkStatus) {
                case PkStatus.MATCHING:
                    tvStatus.setVisibility(View.VISIBLE);
                    tvStatus.setText(R.string.matching);
                    break;
                case PkStatus.MATCHED:
                case PkStatus.STARTED:
                case PkStatus.PK_END:
                    tvStatus.setVisibility(View.VISIBLE);
                    tvStatus.setText(R.string.in_game);
                    break;
                default:
                    tvStatus.setVisibility(View.GONE);
                    break;
            }
        } else {
            tvStatus.setVisibility(View.GONE);
        }

        // 跨域状态显示
        TextView tvCrossAppStatus = helper.getView(R.id.tv_cross_app_status);
        if (item.getSceneType() == SceneType.CROSS_APP_MATCH) {
            if (item.teamStatus == CrossAppMatchStatus.TEAM) {
                tvCrossAppStatus.setText(R.string.in_a_team);
                tvCrossAppStatus.setVisibility(View.VISIBLE);
            } else {
                tvCrossAppStatus.setVisibility(View.GONE);
            }
        } else {
            tvCrossAppStatus.setVisibility(View.GONE);
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
            case SceneType.CUSTOM_SCENE:
                color.colorBg = Color.parseColor("#198de2");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.DANMAKU:
            case SceneType.VERTICAL_DANMAKU:
                color.colorBg = Color.parseColor("#00cbd2");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.DISCO:
                color.colorBg = Color.parseColor("#dd01cb");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.LEAGUE:
                color.colorBg = Color.parseColor("#cb1530");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.CROSS_APP_MATCH:
                color.colorBg = Color.parseColor("#0d0deb");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.AUDIO_3D:
                color.colorBg = Color.parseColor("#258cfb");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            default:
                color.colorBg = Color.parseColor("#f5f5f5");
                color.colorText = Color.parseColor("#999999");
                break;
        }
        return color;
    }

    @NonNull
    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(baseQuickAdapter);
    }

    /**
     * 根据场景id，返回场景信息的model
     */
    public static class SceneTagColor {
        public int colorBg;
        public int colorText;
    }
}