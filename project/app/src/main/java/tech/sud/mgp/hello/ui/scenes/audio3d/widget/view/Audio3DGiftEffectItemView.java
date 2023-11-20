package tech.sud.mgp.hello.ui.scenes.audio3d.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;

/**
 * 3D语聊房的礼物效果展示单个条目
 */
public class Audio3DGiftEffectItemView extends ConstraintLayout {

    private ImageView mIvAvatar;
    private TextView mTvName;
    private TextView mTvInfo;
    private ImageView mIvGiftIcon;
    private TextView mTvCount;

    public Audio3DGiftEffectItemView(@NonNull Context context) {
        this(context, null);
    }

    public Audio3DGiftEffectItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Audio3DGiftEffectItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), R.layout.view_audio3d_gift_effect_item, this);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mTvName = findViewById(R.id.tv_name);
        mTvInfo = findViewById(R.id.tv_info);
        mIvGiftIcon = findViewById(R.id.iv_gift_icon);
        mTvCount = findViewById(R.id.tv_gift_count);
    }

    public void initData(GiftModel giftModel, int giftCount, UserInfo fromUser, UserInfo toUser, boolean isAllSeat) {
        ImageLoader.loadAvatar(mIvAvatar, fromUser.icon);
        mTvName.setText(fromUser.name);
        mTvInfo.setText(getContext().getString(R.string.present_gift, giftModel.giftName));

        if (giftModel.type == 0) {
            ImageLoader.loadDrawable(mIvGiftIcon, giftModel.giftImage);
        } else if (giftModel.type == 1) {
            ImageLoader.loadImage(mIvGiftIcon, giftModel.giftUrl);
        }

        mTvCount.setText("x" + giftCount);
    }

}
