package io.yao.harp.vo.acl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 用户查询实体
 * </p>
 *
 * @author qy
 * @since 2019-11-08
 */
@Data
@Schema(description = "用户查询实体")
public class UserQueryVo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Schema(description = "用户名")
	private String username;

	@Schema(description = "昵称")
	private String nickName;

}

