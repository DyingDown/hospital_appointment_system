package io.yao.harp.vo.hosp;

import io.yao.harp.model.hosp.Department;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Department")
public class DepartmentVo {

	@Schema(description = "科室编号")
	private String depcode;

	@Schema(description = "科室名称")
	private String depname;

	@Schema(description = "下级节点")
	private List<DepartmentVo> children;

}

