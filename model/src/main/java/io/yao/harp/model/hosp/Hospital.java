package io.yao.harp.model.hosp;

import com.alibaba.fastjson.JSONObject;
import io.yao.harp.model.base.BaseEntity;
import io.yao.harp.model.base.BaseMongoEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * <p>
 * Hospital
 * </p>
 *
 * @author qy
 */
@Data
@Schema(description = "Hospital")
@Document("Hospital")
public class Hospital extends BaseMongoEntity {
	
	private static final long serialVersionUID = 1L;
	
	@Schema(description = "医院编号")
	@Indexed(unique = true) //唯一索引
	private String hoscode;

	@Schema(description = "医院名称")
	@Indexed //普通索引
	private String hosname;

	@Schema(description = "医院类型")
	private String hostype;

	@Schema(description = "省code")
	private String provinceCode;

	@Schema(description = "市code")
	private String cityCode;

	@Schema(description = "区code")
	private String districtCode;

	@Schema(description = "详情地址")
	private String address;

	@Schema(description = "医院logo")
	private String logoData;

	@Schema(description = "医院简介")
	private String intro;

	@Schema(description = "坐车路线")
	private String route;

	@Schema(description = "状态 0：未上线 1：已上线")
	private Integer status;

	//预约规则
	@Schema(description = "预约规则")
	private BookingRule bookingRule;

	public void setBookingRule(String bookingRule) {
		this.bookingRule = JSONObject.parseObject(bookingRule, BookingRule.class);
	}

}

