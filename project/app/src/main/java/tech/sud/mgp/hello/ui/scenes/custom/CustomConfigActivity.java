package tech.sud.mgp.hello.ui.scenes.custom;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;

import tech.sud.mgp.SudMGPWrapper.model.GameConfigModel;
import tech.sud.mgp.hello.R;
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
        CustomPageItem1View item1View = new CustomPageItem1View(this);
        ConfigItemModel configItemModel = addItem(
                getString(R.string.custom_config_game_settle),
                getString(R.string.custom_config_game_settle_subtitle),
                1, 0, viewModel.configModel.ui.gameSettle.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.gameSettle.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        //是否隐藏ping值
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_ping),
                getString(R.string.custom_config_game_ping_subtitle),
                1, 0, viewModel.configModel.ui.ping.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.ping.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        //是否隐藏版本信息
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_version),
                getString(R.string.custom_config_game_version_subtitle),
                1, 0, viewModel.configModel.ui.version.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.version.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        //是否隐藏段位信息
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_level),
                getString(R.string.custom_config_game_level_subtitle),
                1, 0, viewModel.configModel.ui.level.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.level.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        //是否隐藏大厅的『设置/音效』按钮
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_lobby_setting_btn),
                getString(R.string.custom_config_game_lobby_setting_btn_subtitle),
                1, 0, viewModel.configModel.ui.lobby_setting_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.lobby_setting_btn.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        //是否隐藏大厅的『帮助』按钮
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_lobby_help_btn),
                getString(R.string.custom_config_game_lobby_help_btn_subtitle),
                1, 0, viewModel.configModel.ui.lobby_help_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.lobby_help_btn.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        //是否隐藏大厅游戏位
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_lobby_players_hide),
                getString(R.string.custom_config_game_lobby_players_hide_subtitle),
                1, 0, viewModel.configModel.ui.lobby_players.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.lobby_players.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        //是否隐藏大厅游戏位上队长标识
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_lobby_player_captain_icon),
                getString(R.string.custom_config_game_lobby_player_captain_icon_subtitle),
                1, 0, viewModel.configModel.ui.lobby_player_captain_icon.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.lobby_player_captain_icon.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        //是否隐藏大厅游戏位上『踢人』按钮
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_lobby_player_kickout_icon),
                getString(R.string.custom_config_game_lobby_player_kickout_icon_subtitle),
                1, 0, viewModel.configModel.ui.lobby_player_kickout_icon.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.lobby_player_kickout_icon.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        //是否隐藏大厅的玩法规则描述文字
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_lobby_rule),
                getString(R.string.custom_config_game_lobby_rule_subtitle),
                1, 0, viewModel.configModel.ui.lobby_rule.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.lobby_rule.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        //是否隐藏大厅的玩法设置
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_lobby_game_setting),
                getString(R.string.custom_config_game_lobby_game_setting_subtitle),
                1, 0, viewModel.configModel.ui.lobby_game_setting.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.lobby_game_setting.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        //是否隐藏『加入游戏』按钮
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_join_btn_hide),
                getString(R.string.custom_config_game_join_btn_hide_subtitle),
                1, 0, viewModel.configModel.ui.join_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.join_btn.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        //是否隐藏『退出游戏』按钮
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_cancel_join_btn_hide),
                getString(R.string.custom_config_game_cancel_join_btn_hide_subtitle),
                1, 0, viewModel.configModel.ui.cancel_join_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.cancel_join_btn.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        //是否隐藏『准备』按钮
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_ready_btn_hide),
                getString(R.string.custom_config_game_ready_btn_hide_subtitle),
                1, 0, viewModel.configModel.ui.ready_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.ready_btn.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        // 是否隐藏『取消准备』按钮
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_cancel_ready_btn_hide),
                getString(R.string.custom_config_game_cancel_ready_btn_hide_subtitle),
                1, 0, viewModel.configModel.ui.cancel_ready_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.cancel_ready_btn.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        // 是否隐藏『开始游戏』按钮
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_start_btn_hide),
                getString(R.string.custom_config_game_start_btn_hide_subtitle),
                1, 0, viewModel.configModel.ui.start_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.start_btn.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        // 是否隐藏『分享』按钮
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_share_btn_hide),
                getString(R.string.custom_config_game_share_btn_hide_subtitle),
                1, 0, viewModel.configModel.ui.share_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.share_btn.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        // 是否隐藏战斗场景中的『设置/音效』按钮
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_setting_btn_hide),
                getString(R.string.custom_config_game_setting_btn_hide_subtitle),
                1, 0, viewModel.configModel.ui.game_setting_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.game_setting_btn.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        // 游戏场景中的帮助按钮
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_help_btn_hide),
                getString(R.string.custom_config_game_help_btn_hide_subtitle),
                1, 0, viewModel.configModel.ui.game_help_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.game_help_btn.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        // 游戏场景中的帮助按钮
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_settle_again_btn_hide),
                getString(R.string.custom_config_game_settle_again_btn_hide_subtitle),
                1, 0, viewModel.configModel.ui.game_settle_again_btn.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.game_settle_again_btn.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        // 是否隐藏背景图，包括大厅和战斗
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_bg_hide),
                getString(R.string.custom_config_game_bg_hide_subtitle),
                1, 0, viewModel.configModel.ui.game_bg.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.game_bg.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        // 大厅中的玩法选择设置面板
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_setting_select_pnl),
                getString(R.string.custom_config_game_setting_select_pnl_subtitle),
                1, 0, viewModel.configModel.ui.game_setting_select_pnl.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.game_setting_select_pnl.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        // 游戏中的托管图标
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_managed_image),
                getString(R.string.custom_config_game_managed_image_subtitle),
                1, 0, viewModel.configModel.ui.game_managed_image.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.game_managed_image.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        // 游戏中牌桌背景图 （注：只对某些带牌桌类游戏有作用）
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_table_image),
                getString(R.string.custom_config_game_table_image_subtitle),
                1, 0, viewModel.configModel.ui.game_table_image.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.game_table_image.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        // 游戏中游戏倒计时显示 （注：现在只针对umo生效）
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_countdown_time),
                getString(R.string.custom_config_game_countdown_time_subtitle),
                1, 0, viewModel.configModel.ui.game_countdown_time.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.game_countdown_time.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        // 游戏中所选择的玩法提示文字 （注：现在只针对ludo生效）
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_selected_tips),
                getString(R.string.custom_config_game_selected_tips_subtitle),
                1, 0, viewModel.configModel.ui.game_selected_tips.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.game_selected_tips.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        // 控制NFT头像的开关
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_nft_avatar),
                getString(R.string.custom_config_nft_avatar_subtitle),
                1, 0, viewModel.configModel.ui.nft_avatar.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.nft_avatar.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        // 控制开场动画的开关
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_opening),
                getString(R.string.custom_config_game_opening_subtitle),
                1, 0, viewModel.configModel.ui.game_opening.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.game_opening.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        // 游戏结算前的mvp动画
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_mvp),
                getString(R.string.custom_config_game_mvp_subtitle),
                1, 0, viewModel.configModel.ui.game_mvp.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.game_mvp.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        // 游戏中动画和头像右上角的UMO图标
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_umo_icon),
                getString(R.string.custom_config_umo_icon_subtitle),
                1, 0, viewModel.configModel.ui.umo_icon.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.umo_icon.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        // 游戏中动画和头像右上角的UMO图标
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_logo),
                getString(R.string.custom_config_logo_subtitle),
                1, 0, viewModel.configModel.ui.logo.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.logo.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

        // 游戏中的游戏位
        item1View = new CustomPageItem1View(this);
        configItemModel = addItem(
                getString(R.string.custom_config_game_players),
                getString(R.string.custom_config_game_players_subtitle),
                1, 0, viewModel.configModel.ui.game_players.hide, false,
                getString(R.string.custom_config_game_hide_false),
                getString(R.string.custom_config_game_hide_true));
        item1View.setListener(itemModel -> viewModel.configModel.ui.game_players.hide = itemModel.hide);
        item1View.setData(configItemModel);
        configContainer.addView(item1View);

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
