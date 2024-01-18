package io.yao.harp.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.yao.harp.model.order.OrderInfo;
import io.yao.harp.model.order.PaymentInfo;

import java.util.Map;

public interface PaymentService extends IService<PaymentInfo> {

    // add payment info to table
    void savePaymentInfo(OrderInfo order, Integer status);

    void paySuccess(String outTradeNo, Integer status, Map<String, String> resultMap);

    PaymentInfo getPaymentInfo(Long orderId, Integer paymentType);
}
