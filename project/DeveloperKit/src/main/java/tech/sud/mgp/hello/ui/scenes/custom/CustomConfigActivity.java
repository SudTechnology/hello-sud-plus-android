package tech.sud.mgp.hello.ui.scenes.custom;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.SudMGPWrapper.model.GameConfigModel;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.utils.GlobalCache;
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
    }

    @Override
    protected void initData() {
        super.initData();
        Object configModel = GlobalCache.getInstance().getSerializable(GlobalCache.CUSTOM_CONFIG_KEY);
        if (configModel instanceof GameConfigModel) {
            viewModel.configModel = (GameConfigModel) configModel;
            LogUtils.i("initData1=" + viewModel.configModel.gameSoundVolume);
        } else {
            viewModel.configModel = new GameConfigModel();
            LogUtils.i("initData2=" + viewModel.configModel.gameSoundVolume);
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        addConfigView();
    }

    private void addConfigView() {
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
                0, viewModel.configModel.gameCPU, false, false,
                getString(R.string.custom_config_game_cpu0),
                getString(R.string.custom_config_game_cpu1));
        item1View2.setListener(itemModel -> viewModel.configModel.gameCPU = itemModel.value);
        item1View2.setData(itemModel2);
        configContainer.addView(item1View2);

        //游戏中的声音
        CustomPageItem1View item1View3 = new CustomPageItem1View(this);
        ConfigItemModel itemModel3 = addItem(
                getString(R.string.custom_config_game_sound_control),
                getString(R.string.custom_config_game_sound_control_subtitle),
                0, viewModel.configModel.gameSoundControl, false, false,
                getString(R.string.custom_config_game_sound_control0),
                getString(R.string.custom_config_game_sound_control1));
        item1View3.setListener(itemModel -> viewModel.configModel.gameSoundControl = itemModel.value);
        item1View3.setData(itemModel3);
        configContainer.addView(item1View3);

        //游戏中的音量
        CustomPageItem2View item1View4 = new CustomPageItem2View(this);
        ConfigItemModel itemModel4 = addItem(
                getString(R.string.custom_config_game_sound_volume),
                getString(R.string.custom_config_game_sound_volume_subtitle),
                0, viewModel.configModel.gameSoundVolume, false, false,
                null, null);
        item1View4.setListener(itemModel -> viewModel.configModel.gameSoundVolume = itemModel.value);
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
                1, 0, viewModel.configModel.ui.gameSettle.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View6.setListener(itemModel -> viewModel.configModel.ui.gameSettle.hide = itemModel.hide);
        item1View6.setData(itemModel6);
        configContainer.addView(item1View6);

        //是否隐藏ping值
        CustomPageItem1View item1View7 = new CustomPageItem1View(this);
        ConfigItemModel itemModel7 = addItem(
                getString(R.string.custom_config_game_ping),
                getString(R.string.custom_config_game_ping_subtitle),
                1, 0, viewModel.configModel.ui.ping.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View7.setListener(itemModel -> viewModel.configModel.ui.ping.hide = itemModel.hide);
        item1View7.setData(itemModel7);
        configContainer.addView(item1View7);

        //是否隐藏版本信息
        CustomPageItem1View item1View8 = new CustomPageItem1View(this);
        ConfigItemModel itemModel8 = addItem(
                getString(R.string.custom_config_game_version),
                getString(R.string.custom_config_game_version_subtitle),
                1, 0, viewModel.configModel.ui.version.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View8.setListener(itemModel -> viewModel.configModel.ui.version.hide = itemModel.hide);
        item1View8.setData(itemModel8);
        configContainer.addView(item1View8);

        //是否隐藏段位信息
        CustomPageItem1View item1View9 = new CustomPageItem1View(this);
        ConfigItemModel itemModel9 = addItem(
                getString(R.string.custom_config_game_level),
                getString(R.string.custom_config_game_level_subtitle),
                1, 0, viewModel.configModel.ui.level.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View9.setListener(itemModel -> viewModel.configModel.ui.level.hide = itemModel.hide);
        item1View9.setData(itemModel9);
        configContainer.addView(item1View9);

        //是否隐藏大厅的『设置/音效』按钮
        CustomPageItem1View item1View10 = new CustomPageItem1View(this);
        ConfigItemModel itemModel10 = addItem(
                getString(R.string.custom_config_game_lobby_setting_btn),
                getString(R.string.custom_config_game_lobby_setting_btn_subtitle),
                1, 0, viewModel.configModel.ui.lobby_setting_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View10.setListener(itemModel -> viewModel.configModel.ui.lobby_setting_btn.hide = itemModel.hide);
        item1View10.setData(itemModel10);
        configContainer.addView(item1View10);

        //是否隐藏大厅的『帮助』按钮
        CustomPageItem1View item1View11 = new CustomPageItem1View(this);
        ConfigItemModel itemModel11 = addItem(
                getString(R.string.custom_config_game_lobby_help_btn),
                getString(R.string.custom_config_game_lobby_help_btn_subtitle),
                1, 0, viewModel.configModel.ui.lobby_help_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View11.setListener(itemModel -> viewModel.configModel.ui.lobby_help_btn.hide = itemModel.hide);
        item1View11.setData(itemModel11);
        configContainer.addView(item1View11);

        //是否隐藏大厅游戏位
        CustomPageItem1View item1View13 = new CustomPageItem1View(this);
        ConfigItemModel itemModel13 = addItem(
                getString(R.string.custom_config_game_lobby_players_hide),
                getString(R.string.custom_config_game_lobby_players_hide_subtitle),
                1, 0, viewModel.configModel.ui.lobby_players.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View13.setListener(itemModel -> viewModel.configModel.ui.lobby_players.hide = itemModel.hide);
        item1View13.setData(itemModel13);
        configContainer.addView(item1View13);

        //是否隐藏大厅游戏位上队长标识
        CustomPageItem1View item1View14 = new CustomPageItem1View(this);
        ConfigItemModel itemModel14 = addItem(
                getString(R.string.custom_config_game_lobby_player_captain_icon),
                getString(R.string.custom_config_game_lobby_player_captain_icon_subtitle),
                1, 0, viewModel.configModel.ui.lobby_player_captain_icon.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View14.setListener(itemModel -> viewModel.configModel.ui.lobby_player_captain_icon.hide = itemModel.hide);
        item1View14.setData(itemModel14);
        configContainer.addView(item1View14);

        //是否隐藏大厅游戏位上『踢人』按钮
        CustomPageItem1View item1View15 = new CustomPageItem1View(this);
        ConfigItemModel itemModel15 = addItem(
                getString(R.string.custom_config_game_lobby_player_kickout_icon),
                getString(R.string.custom_config_game_lobby_player_kickout_icon_subtitle),
                1, 0, viewModel.configModel.ui.lobby_player_kickout_icon.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View15.setListener(itemModel -> viewModel.configModel.ui.lobby_player_kickout_icon.hide = itemModel.hide);
        item1View15.setData(itemModel15);
        configContainer.addView(item1View15);

        //是否隐藏大厅的玩法规则描述文字
        CustomPageItem1View item1View16 = new CustomPageItem1View(this);
        ConfigItemModel itemModel16 = addItem(
                getString(R.string.custom_config_game_lobby_rule),
                getString(R.string.custom_config_game_lobby_rule_subtitle),
                1, 0, viewModel.configModel.ui.lobby_rule.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View16.setListener(itemModel -> viewModel.configModel.ui.lobby_rule.hide = itemModel.hide);
        item1View16.setData(itemModel16);
        configContainer.addView(item1View16);

        //是否隐藏大厅的玩法设置
        CustomPageItem1View item1View17 = new CustomPageItem1View(this);
        ConfigItemModel itemModel17 = addItem(
                getString(R.string.custom_config_game_lobby_game_setting),
                getString(R.string.custom_config_game_lobby_game_setting_subtitle),
                1, 0, viewModel.configModel.ui.lobby_game_setting.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View17.setListener(itemModel -> viewModel.configModel.ui.lobby_game_setting.hide = itemModel.hide);
        item1View17.setData(itemModel17);
        configContainer.addView(item1View17);

        //是否隐藏『加入游戏』按钮
        CustomPageItem1View item1View19 = new CustomPageItem1View(this);
        ConfigItemModel itemModel19 = addItem(
                getString(R.string.custom_config_game_join_btn_hide),
                getString(R.string.custom_config_game_join_btn_hide_subtitle),
                1, 0, viewModel.configModel.ui.join_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View19.setListener(itemModel -> viewModel.configModel.ui.join_btn.hide = itemModel.hide);
        item1View19.setData(itemModel19);
        configContainer.addView(item1View19);

        //是否隐藏『退出游戏』按钮
        CustomPageItem1View item1View21 = new CustomPageItem1View(this);
        ConfigItemModel itemModel21 = addItem(
                getString(R.string.custom_config_game_cancel_join_btn_hide),
                getString(R.string.custom_config_game_cancel_join_btn_hide_subtitle),
                1, 0, viewModel.configModel.ui.cancel_join_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View21.setListener(itemModel -> viewModel.configModel.ui.cancel_join_btn.hide = itemModel.hide);
        item1View21.setData(itemModel21);
        configContainer.addView(item1View21);

        //是否隐藏『准备』按钮
        CustomPageItem1View item1View23 = new CustomPageItem1View(this);
        ConfigItemModel itemModel23 = addItem(
                getString(R.string.custom_config_game_ready_btn_hide),
                getString(R.string.custom_config_game_ready_btn_hide_subtitle),
                1, 0, viewModel.configModel.ui.ready_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View23.setListener(itemModel -> viewModel.configModel.ui.ready_btn.hide = itemModel.hide);
        item1View23.setData(itemModel23);
        configContainer.addView(item1View23);

        // 是否隐藏『取消准备』按钮
        CustomPageItem1View item1View25 = new CustomPageItem1View(this);
        ConfigItemModel itemModel25 = addItem(
                getString(R.string.custom_config_game_cancel_ready_btn_hide),
                getString(R.string.custom_config_game_cancel_ready_btn_hide_subtitle),
                1, 0, viewModel.configModel.ui.cancel_ready_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View25.setListener(itemModel -> viewModel.configModel.ui.cancel_ready_btn.hide = itemModel.hide);
        item1View25.setData(itemModel25);
        configContainer.addView(item1View25);

        // 是否隐藏『开始游戏』按钮
        CustomPageItem1View item1View27 = new CustomPageItem1View(this);
        ConfigItemModel itemModel27 = addItem(
                getString(R.string.custom_config_game_start_btn_hide),
                getString(R.string.custom_config_game_start_btn_hide_subtitle),
                1, 0, viewModel.configModel.ui.start_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View27.setListener(itemModel -> viewModel.configModel.ui.start_btn.hide = itemModel.hide);
        item1View27.setData(itemModel27);
        configContainer.addView(item1View27);

        // 是否隐藏『分享』按钮
        CustomPageItem1View item1View29 = new CustomPageItem1View(this);
        ConfigItemModel itemModel29 = addItem(
                getString(R.string.custom_config_game_share_btn_hide),
                getString(R.string.custom_config_game_share_btn_hide_subtitle),
                1, 0, viewModel.configModel.ui.share_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View29.setListener(itemModel -> viewModel.configModel.ui.share_btn.hide = itemModel.hide);
        item1View29.setData(itemModel29);
        configContainer.addView(item1View29);

        // 是否隐藏战斗场景中的『设置/音效』按钮
        CustomPageItem1View item1View30 = new CustomPageItem1View(this);
        ConfigItemModel itemModel30 = addItem(
                getString(R.string.custom_config_game_setting_btn_hide),
                getString(R.string.custom_config_game_setting_btn_hide_subtitle),
                1, 0, viewModel.configModel.ui.game_setting_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View30.setListener(itemModel -> viewModel.configModel.ui.game_setting_btn.hide = itemModel.hide);
        item1View30.setData(itemModel30);
        configContainer.addView(item1View30);

        // 是否隐藏战斗场景中的『设置/音效』按钮
        CustomPageItem1View item1View31 = new CustomPageItem1View(this);
        ConfigItemModel itemModel31 = addItem(
                getString(R.string.custom_config_game_help_btn_hide),
                getString(R.string.custom_config_game_help_btn_hide_subtitle),
                1, 0, viewModel.configModel.ui.game_help_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View31.setListener(itemModel -> viewModel.configModel.ui.game_help_btn.hide = itemModel.hide);
        item1View31.setData(itemModel31);
        configContainer.addView(item1View31);

        // 是否隐藏背景图，包括大厅和战斗
        CustomPageItem1View item1View34 = new CustomPageItem1View(this);
        ConfigItemModel itemModel34 = addItem(
                getString(R.string.custom_config_game_bg_hide),
                getString(R.string.custom_config_game_bg_hide_subtitle),
                1, 0, viewModel.configModel.ui.game_bg.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View34.setListener(itemModel -> viewModel.configModel.ui.game_bg.hide = itemModel.hide);
        item1View34.setData(itemModel34);
        configContainer.addView(item1View34);

        // region ------- Custom

        // Custom分类
        CustomPageTitleView titleViewCustom = new CustomPageTitleView(this);
        titleViewCustom.setData(addTitle(getString(R.string.custom)));
        configContainer.addView(titleViewCustom);

        // 点击大厅游戏位加入游戏
        CustomPageItem1View item1View12 = new CustomPageItem1View(this);
        ConfigItemModel itemModel12 = addItem(
                getString(R.string.custom_config_game_lobby_players_custom),
                getString(R.string.custom_config_game_lobby_players_custom_subtitle),
                2, 0, false, viewModel.configModel.ui.lobby_players.custom,
                getString(R.string.custom_config_game_custom_false),
                getString(R.string.custom_config_game_custom_true));
        item1View12.setListener(itemModel -> viewModel.configModel.ui.lobby_players.custom = itemModel.custom);
        item1View12.setData(itemModel12);
        configContainer.addView(item1View12);

        //『加入游戏』按钮抛事件
        CustomPageItem1View item1View18 = new CustomPageItem1View(this);
        ConfigItemModel itemModel18 = addItem(
                getString(R.string.custom_config_game_join_btn_custom),
                getString(R.string.custom_config_game_join_btn_custom_subtitle),
                2, 0, false, viewModel.configModel.ui.join_btn.custom,
                getString(R.string.custom_config_game_custom_false),
                getString(R.string.custom_config_game_custom_true));
        item1View18.setListener(itemModel -> viewModel.configModel.ui.join_btn.custom = itemModel.custom);
        item1View18.setData(itemModel18);
        configContainer.addView(item1View18);

        //『退出游戏』按钮抛事件
        CustomPageItem1View item1View20 = new CustomPageItem1View(this);
        ConfigItemModel itemModel20 = addItem(
                getString(R.string.custom_config_game_cancel_join_btn_custom),
                getString(R.string.custom_config_game_cancel_join_btn_custom_subtitle),
                2, 0, false, viewModel.configModel.ui.cancel_join_btn.custom,
                getString(R.string.custom_config_game_custom_false),
                getString(R.string.custom_config_game_custom_true));
        item1View20.setListener(itemModel -> viewModel.configModel.ui.cancel_join_btn.custom = itemModel.custom);
        item1View20.setData(itemModel20);
        configContainer.addView(item1View20);

        //『准备』按钮抛事件
        CustomPageItem1View item1View22 = new CustomPageItem1View(this);
        ConfigItemModel itemModel22 = addItem(
                getString(R.string.custom_config_game_ready_btn_custom),
                getString(R.string.custom_config_game_ready_btn_custom_subtitle),
                2, 0, false, viewModel.configModel.ui.ready_btn.custom,
                getString(R.string.custom_config_game_custom_false),
                getString(R.string.custom_config_game_custom_true));
        item1View22.setListener(itemModel -> viewModel.configModel.ui.ready_btn.custom = itemModel.custom);
        item1View22.setData(itemModel22);
        configContainer.addView(item1View22);

        //『取消准备』按钮抛事件
        CustomPageItem1View item1View24 = new CustomPageItem1View(this);
        ConfigItemModel itemModel24 = addItem(
                getString(R.string.custom_config_game_cancel_ready_btn_custom),
                getString(R.string.custom_config_game_cancel_ready_btn_custom_subtitle),
                2, 0, false, viewModel.configModel.ui.cancel_ready_btn.custom,
                getString(R.string.custom_config_game_custom_false),
                getString(R.string.custom_config_game_custom_true));
        item1View24.setListener(itemModel -> viewModel.configModel.ui.cancel_ready_btn.custom = itemModel.custom);
        item1View24.setData(itemModel24);
        configContainer.addView(item1View24);

        //『开始游戏』按钮抛事件
        CustomPageItem1View item1View26 = new CustomPageItem1View(this);
        ConfigItemModel itemModel26 = addItem(
                getString(R.string.custom_config_game_start_btn_custom),
                getString(R.string.custom_config_game_start_btn_custom_subtitle),
                2, 0, false, viewModel.configModel.ui.start_btn.custom,
                getString(R.string.custom_config_game_custom_false),
                getString(R.string.custom_config_game_custom_true));
        item1View26.setListener(itemModel -> viewModel.configModel.ui.start_btn.custom = itemModel.custom);
        item1View26.setData(itemModel26);
        configContainer.addView(item1View26);

        //『分享』按钮抛事件
        CustomPageItem1View item1View28 = new CustomPageItem1View(this);
        ConfigItemModel itemModel28 = addItem(
                getString(R.string.custom_config_game_share_btn_custom),
                getString(R.string.custom_config_game_share_btn_custom_subtitle),
                2, 0, false, viewModel.configModel.ui.share_btn.custom,
                getString(R.string.custom_config_game_custom_false),
                getString(R.string.custom_config_game_custom_true));
        item1View28.setListener(itemModel -> viewModel.configModel.ui.share_btn.custom = itemModel.custom);
        item1View28.setData(itemModel28);
        configContainer.addView(item1View28);

        // 结算界面中的『关闭』按钮抛事件
        CustomPageItem1View item1View32 = new CustomPageItem1View(this);
        ConfigItemModel itemModel32 = addItem(
                getString(R.string.custom_config_game_settle_close_btn_custom),
                getString(R.string.custom_config_game_settle_close_btn_custom_subtitle),
                2, 0, false, viewModel.configModel.ui.game_settle_close_btn.custom,
                getString(R.string.custom_config_game_settle_close_btn_custom_false),
                getString(R.string.custom_config_game_settle_close_btn_custom_true));
        item1View32.setListener(itemModel -> viewModel.configModel.ui.game_settle_close_btn.custom = itemModel.custom);
        item1View32.setData(itemModel32);
        configContainer.addView(item1View32);

        // 结算界面中的『再来一局』按钮抛事件
        CustomPageItem1View item1View33 = new CustomPageItem1View(this);
        ConfigItemModel itemModel33 = addItem(
                getString(R.string.custom_config_game_settle_again_btn_custom),
                getString(R.string.custom_config_game_settle_again_btn_custom_subtitle),
                2, 0, false, viewModel.configModel.ui.game_settle_again_btn.custom,
                getString(R.string.custom_config_game_settle_again_btn_custom_false),
                getString(R.string.custom_config_game_settle_again_btn_custom_true));
        item1View33.setListener(itemModel -> viewModel.configModel.ui.game_settle_again_btn.custom = itemModel.custom);
        item1View33.setData(itemModel33);
        configContainer.addView(item1View33);

        // 自定义阻止换座位
        CustomPageItem1View item1View35 = new CustomPageItem1View(this);
        ConfigItemModel itemModel35 = addItem(
                getString(R.string.custom_config_game_block_change_seat_custom),
                getString(R.string.custom_config_game_block_change_seat_custom_subtitle),
                2, 0, false, viewModel.configModel.ui.block_change_seat.custom,
                getString(R.string.custom_config_game_block_change_seat_custom_false),
                getString(R.string.custom_config_game_block_change_seat_custom_true));
        item1View35.setListener(itemModel -> viewModel.configModel.ui.block_change_seat.custom = itemModel.custom);
        item1View35.setData(itemModel35);
        configContainer.addView(item1View35);
        // endregion ------- Custom
    }

    private ConfigItemModel addTitle(String title) {
        ConfigItemModel titleModel = new ConfigItemModel();
        titleModel.title = title;
        return titleModel;
    }

    private ConfigItemModel addItem(String title, String subTitle, int type, int value, boolean hide,
                                    boolean custom, String optionTitle1, String optionTitle2) {
        ConfigItemModel itemModel = new ConfigItemModel(title, subTitle, type, value, hide, custom);
        if (type == 0) {
            if (optionTitle1 != null && optionTitle2 != null) {
                ConfigItemModel.OptionListBean bean1 = new ConfigItemModel.OptionListBean();
                bean1.title = optionTitle1;
                bean1.isSeleted = (value == 0);

                ConfigItemModel.OptionListBean bean2 = new ConfigItemModel.OptionListBean();
                bean2.title = optionTitle2;
                bean2.isSeleted = (value == 1);

                itemModel.optionList.add(bean1);
                itemModel.optionList.add(bean2);
            }
        } else if (type == 1) {
            ConfigItemModel.OptionListBean bean1 = new ConfigItemModel.OptionListBean();
            bean1.title = optionTitle1;
            bean1.isSeleted = !hide;

            ConfigItemModel.OptionListBean bean2 = new ConfigItemModel.OptionListBean();
            bean2.title = optionTitle2;
            bean2.isSeleted = hide;

            itemModel.optionList.add(bean1);
            itemModel.optionList.add(bean2);
        } else if (type == 2) {
            ConfigItemModel.OptionListBean bean1 = new ConfigItemModel.OptionListBean();
            bean1.title = optionTitle1;
            bean1.isSeleted = !custom;

            ConfigItemModel.OptionListBean bean2 = new ConfigItemModel.OptionListBean();
            bean2.title = optionTitle2;
            bean2.isSeleted = custom;

            itemModel.optionList.add(bean1);
            itemModel.optionList.add(bean2);
        }
        viewModel.items.add(itemModel);
        return itemModel;
    }

    @Override
    protected void onDestroy() {
        viewModel.saveCustomConfig();
        super.onDestroy();
    }
}
