package io.yao.harp.hosp.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.yao.harp.common.result.Result;
import io.yao.harp.hosp.service.DepartmentService;
import io.yao.harp.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hosp/department")

public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @Operation(summary = "Get all department of a certain hospital by hoscode")
    @GetMapping("getAllDept/{hoscode}")
    public Result getAllDept(@PathVariable String hoscode) {
        List<DepartmentVo> departmentList = departmentService.getDeptTree(hoscode);
        return Result.ok(departmentList);
    }
}
