package io.yao.harp.vo.order;

import io.yao.harp.vo.msm.MsmVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "OrderMqVo")
public class OrderMqVo {

	@Schema(description = "可预约数")
	private Integer reservedNumber;

	@Schema(description = "剩余预约数")
	private Integer availableNumber;

	@Schema(description = "排班id")
	private String scheduleId;

	@Schema(description = "短信实体")
	private MsmVo msmVo;

}

