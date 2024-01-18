package io.yao.harp.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.yao.harp.model.order.PaymentInfo;
import io.yao.harp.model.order.RefundInfo;

public interface RefundInfoService extends IService<RefundInfo> {

    // save the refund info
    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);
}
