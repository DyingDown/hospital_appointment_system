package io.yao.harp.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.yao.harp.cmn.client.DictFeignClient;
import io.yao.harp.hosp.repository.HospitalRepository;
import io.yao.harp.hosp.service.HospitalService;
import io.yao.harp.model.hosp.Hospital;
import io.yao.harp.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public void save(Map<String, Object> parameterMap) {
        String mapString = JSONObject.toJSONString(parameterMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);

        // if data exist
        String hoscode = hospital.getHoscode();
        Hospital targetHospital  = hospitalRepository.getHospitalByHoscode(hoscode);

        // if not exists, then add
        // else, then modify
        if(targetHospital  != null) {
            hospital.setStatus(targetHospital.getStatus());
            hospital.setCreateTime(targetHospital.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        } else {
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospital;
    }

    @Override
    public Page<Hospital> selectHospitalPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);
        Example example = Example.of(hospital, matcher);
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);
        
        pages.getContent().stream().forEach(item -> {
            this.setHospitalType(item);
        });
        return pages;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        Hospital hospital = hospitalRepository.findById(id).get();
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }

    @Override
    public Map<String, Object> getHospialById(String id) {
        Map<String, Object> result = new HashMap<>();
        Hospital hospital = this.setHospitalType(hospitalRepository.findById(id).get());
        result.put("hospital", hospital);
        result.put("bookingRule", hospital.getBookingRule());
        hospital.setBookingRule(null);
        return result;
    }

    @Override
    public String getHospNameByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if(hospital != null) {
            return hospital.getHosname();
        }
        return null;
    }

    @Override
    public List<Hospital> getHospitalsByName(String hosname) {
        return hospitalRepository.getHospitalByHosnameLike(hosname);
    }

    @Override
    public Map<String, Object> getHospitalDetail(String hoscode) {
        Map<String, Object> result = new HashMap<>();
        Hospital hospital = this.setHospitalType(this.getByHoscode(hoscode));
        result.put("hospital", hospital);
        result.put("bookingRule", hospital.getBookingRule());
        hospital.setBookingRule(null);
        return result;
    }

    private Hospital setHospitalType(Hospital hospital) {
        String hosTypeName = dictFeignClient.getName("Hostype", hospital.getHostype());
        String provinceName = dictFeignClient.getName(hospital.getProvinceCode());
        String cityName = dictFeignClient.getName(hospital.getCityCode());
        String districtName = dictFeignClient.getName(hospital.getDistrictCode());

        hospital.getParam().put("hosTypeString", hosTypeName);
        hospital.getParam().put("fullAddress", provinceName+cityName+districtName);

        return hospital;
    }
}
