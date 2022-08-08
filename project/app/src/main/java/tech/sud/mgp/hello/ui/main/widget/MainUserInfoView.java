package tech.sud.mgp.hello.ui.main.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.ImageLoader;

/**
 * 首页顶部的个人信息弹窗
 */
public class MainUserInfoView extends ConstraintLayout {

    private ImageView ivIcon;
    private TextView tvName;
    private TextView tvUserId;

    public MainUserInfoView(@NonNull Context context) {
        this(context, null);
    }

    public MainUserInfoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainUserInfoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
        initData();
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        inflate(context, R.layout.view_main_user_info, this);
        ivIcon = findViewById(R.id.user_info_iv_icon);
        tvName = findViewById(R.id.user_info_tv_name);
        tvUserId = findViewById(R.id.tv_user_id);
    }

    private void initData() {
        ImageLoader.loadAvatar(ivIcon, HSUserInfo.avatar);
        tvName.setText(HSUserInfo.nickName);
        tvUserId.setText(getContext().getString(R.string.setting_userid, HSUserInfo.userId + ""));
    }

    public void setAvatarOnClickListener(OnClickListener listener) {
        ivIcon.setOnClickListener(listener);
    }

}
