package tech.sud.mgp.hello.ui.main.settings.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.utils.AppSharedPreferences;
import tech.sud.mgp.hello.ui.main.settings.model.LangCellType;
import tech.sud.mgp.hello.ui.main.settings.model.LangModel;
import tech.sud.mgp.hello.ui.main.utils.HSLanguageUtils;

public class LanguageViewModel extends BaseViewModel {

    private LangCellType mLangCellType;

    public List<LangModel> languages = new ArrayList<LangModel>();

    /**
     * 初始化当前语言
     */
    public LangCellType currentLanguage() {
        if (mLangCellType != null) {
            return mLangCellType;
        }
        String cellType = AppSharedPreferences.getSP().getString(AppSharedPreferences.KEY_LANG_CELL_TYPE);
        try {
            mLangCellType = LangCellType.valueOf(cellType);
            return mLangCellType;
        } catch (Exception e) {

        }
        return LangCellType.Follow;
    }

    /**
     * 初始化语言列表
     */
    public void createLangCellData(String[] langs) {
        languages.clear();
        LangCellType[] langTypes = LangCellType.values();
        for (int i = 0; i < langs.length; i++) {
            LangModel model = new LangModel();
            model.title = langs[i];
            model.cellType = langTypes[i];
            model.isSelected = model.cellType == mLangCellType;
            languages.add(model);
        }
    }

    /**
     * 点击选中语言
     */
    public void clickCell(LangModel model) {
        if (languages.size() > 0) {
            for (int i = 0; i < languages.size(); i++) {
                LangModel item = languages.get(i);
                item.isSelected = item.cellType == model.cellType;
            }
        }
    }

    /**
     * 修改语言成功后，保存数据
     */
    public void saveLangCellType(LangCellType type) {
        mLangCellType = type;
        AppSharedPreferences.getSP().put(AppSharedPreferences.KEY_LANG_CELL_TYPE, type.name());
    }

    /**
     * 语言
     * */
    public Locale converLocale(LangCellType cellType){
        Locale locale = Locale.US;
        switch (cellType){
            case Follow:
                locale = HSLanguageUtils.getSystemLanguage();
                break;
            case SChin:
                locale = Locale.CHINESE;
                break;
            case TChin:
                locale = Locale.TRADITIONAL_CHINESE;
                break;
            case Eng:
                break;
            case Arabic:
                locale = new Locale("ar","AE");
                break;
            case Indonesian:
                locale = new Locale("in","ID");
                break;
            case Malaysia:
                locale = new Locale("ms","MY");
                break;
            case Thai:
                locale = new Locale("th","TH");
                break;
            case Vietnamese:
                locale = new Locale("vi","VN");
                break;
            case Korean:
                locale = Locale.KOREA;
                break;
            case Spanish:
                locale = new Locale("es","ES");
                break;
            case Japanese:
                locale = Locale.JAPANESE;
                break;
        }
        return locale;
    }

}
