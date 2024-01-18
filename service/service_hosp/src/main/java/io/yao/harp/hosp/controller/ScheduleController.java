package io.yao.harp.hosp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.yao.harp.common.result.Result;
import io.yao.harp.hosp.service.ScheduleService;
import io.yao.harp.model.hosp.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController

@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Operation(summary = "Get schedule info by hoscode and deptcode")
    @GetMapping("scheduleInfo/{page}/{limit}/{hoscode}/{deptcode}")
    public Result getScheduleInfoByHoscodeAndDeptcode(@PathVariable Long page,
                                                      @PathVariable Long limit,
                                                      @PathVariable String hoscode,
                                                      @PathVariable String deptcode) {
        Map<String, Object> map = scheduleService.getScheduleRule(page, limit, hoscode, deptcode);
        return Result.ok(map);
    }

    // get schedule detailed info by hospital code, department code, work date
    @Operation(summary = "Get detailed schedule info")
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail(@PathVariable String hoscode,
                                    @PathVariable String  depcode,
                                    @PathVariable String workDate) {
        List<Schedule> scheduleList = scheduleService.getScheduleDetail(hoscode, depcode, workDate);
        return Result.ok(scheduleList);
    }
}
