package com.offcn.page.service;

/**
 * Created by travelround on 2019/2/25.
 */
public interface ItemPageService {

    /**
     * 生成商品详细页
     * @param goodsId
     */
    public boolean genItemHtml(Long goodsId);

    /**
     * 删除商品详细页
     * @param goodsId
     * @return
     */
    public boolean deleteItemHtml(Long[] goodsIds);
}
