package io.yao.harp.hosp.controller.api;

import com.alibaba.excel.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.yao.harp.common.exception.HarpException;
import io.yao.harp.common.helper.HttpRequestHelper;
import io.yao.harp.common.result.Result;
import io.yao.harp.common.result.ResultCodeEnum;
import io.yao.harp.common.utils.HttpUtil;
import io.yao.harp.common.utils.MD5;
import io.yao.harp.hosp.service.DepartmentService;
import io.yao.harp.hosp.service.HospitalService;
import io.yao.harp.hosp.service.HospitalSetService;
import io.yao.harp.hosp.service.ScheduleService;
import io.yao.harp.hosp.utils.HospUtil;
import io.yao.harp.model.hosp.Department;
import io.yao.harp.model.hosp.Hospital;
import io.yao.harp.model.hosp.Schedule;
import io.yao.harp.vo.hosp.DepartmentQueryVo;
import io.yao.harp.vo.hosp.ScheduleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Tag(name = "Hospital Management API")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

//    @Autowired
//    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private HospUtil hospUtil;

    @Operation(summary = "upload hosptial")
    @PostMapping("saveHospital")
    public Result saveHospital(HttpServletRequest request) {
        Map<String, Object> parameterMap = hospUtil.preProcessData(request);

        String hoscode = (String) parameterMap.get("hoscode");

        hospUtil.verifyHospSignedKey(hoscode, (String)parameterMap.get("sign"));

        String logoData = (String) parameterMap.get("logoData");
        logoData = logoData.replaceAll(" ", "+");
        parameterMap.put("logoData", logoData);

        hospitalService.save(parameterMap);
        return Result.ok();
    }

    @Operation(summary = "search hospital")
    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request) {
        Map<String, Object> parameterMap = hospUtil.preProcessData(request);

        String hoscode = (String) parameterMap.get("hoscode");

        hospUtil.verifyHospSignedKey(hoscode, (String)parameterMap.get("sign"));

        Hospital hospital = hospitalService.getByHoscode(hoscode);

        return Result.ok(hospital);
    }

    @Operation(summary = "上传科室")
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        // get department information
        Map<String, Object> parameterMap = hospUtil.preProcessData(request);
        String hoscode = (String) parameterMap.get("hoscode");

        hospUtil.verifyHospSignedKey(hoscode, (String)parameterMap.get("sign"));

        departmentService.save(parameterMap);
        return Result.ok();

    }
    @Operation(summary = "Get department list")
    @PostMapping("department/list")
    public Result getDepartmentList(HttpServletRequest request) {
        Map<String, Object> parameterMap = hospUtil.preProcessData(request);
        String hoscode = (String) parameterMap.get("hoscode");
        int page = StringUtils.isEmpty(parameterMap.get("page")) ? 1 : Integer.parseInt((String) parameterMap.get("page"));
        int limit = StringUtils.isEmpty(parameterMap.get("limit")) ? 10 : Integer.parseInt((String) parameterMap.get("limit"));

        hospUtil.verifyHospSignedKey(hoscode, (String)parameterMap.get("sign"));

        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);

        Page<Department> pages = departmentService.findPageDepartment(page, limit, departmentQueryVo);
        return Result.ok(pages);
    }

    @Operation(summary = "delete department")
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request) {
        Map<String, Object> parameterMap = hospUtil.preProcessData(request);
        String hoscode = (String) parameterMap.get("hoscode");
        String depcode = (String) parameterMap.get("depcode");
        hospUtil.verifyHospSignedKey(hoscode, (String)parameterMap.get("sign"));

        departmentService.remove(hoscode, depcode);
        return Result.ok();
    }

    @Operation(summary = "upload doctors schedule")
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {
        Map<String, Object> parameterMap = hospUtil.preProcessData(request);
        String hoscode = (String) parameterMap.get("hoscode");
        hospUtil.verifyHospSignedKey(hoscode, (String)parameterMap.get("sign"));

        scheduleService.save(parameterMap);
        return Result.ok();
    }
    @Operation(summary = "Get the schedule information")
    @PostMapping("schedule/list")
    public Result getScheduleList(HttpServletRequest request) {
        Map<String, Object> parameterMap = hospUtil.preProcessData(request);
        String hoscode = (String) parameterMap.get("hoscode");
        String depcode = (String) parameterMap.get("depcode");

        int page = StringUtils.isEmpty(parameterMap.get("page")) ? 1 : Integer.parseInt((String) parameterMap.get("page"));
        int limit = StringUtils.isEmpty(parameterMap.get("limit")) ? 10 : Integer.parseInt((String) parameterMap.get("limit"));

        hospUtil.verifyHospSignedKey(hoscode, (String)parameterMap.get("sign"));

        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);

        Page<Schedule> pages = scheduleService.findSchedulePage(page, limit, scheduleQueryVo);
        return Result.ok(pages);
    }

    @Operation(summary = "remove schedule")
    @PostMapping("schedule/remove")
    public Result removeSchedule(HttpServletRequest request) {
        Map<String, Object> parameterMap = hospUtil.preProcessData(request);
        String hoscode = (String) parameterMap.get("hoscode");
        String hosScheduleId = (String) parameterMap.get("hosScheduleId");

        hospUtil.verifyHospSignedKey(hoscode, (String)parameterMap.get("sign"));
        scheduleService.remove(hoscode, hosScheduleId);
        return Result.ok();
    }
}
