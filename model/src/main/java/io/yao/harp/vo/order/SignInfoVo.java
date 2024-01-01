package io.yao.harp.vo.order;

import io.yao.harp.model.base.BaseMongoEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * <p>
 * HospitalSet
 * </p>
 *
 * @author qy
 */
@Data
@Schema(description = "签名信息")
public class SignInfoVo  implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Schema(description = "api基础路径")
	private String apiUrl;

	@Schema(description = "签名秘钥")
	private String signKey;

}

