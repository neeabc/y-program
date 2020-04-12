package com.offcn.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offcn.cart.service.CartService;
import com.offcn.entity.Result;
import com.offcn.group.Cart;
import com.offcn.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by travelround on 2019/7/10.
 */

// 思路:
// 1.从cookie中取购物车
// 2.向购物车添加商品
// 3.将购物车存入cookie

@RestController
@RequestMapping("/cart")
public class CartController {

    // 为避免远程服务器调用超时,可以改超时时间为6秒
    @Reference(timeout = 6000)
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    // 购物车列表
    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        // 得到登陆人账号,判断是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录用户:" + username);


        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartListString == null || cartListString.equals("")) {
            cartListString="[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);

        // return cartList_cookie;
        // 判断是否登陆
        if (username.equals("anonymousUser")) {
            // 未登陆
            return cartList_cookie;
        } else {
            // 已登陆
            // 到redis中取购物车

            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);

            if (cartList_cookie.size() > 0) {
                // 若cookie中有购物车, 合并
                cartList_redis = cartService.mergeCartList(cartList_redis, cartList_cookie);
                // 合并后清除cookie中的购物车
                CookieUtil.deleteCookie(request, response, "cartList");
                // 将合并后的购物车更新回redis
                cartService.saveCartListToRedis(username, cartList_redis);
            }


            return cartList_redis;
        }

    }

    // 添加购物车
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9105", allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId, Integer num) {

        // 添加跨越请求
        // 跨域:在不同域之间进行数据传输或通讯,出于安全考虑,ajax,js等默认不允许访问. 只要协议,域名,端口有任何一个不同,就被当做跨域
//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
//        response.setHeader("Access-Control-Allow-Credentials", "true");

        // 得到登陆人账号,判断是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录用户:" + username);

        try {
            List<Cart> cartList = findCartList();// 获取购物车列表
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            // CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600*24,"UTF-8");
            if (username.equals("anonymousUser")) {
                // 未登陆
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600*24,"UTF-8");
                System.out.println("向cookie存入数据");
            } else {
                // 已登陆
                // 保存到redis
                cartService.saveCartListToRedis(username, cartList);
            }
            return  new Result(true, "添加成功");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return  new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false, "添加失败");
        }
    }


}
