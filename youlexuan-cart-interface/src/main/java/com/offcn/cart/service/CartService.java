package com.offcn.cart.service;

import com.offcn.group.Cart;

import java.util.List;

/**
 * Created by travelround on 2019/7/10.
 */
public interface CartService {

    List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);

    List<Cart> findCartListFromRedis(String username);

    void saveCartListToRedis(String username, List<Cart> cartList);

    List<Cart> mergeCartList(List<Cart> cartList_redis, List<Cart> cartList_cookie);
}
