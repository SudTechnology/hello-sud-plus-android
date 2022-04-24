package tech.sud.mgp.hello.ui.scenes.custom.model;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;

/**
 * 游戏custom配置
 */
public class ConfigItemModel {

    public ConfigItemModel() {
    }

    public ConfigItemModel(String title,
                           String subTitle,
                           int type,
                           int value,
                           boolean hide,
                           boolean custom) {
        this.title = title;
        this.subTitle = subTitle;
        this.type = type;
        this.value = value;
        this.hide = hide;
        this.custom = custom;
    }

    @NonNull
    public String title;
    public String subTitle;
    public int value;//游戏系统
    public boolean hide;//ui
    public boolean custom;//custom
    public int type;//0value 1hide 2custom

    public List<OptionListBean> optionList = new ArrayList<>(2);

    public static class OptionListBean {
        public String title;
        public boolean isSeleted;
    }
}
    