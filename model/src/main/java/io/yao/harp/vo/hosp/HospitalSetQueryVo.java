package io.yao.harp.vo.hosp;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class HospitalSetQueryVo {

    @Schema(description = "医院名称")
    private String hosname;

    @Schema(description = "医院编号")
    private String hoscode;
}
