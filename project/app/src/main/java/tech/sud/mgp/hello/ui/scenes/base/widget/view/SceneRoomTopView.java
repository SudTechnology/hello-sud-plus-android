package tech.sud.mgp.hello.ui.scenes.base.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;

/**
 * 语聊房顶部的View
 */
public class SceneRoomTopView extends ConstraintLayout {

    private TextView tvName;
    private TextView tvId;
    private TextView tvNumber;
    private View containerSelectGame;
    private TextView tvSelectGame;
    private TextView tvFinishGame;
    private ImageView ivMore;
    private LinearLayout endContainer;

    public SceneRoomTopView(@NonNull Context context) {
        this(context, null);
    }

    public SceneRoomTopView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SceneRoomTopView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.view_scene_room_top, this);
        tvName = findViewById(R.id.top_tv_name);
        tvId = findViewById(R.id.top_tv_room_id);
        tvNumber = findViewById(R.id.top_tv_room_number);
        containerSelectGame = findViewById(R.id.top_container_select_game);
        tvSelectGame = findViewById(R.id.top_tv_select_game);
        ivMore = findViewById(R.id.top_iv_more);
        tvFinishGame = findViewById(R.id.top_tv_finish_game);
        endContainer = findViewById(R.id.end_container);
    }

    /** 设置房间名称 */
    public void setName(String value) {
        tvName.setText(value);
    }

    /** 设置房号 */
    public void setId(String value) {
        tvId.setText(value);
    }

    /** 设置房间人数 */
    public void setNumber(String value) {
        tvNumber.setText(value);
    }

    /** 设置选择游戏中的文字内容 */
    public void setSelectGameName(String value) {
        tvSelectGame.setText(value);
    }

    /** 设置选择游戏点击监听 */
    public void setSelectGameClickListener(OnClickListener listener) {
        containerSelectGame.setOnClickListener(listener);
    }

    /** 设置更多按钮的点击监听 */
    public void setMoreOnClickListener(OnClickListener listener) {
        ivMore.setOnClickListener(listener);
    }

    /** 设置选择游戏的可见性 */
    public void setSelectGameVisible(boolean isVisible) {
        containerSelectGame.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    /** 设置结束游戏的可见性 */
    public void setFinishGameVisible(boolean isVisible) {
        tvFinishGame.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    /** 设置结束游戏点击监听 */
    public void setFinishGameOnClickListener(OnClickListener listener) {
        tvFinishGame.setOnClickListener(listener);
    }

    /** 添加一个自定义的View */
    public void addCustomView(View view, LinearLayout.LayoutParams params) {
        endContainer.addView(view, 0, params);
    }

}
