/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package tech.sud.mgp.SudMGPWrapper.model;

import java.io.Serializable;

/**
 * 游戏配置模型
 * 参考文档：https://docs.sud.tech/zh-CN/app/Client/API/ISudFSMMG/onGetGameCfg.html
 */
public class GameConfigModel implements Serializable {

    public int gameMode = 1; // 游戏模式（每个游戏默认模式是1，不填是1）
    public int gameCPU = 0; // 游戏CPU（值为0和1；0：CPU正常功耗，1：CPU低功耗；默认是0，CPU正常功耗）
    public int gameSoundControl = 0; // 游戏中声音的播放是否被app层接管（值为0和1；0：游戏播放声音，1：app层播放声音，游戏中不播放任何声音；默认是0）
    public int gameSoundVolume = 100; // 游戏中音量的大小（值为0到100；默认是100）
    public GameUi ui = new GameUi(); // 对游戏ui界面的配置，可定制ui界面的显示与不显示

    // 游戏配置中，ui部分
    public static class GameUi implements Serializable {
        public GameSettle gameSettle = new GameSettle(); // 结算界面
        public GamePing ping = new GamePing(); // 界面中的ping值
        public GameVersion version = new GameVersion(); // 界面中的版本信息值
        public GameLevel level = new GameLevel(); // 大厅中的段位信息
        public GameLobbySettingBtn lobby_setting_btn = new GameLobbySettingBtn(); // 大厅的设置按钮
        public GameLobbyHelpBtn lobby_help_btn = new GameLobbyHelpBtn(); // 大厅的帮助按钮
        public GameLobbyPlayers lobby_players = new GameLobbyPlayers(); // 大厅玩家展示位
        public GameLobbyPlayerCaptainIcon lobby_player_captain_icon = new GameLobbyPlayerCaptainIcon(); // 大厅玩家展示位上队长标识
        public GameLobbyPlayerKickoutIcon lobby_player_kickout_icon = new GameLobbyPlayerKickoutIcon(); // 大厅玩家展示位上踢人标识
        public GameLobbyRule lobby_rule = new GameLobbyRule(); // 大厅的玩法规则描述文字
        public GameLobbyGameSetting lobby_game_setting = new GameLobbyGameSetting(); // 玩法设置
        public GameJoinBtn join_btn = new GameJoinBtn(); // 加入按钮
        public GameCancelJoinBtn cancel_join_btn = new GameCancelJoinBtn(); // 取消加入按钮
        public GameReadyBtn ready_btn = new GameReadyBtn(); // 准备按钮
        public GameCancelReadyBtn cancel_ready_btn = new GameCancelReadyBtn(); // 取消准备按钮
        public GameStartBtn start_btn = new GameStartBtn(); // 开始按钮
        public GameShareBtn share_btn = new GameShareBtn(); // 分享
        public GameSttingBtn game_setting_btn = new GameSttingBtn(); // 游戏场景中的设置按钮
        public GameHelpBtn game_help_btn = new GameHelpBtn(); // 游戏场景中的帮助按钮
        public GameSettleCloseBtn game_settle_close_btn = new GameSettleCloseBtn(); // 游戏结算界面中的关闭按钮
        public GameSettleAgainBtn game_settle_again_btn = new GameSettleAgainBtn(); // 游戏结算界面中的再来一局按钮
        public GameBg game_bg = new GameBg();// 是否隐藏背景图，包括大厅和战斗
        public BlockChangeSeat block_change_seat = new BlockChangeSeat(); // 自定义阻止换座位
    }

    // 结算界面
    public static class GameSettle implements Serializable {
        public boolean hide = false; // 是否隐藏结算界面（false: 显示； true: 隐藏，默认为 false）
    }

    // 界面中的ping值
    public static class GamePing implements Serializable {
        public boolean hide = false; // 是否隐藏ping值（false: 显示；true: 隐藏，默认为false）
    }

    // 界面中的版本信息值
    public static class GameVersion implements Serializable {
        public boolean hide = false; // 是否隐藏版本信息（false: 显示； true: 隐藏，默认为false）
    }

    // 大厅中的段位信息
    public static class GameLevel implements Serializable {
        public boolean hide = false; // 是否隐藏段位信息（false: 显示； true: 隐藏，默认为false）
    }

    // 大厅的设置按钮
    public static class GameLobbySettingBtn implements Serializable {
        public boolean hide = false; // 是否隐藏大厅的设置按钮（false: 显示； true: 隐藏，默认为false）
    }

    // 大厅的帮助按钮
    public static class GameLobbyHelpBtn implements Serializable {
        public boolean hide = false; // 是否隐藏大厅的帮助按钮（false: 显示； true: 隐藏，默认为false）
    }

    // 大厅玩家展示位
    public static class GameLobbyPlayers implements Serializable {
        public boolean custom = false; // 大厅玩家展示位头像点击加入（false: 游戏处理逻辑； true: 游戏只通知按钮点击事件，不处理；默认为false）
        public boolean hide = false; // 是否隐藏大厅玩家展示位（false: 显示； true: 隐藏，默认为false）
    }

