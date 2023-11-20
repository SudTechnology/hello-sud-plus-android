package tech.sud.mgp.hello.ui.scenes.audio3d.widget.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.ui.scenes.audio3d.model.EmojiModel;

public class EmojiAdapter extends BaseQuickAdapter<EmojiModel, BaseViewHolder> {

    public EmojiAdapter() {
        super(R.layout.item_audio3d_emoji);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, EmojiModel model) {
        holder.setImageResource(R.id.iv_icon, model.resId);
        holder.setText(R.id.tv_name, model.name);

        View viewRoot = holder.getView(R.id.view_root);
        ViewUtils.setViewClickAnim(viewRoot);
    }

}
