package tech.sud.mgp.hello.ui.main.base.activity;

public class TabModel {
    public int index;
    public String text;
    public int iconId;
    public int selectedIconId;
    public boolean isLlmBot;

    public TabModel(int index, String text, int iconId) {
        this.index = index;
        this.text = text;
        this.iconId = iconId;
    }

    public TabModel(int index, String text, int iconId, int selectedIconId, boolean isLlmBot) {
        this.index = index;
        this.text = text;
        this.iconId = iconId;
        this.selectedIconId = selectedIconId;
        this.isLlmBot = isLlmBot;
    }

}
