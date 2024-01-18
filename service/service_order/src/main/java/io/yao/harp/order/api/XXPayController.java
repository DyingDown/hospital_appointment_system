package io.yao.harp.order.api;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.yao.harp.common.result.Result;
import io.yao.harp.enums.PaymentTypeEnum;
import io.yao.harp.order.service.PaymentService;
import io.yao.harp.order.service.XXPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/order/xxpay")
public class XXPayController {

    @Autowired
    private XXPayService xxPayService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("createNative/{orderId}")
    public Result createNative(@PathVariable Long orderId) {
        Map map = xxPayService.createNative(orderId);
        return Result.ok(map);
    }

    @Operation(description = "查询支付状态")
    @GetMapping("/queryPayStatus/{orderId}")
    public Result queryPayStatus(
            @Parameter(name = "orderId", description = "订单id", required = true)
            @PathVariable("orderId") Long orderId) {
        Map<String, String> resultMap = xxPayService.queryPayStatus(orderId);
        if (resultMap == null) {//出错
            return Result.fail().message("支付出错");
        }
        if ("SUCCESS".equals(resultMap.get("trade_state"))) {//如果成功
            //更改订单状态，处理支付结果
            String out_trade_no = resultMap.get("out_trade_no");
            paymentService.paySuccess(out_trade_no, PaymentTypeEnum.XXPAY.getStatus(), resultMap);
            return Result.ok().message("支付成功");
        }
        return Result.ok().message("支付中");

    }


}
