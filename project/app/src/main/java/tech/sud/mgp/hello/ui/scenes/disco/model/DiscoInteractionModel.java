package tech.sud.mgp.hello.ui.scenes.disco.model;

import java.io.Serializable;

/**
 * 描述蹦迪交互的模型
 */
public class DiscoInteractionModel implements Serializable {
    public String name;
    public DiscoInteractionType type = DiscoInteractionType.UNDEFINED;
    public Integer price; // 价格，为空时就是没有价格，0为免费
}
