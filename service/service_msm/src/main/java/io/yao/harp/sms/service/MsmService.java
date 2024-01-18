package io.yao.harp.sms.service;

import io.yao.harp.vo.msm.MsmVo;

public interface MsmService {

    // send verification code
    boolean send(String phone, String code);

    boolean send(MsmVo msmVo);
}
