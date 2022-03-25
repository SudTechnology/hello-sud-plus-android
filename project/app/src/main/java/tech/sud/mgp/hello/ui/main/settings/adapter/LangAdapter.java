package tech.sud.mgp.hello.ui.main.settings.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.main.settings.model.LangModel;
import tech.sud.mgp.hello.ui.main.settings.model.TextType;

public class LangAdapter extends BaseMultiItemQuickAdapter<LangModel, BaseViewHolder> {

    public LangAdapter(@Nullable List<LangModel> data) {
        super(data);
        addItemType(TextType.TickPlain.getValue(), R.layout.item_setting_language);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, LangModel langModel) {
        if (langModel.getItemType() == TextType.TickPlain.getValue()){
            baseViewHolder.setText(R.id.setting_item_tick_title, langModel.title);
            baseViewHolder.setVisible(R.id.setting_item_tick_logo, langModel.isSelected);
        }

    }
}
