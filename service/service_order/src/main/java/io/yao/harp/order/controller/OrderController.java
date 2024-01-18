package io.yao.harp.order.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.yao.harp.common.result.Result;
import io.yao.harp.enums.OrderStatusEnum;
import io.yao.harp.model.order.OrderInfo;
import io.yao.harp.order.service.OrderService;
import io.yao.harp.vo.order.OrderQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "orders", description = "orders")
@RestController
@RequestMapping("/admin/order/orderInfo")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Operation(summary = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(
            @Parameter(name = "page", description = "当前页码", required = true)
            @PathVariable Long page,
            @Parameter(name = "limit", description = "每页记录数", required = true)
            @PathVariable Long limit,
            @Parameter(name = "orderCountQueryVo", description = "查询对象", required = false) OrderQueryVo orderQueryVo) {
        System.out.println(orderQueryVo);
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        IPage<OrderInfo> pageModel = orderService.selectPage(pageParam, orderQueryVo);
        return Result.ok(pageModel);
    }

    @Operation(summary = "获取订单状态")
    @GetMapping("getStatusList")
    public Result getStatusList() {
        return Result.ok(OrderStatusEnum.getStatusList());
    }

}
