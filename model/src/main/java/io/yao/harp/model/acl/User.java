package io.yao.harp.model.acl;

import io.yao.harp.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 用户
 * </p>
 *
 * @author qy
 * @since 2019-11-08
 */
@Data
@Schema(description = "用户")
@TableName("acl_user")
public class User extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Schema(description = "用户名")
	@TableField("username")
	private String username;

	@Schema(description = "密码")
	@TableField("password")
	private String password;

	@Schema(description = "昵称")
	@TableField("nick_name")
	private String nickName;

	@Schema(description = "用户头像")
	@TableField("salt")
	private String salt;

	@Schema(description = "用户签名")
	@TableField("token")
	private String token;

}



