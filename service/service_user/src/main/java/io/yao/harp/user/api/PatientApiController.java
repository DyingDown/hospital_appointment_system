package io.yao.harp.user.api;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.yao.harp.common.result.Result;
import io.yao.harp.common.utils.AuthContextHolder;
import io.yao.harp.hosp.service.ScheduleService;
import io.yao.harp.model.user.Patient;
import io.yao.harp.user.service.PatientService;
import io.yao.harp.vo.hosp.ScheduleOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/user/patient")
public class PatientApiController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private ScheduleService scheduleService;

    @Operation(summary = "Get Patient List")
    @GetMapping("auth/findAll")
    public Result getPatientList(HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        List<Patient> patientList = patientService.getPatientByUserId(userId);
        return Result.ok(patientList);
    }

    @Operation(summary = "Add patient by ID")
    @PostMapping("auth/save")
    public Result savePatient(@RequestBody Patient patient, HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        patient.setUserId(userId);
        patientService.save(patient);
        return Result.ok();
    }

    @Operation(summary = "Get patient by patient's Id")
    @GetMapping("auth/get/{id}")
    public Result getPatientById(@PathVariable Long id) {
        Patient patient = patientService.getPatientById(id);
        return Result.ok(patient);
    }

    @Operation(summary = "Update patient info")
    @PostMapping("auth/update")
    public Result updatePatient(@RequestBody Patient patient) {
        patientService.updateById(patient);
        return Result.ok();
    }

    @Operation(summary = "Delete patient")
    @DeleteMapping("auth/remove/{id}")
    public Result removePatientById(@PathVariable Long id) {
        patientService.removeById(id);
        return Result.ok();
    }

    @Operation(description = "获取就诊人")
    @GetMapping("inner/get/{id}")
    public Patient getPatientOrder(
            @Parameter(name = "id", description = "就诊人id", required = true)
            @PathVariable("id") Long id) {
        Patient patient = patientService.getById(id);
        return patient;
    }

}
