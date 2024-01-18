package io.yao.harp.order.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import io.yao.harp.enums.PaymentTypeEnum;
import io.yao.harp.enums.RefundStatusEnum;
import io.yao.harp.model.order.OrderInfo;
import io.yao.harp.model.order.PaymentInfo;
import io.yao.harp.model.order.RefundInfo;
import io.yao.harp.order.service.OrderService;
import io.yao.harp.order.service.PaymentService;
import io.yao.harp.order.service.RefundInfoService;
import io.yao.harp.order.service.XXPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class XXPayServiceImpl implements XXPayService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RefundInfoService refundInfoService;

    @Override
    public Map createNative(Long orderId) {
        // first get data from redis
        Map payMap = (Map) redisTemplate.opsForValue().get(orderId.toString());
        if(payMap != null) {
            return payMap;
        }

        OrderInfo order = orderService.getById(orderId);
        paymentService.savePaymentInfo(order, PaymentTypeEnum.XXPAY.getStatus());


        // request xxpay api
        // ...

        String resultCode = orderId + order.getPatientId().toString();
        String codeUrl = "https://dyingdown.github.io/img/alipay.jpg";
        // pack result
        //4、封装返回结果集
        Map map = new HashMap<>();
        map.put("orderId", orderId);
        map.put("totalFee", order.getAmount());
        map.put("resultCode", resultCode);
        map.put("codeUrl", codeUrl); // qrcode url

        if(!StringUtils.isEmpty(resultCode)) {
            redisTemplate.opsForValue().set(orderId.toString(), map, 120,TimeUnit.MINUTES);
        }
        return map;
    }

    @Override
    public Map<String, String> queryPayStatus(Long orderId) {
        try {
            OrderInfo orderInfo = orderService.getById(orderId);

            // request xxpay api to get result
            // ...

            Map<String, String > resultMap = new HashMap<>();
            // pack result

            //FIXME: values are fake
            resultMap.put("out_trade_no", orderInfo.getOutTradeNo());
            resultMap.put("result_code", "SUCCESS");
            resultMap.put("return_msg", "OK");
            resultMap.put("trade_status_desc", "订单已支付");
            resultMap.put("bank_type", "OTHERS");
            resultMap.put("trade_state", "SUCCESS");
            resultMap.put("transaction_id", "40000215456848431");
            // ...

            return resultMap;
        } catch (Exception e) {
            return null;
        }
    }

    // xxpay refund
    @Override
    public Boolean refund(Long orderId) {
        try {
            PaymentInfo paymentInfo = paymentService.getPaymentInfo(orderId, PaymentTypeEnum.XXPAY.getStatus());
            RefundInfo refundInfo = refundInfoService.saveRefundInfo(paymentInfo);
            if(refundInfo.getRefundStatus() == RefundStatusEnum.REFUND.getStatus().intValue()) {
                return true;
            }

            // request the xxpay refund api
            // ...

            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("result_code", "SUCCESS");
            resultMap.put("refund_id", refundInfo.getId().toString());

            // pack result
            if (null != resultMap && resultMap.get("result_code").equals("SUCCESS")) {
                refundInfo.setCallbackTime(new Date());
                refundInfo.setTradeNo(resultMap.get("refund_id"));
                refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
                refundInfo.setCallbackContent(JSONObject.toJSONString(resultMap));
                refundInfoService.updateById(refundInfo);
                return true;
            }
            return false;



        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
