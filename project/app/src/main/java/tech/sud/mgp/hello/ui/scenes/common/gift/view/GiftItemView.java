package tech.sud.mgp.hello.ui.scenes.common.gift.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.GiftId;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;

public class GiftItemView extends ConstraintLayout {

    private ConstraintLayout itemGiftBg;
    private ImageView itemGiftImgIv;
    private TextView itemGiftNameTv;
    private TextView tvPrice;
    private TextView tvFeature;
    private TextView tvEffect;
    private TextView tvCustom;
    public boolean isShowFlag; // 是否显示标记

    public GiftItemView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public GiftItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GiftItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_gift_item, this);
        itemGiftBg = findViewById(R.id.item_gift_bg);
        itemGiftImgIv = findViewById(R.id.item_gift_img_iv);
        itemGiftNameTv = findViewById(R.id.item_gift_name_tv);
        tvPrice = findViewById(R.id.tv_price);
        tvFeature = findViewById(R.id.tv_feature);
        tvEffect = findViewById(R.id.tv_effect);
        tvCustom = findViewById(R.id.tv_custom);
    }

    public void setModel(GiftModel model) {
        if (model.type == 0) {
            ImageLoader.loadDrawable(itemGiftImgIv, model.giftImage);
        } else {
            ImageLoader.loadImage(itemGiftImgIv, model.giftUrl);
        }
        itemGiftNameTv.setText(model.giftName);
        itemGiftBg.setSelected(model.checkState);
        tvPrice.setText(model.giftPrice + "");

        if (isShowFlag) {
            if (model.isFeature) {
                tvFeature.setVisibility(View.VISIBLE);
            } else {
                tvFeature.setVisibility(View.GONE);
            }
            if (model.isEffect) {
                tvEffect.setVisibility(View.VISIBLE);
            } else {
                tvEffect.setVisibility(View.GONE);
            }
        } else {
            tvFeature.setVisibility(View.GONE);
            tvEffect.setVisibility(View.GONE);
        }
        if (model.giftId == GiftId.ROCKET) {
            tvCustom.setVisibility(View.VISIBLE);
        } else {
            tvCustom.setVisibility(View.GONE);
        }
    }

}
