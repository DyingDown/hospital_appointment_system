package io.yao.harp.hosp.controller.api;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.yao.harp.common.result.Result;
import io.yao.harp.hosp.service.DepartmentService;
import io.yao.harp.hosp.service.HospitalService;
import io.yao.harp.hosp.service.HospitalSetService;
import io.yao.harp.hosp.service.ScheduleService;
import io.yao.harp.model.hosp.Hospital;
import io.yao.harp.vo.hosp.DepartmentVo;
import io.yao.harp.vo.hosp.HospitalQueryVo;
import io.yao.harp.vo.hosp.ScheduleOrderVo;
import io.yao.harp.vo.order.SignInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp/hospital")
public class HospitalApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Operation(summary = "Get all hospital")
    @GetMapping("getHospitalList/{page}/{limit}")
    public Result getHospitalList(@PathVariable Integer page,
                                  @PathVariable Integer limit,
                                  HospitalQueryVo hospitalQueryVo) {
        Page<Hospital> hospitalList = hospitalService.selectHospitalPage(page, limit, hospitalQueryVo);
        return Result.ok(hospitalList);
    }

    @Operation(summary = "Get hospitals by hospital name")
    @GetMapping("findHospitalsByName/{hosname}")
    public Result findHospitalsByName(@PathVariable String hosname) {
        List<Hospital> hospitalList = hospitalService.getHospitalsByName(hosname);
        return Result.ok(hospitalList);
    }

    @Operation(summary = "Get department by hospitao code")
    @GetMapping("department/{hoscode}")
    public  Result getDepartmentByHoscode(@PathVariable String hoscode) {
        List<DepartmentVo> deptTree = departmentService.getDeptTree(hoscode);
        return Result.ok(deptTree);
    }

    @Operation(summary = "Get hospital booking detail by hoscode")
    @GetMapping("{hoscode}")
    public Result findHospitalDetail(@PathVariable String hoscode) {
        Map<String, Object> result = hospitalService.getHospitalDetail(hoscode);
        return Result.ok(result);
    }

    @Operation(summary = "获取可预约排班数据")
    @GetMapping("auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getBookingSchedule(
            @Parameter(name = "page", description = "当前页码", required = true)
            @PathVariable Integer page,
            @Parameter(name = "limit", description = "每页记录数", required = true)
            @PathVariable Integer limit,
            @Parameter(name = "hoscode", description = "医院code", required = true)
            @PathVariable String hoscode,
            @Parameter(name = "depcode", description = "科室code", required = true)
            @PathVariable String depcode) {
        return Result.ok(scheduleService.getBookingScheduleRule(page, limit, hoscode, depcode));
    }

    @Operation(summary = "获取排班数据")
    @GetMapping("auth/findScheduleList/{hoscode}/{depcode}/{workDate}")
    public Result findScheduleList(
            @Parameter(name = "hoscode", description = "医院code", required = true)
            @PathVariable String hoscode,
            @Parameter(name = "depcode", description = "科室code", required = true)
            @PathVariable String depcode,
            @Parameter(name = "workDate", description = "排班日期", required = true)
            @PathVariable String workDate) {
        return Result.ok(scheduleService.getScheduleDetail(hoscode, depcode, workDate));
    }

    @Operation(description = "根据排班id获取排班数据")
    @GetMapping("getSchedule/{scheduleId}")
    public Result getSchedule(
            @Parameter(name = "scheduleId", description = "排班id", required = true)
            @PathVariable String scheduleId) {
        return Result.ok(scheduleService.getById(scheduleId));
    }

    @Operation(summary = "获取医院签名信息")
    @GetMapping("inner/getSignInfoVo/{hoscode}")
    public SignInfoVo getSignInfoVo(
            @Parameter(name = "hoscode", description = "医院code", required = true)
            @PathVariable("hoscode") String hoscode) {
        return hospitalSetService.getSignInfoVo(hoscode);
    }


    @Operation(description = "根据排班id获取预约下单数据")
    @GetMapping("inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(
            @Parameter(name = "scheduleId", description = "排班id", required = true)
            @PathVariable("scheduleId") String scheduleId) {
        return scheduleService.getScheduleOrderVo(scheduleId);
    }
}
