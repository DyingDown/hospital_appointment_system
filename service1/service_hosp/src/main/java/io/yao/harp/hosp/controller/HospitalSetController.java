package io.yao.harp.hosp.controller;

import com.sun.org.apache.xpath.internal.operations.Bool;
import io.yao.harp.hosp.service.HospitalSetService;
import io.yao.harp.model.hosp.HospitalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    @GetMapping("findAll")
    public List<HospitalSet> getAllHospitalSet() {
        return hospitalSetService.list();
    }

    @GetMapping("{id}")
    public boolean deleteHospitalById(@PathVariable Long id) {
        return hospitalSetService.removeById(id);
    }
}
