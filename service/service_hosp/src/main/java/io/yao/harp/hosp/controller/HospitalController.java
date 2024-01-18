package io.yao.harp.hosp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.yao.harp.common.result.Result;
import io.yao.harp.hosp.service.HospitalService;
import io.yao.harp.model.hosp.Hospital;
import io.yao.harp.vo.hosp.HospitalQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/hospital")

public class HospitalController {
    @Autowired
    private HospitalService hospitalService;

    @Operation(summary = "Get hospital List by some condition")
    @GetMapping("list/{page}/{limit}")
    public Result getHospitalList(@PathVariable Integer page,
                                  @PathVariable Integer limit,
                                  HospitalQueryVo hospitalQueryVo) {
        Page<Hospital> pageModel = hospitalService.selectHospitalPage(page, limit, hospitalQueryVo);
        return Result.ok(pageModel);
    }

    @Operation(summary = "Update Hospital status")
    @GetMapping("updateHospStatus/{id}/{status}")
    public Result updateHospStatus(@PathVariable String id, @PathVariable Integer status) {
        hospitalService.updateStatus(id, status);
        return Result.ok();
    }

    @Operation(summary = "hosptial detailed information")
    @GetMapping("detailedHospInfo/{id}")
    public Result getDetailedHospInfo(@PathVariable String id) {
        Map<String, Object> hospitalMap = hospitalService.getHospialById(id);
        return Result.ok(hospitalMap);
    }
}
