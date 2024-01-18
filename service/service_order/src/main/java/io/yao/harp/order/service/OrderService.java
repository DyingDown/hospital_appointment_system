package io.yao.harp.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.yao.harp.model.order.OrderInfo;
import io.yao.harp.vo.order.OrderQueryVo;

public interface OrderService extends IService<OrderInfo> {
    //保存订单
    Long saveOrder(String scheduleId, Long patientId);

    // get order by order id
    OrderInfo getOrder(String orderId);

    // select order list and page it
    IPage<OrderInfo> selectPage(Page<OrderInfo> pageParams, OrderQueryVo orderQueryVo);


    // cancel order by order id
    Boolean cancelOrder(Long orderId);


    // send message to remind patient's of today's appointment
    void patientTips();
}

