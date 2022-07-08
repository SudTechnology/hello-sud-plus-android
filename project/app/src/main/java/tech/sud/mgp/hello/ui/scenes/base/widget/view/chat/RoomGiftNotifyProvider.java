package tech.sud.mgp.hello.ui.scenes.base.widget.view.chat;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftNotifyDetailModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.utils.CenterImageSpan;

public class RoomGiftNotifyProvider extends BaseItemProvider<Object> {
    @Override
    public int getItemViewType() {
        return RoomChatAdapter.TYPE_GIFT_NOTIFY;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_room_gift_notify;
    }

    @Override
    public void convert(@NonNull BaseViewHolder baseViewHolder, Object o) {
        GiftNotifyDetailModel item = (GiftNotifyDetailModel) o;
        TextView textTv = baseViewHolder.getView(R.id.text_tv);
        loadImage(item, textTv);
    }

    public void loadImage(GiftNotifyDetailModel item, TextView textTv) {
        if (item.gift != null) {
            if (item.gift.type == 0) {
                loadLocalImage(item, textTv);
            } else if (item.gift.type == 1) {
                loadImageByUrl(item, textTv);
            }
        }
    }

    private void loadImageByUrl(GiftNotifyDetailModel item, TextView textTv) {
        int overSize = DensityUtils.dp2px(getContext(), 20);
        ImageLoader.loadSizeGift(textTv, item.gift.giftUrl, overSize,
                new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        creatText(item, textTv, null, overSize);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        creatText(item, textTv, resource, overSize);
                        return false;
                    }
                });
    }

    private void loadLocalImage(GiftNotifyDetailModel item, TextView textTv) {
        int overSize = DensityUtils.dp2px(getContext(), 20);
        ImageLoader.loadSizeGift(textTv, item.gift.giftSmallImage, overSize,
                new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        creatText(item, textTv, null, overSize);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        creatText(item, textTv, resource, overSize);
                        return false;
                    }
                });
    }

    private void creatText(GiftNotifyDetailModel item, TextView textTv, Drawable drawable, int overSize) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(item.sendUser.name);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#aaaaaa"));
        builder.append(" " + getContext().getString(R.string.audio_dialog_present) + " ", colorSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (item.toUser != null && item.toUser.name != null) {
            builder.append(item.toUser.name);
        }
        builder.append("  " + item.gift.giftName + " ");

        if (drawable != null) {
            builder.append("o");
            drawable.setBounds(0, 0, overSize, overSize);
            CenterImageSpan span = new CenterImageSpan(drawable);
            builder.setSpan(span, builder.length() - 1, builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            builder.append("x" + item.giftCount);
        }
        textTv.post(() -> textTv.setText(builder));
    }

}