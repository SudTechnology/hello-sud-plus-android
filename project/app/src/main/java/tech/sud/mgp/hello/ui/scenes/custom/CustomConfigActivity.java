package tech.sud.mgp.hello.ui.scenes.custom;

import android.widget.LinearLayout;
import android.widget.TextView;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.SudMGPWrapper.model.GameConfigModel;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.utils.GlobalCache;
import tech.sud.mgp.hello.ui.scenes.custom.dialog.CustomApiDialog;
import tech.sud.mgp.hello.ui.scenes.custom.model.ConfigItemModel;
import tech.sud.mgp.hello.ui.scenes.custom.view.CustomPageItem1View;
import tech.sud.mgp.hello.ui.scenes.custom.view.CustomPageItem2View;
import tech.sud.mgp.hello.ui.scenes.custom.view.CustomPageTitleView;
import tech.sud.mgp.hello.ui.scenes.custom.viewmodel.CustomConfigViewModel;

/**
 * custom配置
 */
public class CustomConfigActivity extends BaseActivity {

    private TextView gameTipTv;
    private LinearLayout configContainer;
    private CustomConfigViewModel viewModel = new CustomConfigViewModel();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_custom_config;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        gameTipTv = findViewById(R.id.game_tip_tv);
        configContainer = findViewById(R.id.config_container);
        gameTipTv.setOnClickListener(v -> {
            CustomApiDialog dialog = new CustomApiDialog();
            dialog.show(getSupportFragmentManager(), null);
        });
    }

    @Override
    protected void initData() {
        super.initData();
        Object configModel = GlobalCache.getInstance().getSerializable(GlobalCache.CUSTOM_CONFIG_KEY);
        if (configModel instanceof GameConfigModel){
            viewModel.configModel = (GameConfigModel) configModel;
        }else {
            viewModel.configModel = new GameConfigModel();
        }

    }

    @Override
    protected void setListeners() {
        super.setListeners();
        addConfigView();
    }

    private void addConfigView(){
        viewModel.items.clear();
        configContainer.removeAllViews();

        //游戏系统
        CustomPageTitleView titleView1 = new CustomPageTitleView(this);
        ConfigItemModel itemModel1 = addTitle(getString(R.string.custom_config_game_system));
        titleView1.setData(itemModel1);
        configContainer.addView(titleView1);

        //游戏CPU
        CustomPageItem1View item1View2 = new CustomPageItem1View(this);
        ConfigItemModel itemModel2 = addItem(
                getString(R.string.custom_config_game_cpu),
                getString(R.string.custom_config_game_cpu_subtitle),
                0,viewModel.configModel.gameCPU,false,false,
                getString(R.string.custom_config_game_cpu0),
                getString(R.string.custom_config_game_cpu1));
        item1View2.setData(itemModel2);
        configContainer.addView(item1View2);

        //游戏中的声音
        CustomPageItem1View item1View3 = new CustomPageItem1View(this);
        ConfigItemModel itemModel3 = addItem(
                getString(R.string.custom_config_game_sound_control),
                getString(R.string.custom_config_game_sound_control_subtitle),
                0,viewModel.configModel.gameSoundControl,false,false,
                getString(R.string.custom_config_game_sound_control0),
                getString(R.string.custom_config_game_sound_control1));
        item1View3.setData(itemModel3);
        configContainer.addView(item1View3);

        //游戏中的音量
        CustomPageItem2View item1View4 = new CustomPageItem2View(this);
        ConfigItemModel itemModel4 = addItem(
                getString(R.string.custom_config_game_sound_volume),
                getString(R.string.custom_config_game_sound_volume_subtitle),
                0,viewModel.configModel.gameSoundVolume,false,false,
                null,null);
        item1View4.setData(itemModel4);
        configContainer.addView(item1View4);

        //UI
        CustomPageTitleView titleView5 = new CustomPageTitleView(this);
        ConfigItemModel itemModel5 = addTitle("UI");
        titleView5.setData(itemModel5);
        configContainer.addView(titleView5);

        //是否隐藏结算页面
        CustomPageItem1View item1View6 = new CustomPageItem1View(this);
        ConfigItemModel itemModel6 = addItem(
                getString(R.string.custom_config_game_settle),
                getString(R.string.custom_config_game_settle_subtitle),
                1,0,viewModel.configModel.ui.gameSettle.hide,false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View6.setData(itemModel6);
        configContainer.addView(item1View6);

        //是否隐藏ping值
        CustomPageItem1View item1View7 = new CustomPageItem1View(this);
        ConfigItemModel itemModel7 = addItem(
                getString(R.string.custom_config_game_ping),
                getString(R.string.custom_config_game_ping_subtitle),
                1,0,viewModel.configModel.ui.ping.hide,false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View7.setData(itemModel7);
        configContainer.addView(item1View7);

        //是否隐藏版本信息
        CustomPageItem1View item1View8 = new CustomPageItem1View(this);
        ConfigItemModel itemModel8 = addItem(
                getString(R.string.custom_config_game_version),
                getString(R.string.custom_config_game_version_subtitle),
                1,0,viewModel.configModel.ui.version.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View8.setData(itemModel8);
        configContainer.addView(item1View8);

        //是否隐藏段位信息
        CustomPageItem1View item1View9 = new CustomPageItem1View(this);
        ConfigItemModel itemModel9 = addItem(
                getString(R.string.custom_config_game_level),
                getString(R.string.custom_config_game_level_subtitle),
                1,0,viewModel.configModel.ui.level.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View9.setData(itemModel9);
        configContainer.addView(item1View9);

        //是否隐藏大厅的『设置/音效』按钮
        CustomPageItem1View item1View10 = new CustomPageItem1View(this);
        ConfigItemModel itemModel10 = addItem(
                getString(R.string.custom_config_game_lobby_setting_btn),
                getString(R.string.custom_config_game_lobby_setting_btn_subtitle),
                1,0,viewModel.configModel.ui.lobby_setting_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View10.setData(itemModel10);
        configContainer.addView(item1View10);

        //是否隐藏大厅的『帮助』按钮
        CustomPageItem1View item1View11 = new CustomPageItem1View(this);
        ConfigItemModel itemModel11 = addItem(
                getString(R.string.custom_config_game_lobby_help_btn),
                getString(R.string.custom_config_game_lobby_help_btn_subtitle),
                1,0,viewModel.configModel.ui.lobby_help_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View11.setData(itemModel11);
        configContainer.addView(item1View11);

    }

    private ConfigItemModel addTitle(String title){
        ConfigItemModel titleModel = new ConfigItemModel();
        titleModel.title = title;
        return titleModel;
    }

    private ConfigItemModel addItem(String title,
                         String subTitle,
                         int type,
                         int value,
                         boolean hide,
                         boolean custom,
                         String optionTitle1,
                         String optionTitle2){
        ConfigItemModel itemModel = new ConfigItemModel(title, subTitle, type, value, hide, custom);
        if (type == 0){
            if (optionTitle1!=null&&optionTitle2!=null){
                ConfigItemModel.OptionListBean bean1 = new ConfigItemModel.OptionListBean();
                bean1.title= optionTitle1;
                bean1.isSeleted = (value==0);

                ConfigItemModel.OptionListBean bean2 = new ConfigItemModel.OptionListBean();
                bean2.title= optionTitle2;
                bean2.isSeleted =(value==1);

                itemModel.optionList.add(bean1);
                itemModel.optionList.add(bean2);
            }
        }else  if (type == 1){
            ConfigItemModel.OptionListBean bean1 = new ConfigItemModel.OptionListBean();
            bean1.title= optionTitle1;
            bean1.isSeleted = !hide;

            ConfigItemModel.OptionListBean bean2 = new ConfigItemModel.OptionListBean();
            bean2.title= optionTitle2;
            bean2.isSeleted = hide;

            itemModel.optionList.add(bean1);
            itemModel.optionList.add(bean2);
        }else if (type == 2){
            ConfigItemModel.OptionListBean bean1 = new ConfigItemModel.OptionListBean();
            bean1.title= optionTitle1;
            bean1.isSeleted = !custom;

            ConfigItemModel.OptionListBean bean2 = new ConfigItemModel.OptionListBean();
            bean2.title= optionTitle2;
            bean2.isSeleted = custom;

            itemModel.optionList.add(bean1);
            itemModel.optionList.add(bean2);
        }
        return itemModel;
    }

}
