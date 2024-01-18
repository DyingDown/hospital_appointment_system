package io.yao.harp.order.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.yao.harp.common.helper.HttpRequestHelper;
import io.yao.harp.common.result.Result;
import io.yao.harp.common.utils.AuthContextHolder;
import io.yao.harp.enums.OrderStatusEnum;
import io.yao.harp.model.order.OrderInfo;
import io.yao.harp.order.service.OrderService;
import io.yao.harp.vo.order.OrderQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Tag(name="订单接口")
@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderApiController {

    @Autowired
    private OrderService orderService;

    @Operation(description = "创建订单")
    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    public Result submitOrder(
            @Parameter(name = "scheduleId", description = "排班id", required = true)
            @PathVariable String scheduleId,
            @Parameter(name = "patientId", description = "就诊人id", required = true)
            @PathVariable Long patientId) {
        return Result.ok(orderService.saveOrder(scheduleId, patientId));
    }

    @Operation(description = "Get Order by order Id")
    @GetMapping("auth/getOrders/{orderId}")
    public Result getOrders(@PathVariable String orderId) {
        OrderInfo orderInfo = orderService.getOrder(orderId);
        return Result.ok(orderInfo);
    }

    @Operation(summary = "Get Order list page")
    @GetMapping("auth/{page}/{limit}")
    public Result getListPage(@PathVariable Long page,
                              @PathVariable Long limit,
                              OrderQueryVo orderQueryVo, HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        orderQueryVo.setUserId(userId);
        Page<OrderInfo> pageParams = new Page<>(page, limit);
        IPage<OrderInfo> pageModel = orderService.selectPage(pageParams, orderQueryVo);
        return Result.ok(pageModel);
    }

    @Operation(summary = "获取订单状态")
    @GetMapping("auth/getStatusList")
    public Result getStatusList() {
        return Result.ok(OrderStatusEnum.getStatusList());
    }

    @Operation(summary = "cancel order")
    @GetMapping("auth/cancelOrder/{orderId}")
    public Result cancelOrder(@PathVariable Long orderId) {
        Boolean isCanceled = orderService.cancelOrder(orderId);
        return Result.ok(isCanceled);
    }

}
