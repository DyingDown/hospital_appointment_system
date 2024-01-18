package io.yao.harp.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.yao.harp.model.user.Patient;

import java.util.List;

public interface PatientService extends IService<Patient> {
    List<Patient> getPatientByUserId(Long userId);

    Patient getPatientById(Long id);
}
