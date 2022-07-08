package tech.sud.mgp.hello.ui.main.settings.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class LangModel implements MultiItemEntity {
    public LangCellType cellType;
    public String title;
    public Boolean isSelected;

    @Override
    public int getItemType() {
        return TextType.TickPlain.getValue();
    }
}
