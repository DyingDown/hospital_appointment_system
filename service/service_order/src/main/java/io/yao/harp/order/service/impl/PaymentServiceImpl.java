package io.yao.harp.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.yao.harp.common.exception.HarpException;
import io.yao.harp.common.helper.HttpRequestHelper;
import io.yao.harp.common.result.ResultCodeEnum;
import io.yao.harp.common.utils.MD5;
import io.yao.harp.enums.OrderStatusEnum;
import io.yao.harp.enums.PaymentStatusEnum;
import io.yao.harp.enums.PaymentTypeEnum;
import io.yao.harp.hospital.client.HospitalFeignClient;
import io.yao.harp.model.order.OrderInfo;
import io.yao.harp.model.order.PaymentInfo;
import io.yao.harp.order.mapper.PaymentInfoMapper;
import io.yao.harp.order.service.OrderService;
import io.yao.harp.order.service.PaymentService;
import io.yao.harp.vo.order.SignInfoVo;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl extends
        ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HospitalFeignClient hospitalFeignClient;

    @Override
    public void savePaymentInfo(OrderInfo order, Integer paymentType) {
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", order.getId());
        wrapper.eq("payment_type", paymentType);
        Integer count = baseMapper.selectCount(wrapper);
        if(count > 0) {
            return ;
        }
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(order.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setOutTradeNo(order.getOutTradeNo());
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
        String subject = new DateTime(order.getReserveDate()).toString("yyyy-MM-dd")+"|"+order.getHosname()+"|"+order.getDepname()+"|"+order.getTitle();
        paymentInfo.setSubject(subject);
        paymentInfo.setTotalAmount(order.getAmount());
        baseMapper.insert(paymentInfo);
    }


    // update order status
    @Override
    public void paySuccess(String outTradeNo, Integer status, Map<String, String> resultMap) {
        // get the payment info
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("out_trade_no", outTradeNo);
        wrapper.eq("payment_type", PaymentTypeEnum.XXPAY.getStatus());
        PaymentInfo paymentInfo = baseMapper.selectOne(wrapper);
        if(paymentInfo == null) {
            throw new HarpException(ResultCodeEnum.PARAM_ERROR);
        }
        if(paymentInfo.getPaymentStatus() != PaymentStatusEnum.UNPAID.getStatus()) {
            return ;
        }
        // update payment info
        paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
        paymentInfo.setTradeNo(resultMap.get("transaction_id"));
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(resultMap.toString());
        baseMapper.updateById(paymentInfo);

        // update order status
        OrderInfo orderInfo = orderService.getById(paymentInfo.getOrderId());
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderService.updateById(orderInfo);

        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(orderInfo.getHoscode());
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("hoscode",orderInfo.getHoscode());
        reqMap.put("hosRecordId",orderInfo.getHosRecordId());
        reqMap.put("timestamp", HttpRequestHelper.getTimestamp());
//        String sign = HttpRequestHelper.getSign(reqMap, signInfoVo.getSignKey());
        String sign = MD5.encrypt(signInfoVo.getSignKey());
        reqMap.put("sign", sign);

        JSONObject result = HttpRequestHelper.sendRequest(reqMap, signInfoVo.getApiUrl() + "/order/updatePayStatus");
        if(result.getInteger("code") != 200) {
            throw new HarpException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        }

    }

    @Override
    public PaymentInfo getPaymentInfo(Long orderId, Integer paymentType) {
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        wrapper.eq("payment_type", paymentType);
        PaymentInfo paymentInfo = baseMapper.selectOne(wrapper);
        if(null == paymentInfo) {
            throw new HarpException(ResultCodeEnum.PARAM_ERROR);
        }

        return paymentInfo;
    }
}
