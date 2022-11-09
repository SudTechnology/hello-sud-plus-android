package tech.sud.mgp.hello.ui.main.home.helper;

import com.blankj.utilcode.util.ActivityUtils;
import com.jeremyliao.liveeventbus.LiveEventBus;

import tech.sud.mgp.hello.common.event.LiveEventBusKey;
import tech.sud.mgp.hello.common.event.model.JumpRocketEvent;
import tech.sud.mgp.hello.service.main.resp.GetBannerResp;
import tech.sud.mgp.hello.ui.main.base.utils.RouterUtils;

/**
 * 跳转助手
 */
public class JumpHelper {

    public static final int JUMP_TYPE_ROCKET = 1; // 创建语聊房，打开火箭
    public static final int JUMP_TYPE_H5 = 2; // 打开H5页面

    /**
     * 跳转
     *
     * @param model 跳转参数
     * @return 返回true表示消费了这次跳转
     */
    public static boolean jump(GetBannerResp.BannerModel model) {
        if (model == null) {
            return false;
        }
        switch (model.type) {
            case JUMP_TYPE_ROCKET: // 创建语聊房，打开火箭
                LiveEventBus.get(LiveEventBusKey.KEY_JUMP_ROCKET).post(new JumpRocketEvent());
                break;
            case JUMP_TYPE_H5: // 打开H5页面
                RouterUtils.openUrl(ActivityUtils.getTopActivity(), "", model.jumpUrl);
                break;
        }
        return false;
    }

}
