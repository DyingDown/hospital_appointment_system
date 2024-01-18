package io.yao.harp.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.yao.harp.model.hosp.HospitalSet;
import io.yao.harp.vo.order.SignInfoVo;

public interface HospitalSetService extends IService<HospitalSet> {
    String getSignedKey(String hoscode);

    SignInfoVo getSignInfoVo(String hoscode);
}
