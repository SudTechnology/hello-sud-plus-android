package tech.sud.mgp.hello.service.main.resp;

import java.io.Serializable;
import java.util.List;

/**
 * 获取首页banner信息 返回参数
 */
public class GetBannerResp implements Serializable {
    public List<BannerModel> bannerInfoList;

    public static class BannerModel implements Serializable {
        public String configId; // 配置id
        public int type; // 跳转类型（1.房间，2.H5）
        public String image; // 图片
        public String jumpUrl; // 跳转路径
    }

}
