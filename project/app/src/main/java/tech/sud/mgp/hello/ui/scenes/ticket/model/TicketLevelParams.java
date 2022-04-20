package tech.sud.mgp.hello.ui.scenes.ticket.model;

import java.io.Serializable;

/**
 * 进入场景等级页面的参数
 */
public class TicketLevelParams implements Serializable {
    public int sceneType; // 场景类型
    public long gameId; // 游戏id
    public String gameName; // 游戏名称
}
