package io.yao.harp.model.order;

import io.yao.harp.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * RefundInfo
 * </p>
 *
 * @author qy
 */
@Data
@Schema(description = "RefundInfo")
@TableName("refund_info")
public class RefundInfo extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	@Schema(description = "对外业务编号")
	@TableField("out_trade_no")
	private String outTradeNo;

	@Schema(description = "订单编号")
	@TableField("order_id")
	private Long orderId;

	@Schema(description = "支付类型（微信 支付宝）")
	@TableField("payment_type")
	private Integer paymentType;

	@Schema(description = "交易编号")
	@TableField("trade_no")
	private String tradeNo;

	@Schema(description = "退款金额")
	@TableField("total_amount")
	private BigDecimal totalAmount;

	@Schema(description = "交易内容")
	@TableField("subject")
	private String subject;

	@Schema(description = "退款状态")
	@TableField("refund_status")
	private Integer refundStatus;

	@Schema(description = "回调时间")
	@TableField("callback_time")
	private Date callbackTime;

	@Schema(description = "回调信息")
	@TableField("callback_content")
	private String callbackContent;

}

