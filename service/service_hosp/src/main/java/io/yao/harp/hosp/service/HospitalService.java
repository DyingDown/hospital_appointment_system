package io.yao.harp.hosp.service;

import io.yao.harp.model.hosp.Hospital;
import io.yao.harp.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {
    void save(Map<String, Object> parameterMap);

    Hospital getByHoscode(String hoscode);

    // select hospital page by condition
    Page<Hospital> selectHospitalPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);

    // get hospital detailed information
    Map<String, Object> getHospialById(String id);


    String getHospNameByHoscode(String hoscode);

    List<Hospital> getHospitalsByName(String hosname);

    // get hospital detail and booking info by hospital code
    Map<String, Object> getHospitalDetail(String hoscode);
}
