package io.yao.harp.order.service;

import java.util.Map;

public interface XXPayService {

    // get xxpay qrcode
    Map createNative(Long orderId);

    Map<String, String> queryPayStatus(Long orderId);

    Boolean refund(Long orderId);
}
