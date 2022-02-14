package tech.sud.mgp.hello.ui.room.audio.gift.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.room.audio.gift.model.GiftModel;

public class GiftItemView extends ConstraintLayout {

    private ConstraintLayout itemGiftBg;
    private ImageView itemGiftImgIv;
    private TextView itemGiftNameTv;

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
        inflate(context, R.layout.audio_view_gift_item, this);
        itemGiftBg = findViewById(R.id.item_gift_bg);
        itemGiftImgIv = findViewById(R.id.item_gift_img_iv);
        itemGiftNameTv = findViewById(R.id.item_gift_name_tv);
    }

    public void setModel(GiftModel model) {
        itemGiftImgIv.setImageResource(model.giftImage);
        itemGiftNameTv.setText(model.giftName);
        if (model.checkState) {
            itemGiftBg.setSelected(true);
        } else {
            itemGiftBg.setSelected(false);
        }
    }

}
