package tech.sud.mgp.hello.common.event.model;

import java.io.Serializable;

public class JumpRocketEvent implements Serializable {
    public boolean isConsume; // 粘性事件，消费完成之后，将值设为true，后续不再处理
}
