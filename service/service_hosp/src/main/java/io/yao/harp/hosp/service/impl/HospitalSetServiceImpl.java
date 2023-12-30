package io.yao.harp.hosp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.yao.harp.hosp.mapper.HospitalSetMapper;
import io.yao.harp.hosp.service.HospitalSetService;
import io.yao.harp.model.hosp.HospitalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {

}
