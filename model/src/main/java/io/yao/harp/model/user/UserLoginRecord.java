package io.yao.harp.model.user;

import io.yao.harp.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * UserLoginRecord
 * </p>
 *
 * @author qy
 */
@Data
@Schema(description = "用户登录日志")
@TableName("user_login_record")
public class UserLoginRecord extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	@Schema(description = "用户id")
	@TableField("user_id")
	private Long userId;

	@Schema(description = "ip")
	@TableField("ip")
	private String ip;

}