    // 大厅玩家展示位上队长标识
    public static class GameLobbyPlayerCaptainIcon implements Serializable {
        public boolean hide = false; // 是否隐藏大厅玩家展示位上队长标识（false: 显示； true: 隐藏，默认为false）
    }

    // 大厅玩家展示位上踢人标识
    public static class GameLobbyPlayerKickoutIcon implements Serializable {
        public boolean hide = false; // 是否隐藏大厅玩家展示位上踢人标识（false: 显示； true: 隐藏，默认为false）
    }

    // 大厅的玩法规则描述文字
    public static class GameLobbyRule implements Serializable {
        public boolean hide = false; // 是否隐藏大厅的玩法规则描述文字（false: 显示； true: 隐藏，默认为false）
    }

    // 玩法设置
    public static class GameLobbyGameSetting implements Serializable {
        public boolean hide = false; // 是否隐藏玩法设置（false: 显示； true: 隐藏，默认为false）
    }

    // 加入按钮
    public static class GameJoinBtn implements Serializable {
        public boolean custom = false; // 加入按钮（false: 游戏处理逻辑； true: 游戏只通知按钮点击事件，不处理；默认为false）
        public boolean hide = false; // 是否隐藏加入按钮（false: 显示； true: 隐藏，默认为false）
    }

    // 取消加入按钮
    public static class GameCancelJoinBtn implements Serializable {
        public boolean custom = false; // 取消加入按钮（false: 游戏处理逻辑； true: 游戏只通知按钮点击事件，不处理；默认为false）
        public boolean hide = false; // 是否隐藏取消加入按钮（false: 显示； true: 隐藏，默认为false）
    }

    // 准备按钮
    public static class GameReadyBtn implements Serializable {
        public boolean custom = false; // 准备按钮（false: 游戏处理逻辑； true: 游戏只通知按钮点击事件，不处理；默认为false）
        public boolean hide = false; // 是否隐藏准备按钮（false: 显示； true: 隐藏，默认为false）
    }

    // 取消准备按钮
    public static class GameCancelReadyBtn implements Serializable {
        public boolean custom = false; // 取消准备按钮（false: 游戏处理逻辑； true: 游戏只通知按钮点击事件，不处理；默认为false）
        public boolean hide = false; // 是否隐藏取消准备按钮（false: 显示； true: 隐藏，默认为false）
    }

    // 开始游戏按钮
    public static class GameStartBtn implements Serializable {
        public boolean custom = false; // 开始游戏按钮（false: 游戏处理逻辑； true: 游戏只通知按钮点击事件，不处理；默认为false）
        public boolean hide = false; // 是否隐藏开始游戏按钮（false: 显示； true: 隐藏，默认为false）
    }

    // 分享按钮
    public static class GameShareBtn implements Serializable {
        public boolean custom = false; // 分享按钮（false: 游戏处理逻辑； true: 游戏只通知按钮点击事件，不处理；默认为false）
        public boolean hide = true; // 是否隐藏分享按钮（false: 显示； true: 隐藏，默认为true）
    }

    // 游戏场景中的设置按钮
    public static class GameSttingBtn implements Serializable {
        public boolean hide = false; // 是否隐藏游戏场景中的设置按钮（false: 显示； true: 隐藏，默认为false）
    }

    // 游戏场景中的帮助按钮
    public static class GameHelpBtn implements Serializable {
        public boolean hide = false; // 是否隐藏游戏场景中的帮助按钮（false: 显示； true: 隐藏，默认为false）
    }

    // 游戏结算界面中的关闭按钮
    public static class GameSettleCloseBtn implements Serializable {
        public boolean custom = false; // 游戏结算界面中的关闭按钮（false: 关闭结算界面返回大厅； true: 游戏通知按钮点击事件，并关闭结算界面返回大厅；默认为false）
    }

    // 游戏结算界面中的再来一局按钮
    public static class GameSettleAgainBtn implements Serializable {
        // 游戏结算界面中的再来一局按钮
        // （false: 关闭结算界面返回大厅并将玩家设置为准备状态； true: 游戏通知按钮点击事件，并关闭结算界面返回大厅（不将玩家设置为准备状态）；默认为false）
        public boolean custom = false;
    }

    // 是否隐藏背景图，包括大厅和战斗
    // ！！！这里只隐藏加载完成后的背景图，加载中背景图如需隐藏则调用：{SudMGP.getCfg().setShowLoadingGameBg(false); }
    public static class GameBg implements Serializable {
        //（false: 显示； true: 隐藏，默认为false）
        public boolean hide = false;
    }

    // 自定义阻止换座位
    public static class BlockChangeSeat implements Serializable {
        //（false: 可以换座位； true: 不可以换座位；默认为false）
        public boolean custom = false;
    }

}