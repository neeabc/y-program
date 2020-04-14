package com.offcn.pay.service;

import java.util.Map;

/**
 * Created by travelround on 2019/7/12.
 */
public interface AliPayService {
    Map createNative(String out_trade_no, String total_fee);

    Map<String,String> queryPayStatus(String out_trade_no);

    Map closePay(String out_trade_no);
}
