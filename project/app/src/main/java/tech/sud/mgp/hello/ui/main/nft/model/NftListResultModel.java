package tech.sud.mgp.hello.ui.main.nft.model;

import java.util.List;

/**
 * nft列表返回
 */
public class NftListResultModel {

    public int totalCount; // 总数
    public String pageKey; // 分页键，第一次不设置，当返回值存在page_key时，使用该值请求下一页数据
    public List<NftModel> list; // nft列表

}