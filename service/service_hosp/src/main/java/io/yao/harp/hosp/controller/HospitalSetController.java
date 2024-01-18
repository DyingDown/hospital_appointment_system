package io.yao.harp.hosp.controller;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.yao.harp.common.utils.MD5;
import io.yao.harp.hosp.service.HospitalSetService;
import io.yao.harp.model.hosp.HospitalSet;
import io.yao.harp.vo.hosp.HospitalSetQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import io.yao.harp.common.result.Result;

import java.util.List;
import java.util.Queue;
import java.util.Random;

@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@Tag(name = "Hospital Appointment and Registration Platform")

public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    @Operation(summary = "Find all the hospitals")
    @GetMapping("findAll")
    public Result getAllHospitalSet() {
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    @Operation(summary = "Soft Delete hospital by its id")
    @DeleteMapping("{id}")
    public Result deleteHospitalById(@PathVariable Long id) {
        boolean res = hospitalSetService.removeById(id);
        if(res) {
            return Result.ok(res);
        } else {
            return Result.fail(res);
        }
    }

    @Operation(summary = "Find hospital by pages")
    @PostMapping("findPageHospitalSet/{current}/{limit}")
    public Result findPageHospitalSet(@PathVariable Long current,
                                      @PathVariable Long limit,
                                      @RequestBody HospitalSetQueryVo hospitalSetQueryVo) {
        Page<HospitalSet> page = new Page<>(current, limit);

        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        String code = hospitalSetQueryVo.getHoscode();
        String name = hospitalSetQueryVo.getHosname();
        if(!StringUtils.isEmpty(code)) {
            wrapper.eq("hoscode", hospitalSetQueryVo.getHoscode());
        }
        if(!StringUtils.isEmpty(name)) {
            wrapper.like("hosname", hospitalSetQueryVo.getHosname());
        }

        Page<HospitalSet> hospitalSetPage = hospitalSetService.page(page, wrapper);
        return Result.ok(hospitalSetPage);
    }

    @Operation(summary = "Add hospital")
    @PostMapping("addHospital")
    public Result addHospital(@RequestBody HospitalSet hospitalSet) {
        // status 1 means able to use, 0 means not able to use
        hospitalSet.setStatus(1);
        Random random = new Random();
        // set sign key
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));
        boolean saved = hospitalSetService.save(hospitalSet);
        if(saved) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @Operation(summary = "Get hopstial information by Id")
    @GetMapping("getHospitalSet/{id}")
    public Result getHospitalSetById(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    @Operation(summary = "Update hospital information")
    @PostMapping("updateHospitalSet")
    public Result modifyHospitalSet(@RequestBody HospitalSet hospitalSet) {
        boolean updated = hospitalSetService.updateById(hospitalSet);
        if(updated) {
            return Result.ok();
        } else  {
            return Result.fail();
        }
    }

    @Operation(summary = "Batch delete hospital information")
    @DeleteMapping("batchDelete")
    public Result batchDeleteHospitalSet(@RequestBody List<Long> idList) {
        boolean deleted = hospitalSetService.removeByIds(idList);
        if(deleted) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @Operation(summary = "Lock and unlock the hospital status")
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id, @PathVariable Integer status) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        hospitalSet.setStatus(status);
        boolean updated = hospitalSetService.updateById(hospitalSet);
        if(updated) return Result.ok();
        else return Result.fail();
    }

    @Operation(summary = "Send sign key")
    @PutMapping("/sendKey/{id}")
    public  Result sendKey(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        // TODO: send text message
        return Result.ok();
    }

}
