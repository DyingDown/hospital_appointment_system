package io.yao.harp.hosp.utils;


import io.yao.harp.common.exception.HarpException;
import io.yao.harp.common.helper.HttpRequestHelper;
import io.yao.harp.common.result.ResultCodeEnum;
import io.yao.harp.common.utils.MD5;
import io.yao.harp.hosp.service.HospitalSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Component
public class HospUtil {

    @Autowired
    private HospitalSetService hospitalSetService;

    public void verifyHospSignedKey(String hoscode, String sign) {
        String signKey = hospitalSetService.getSignedKey(hoscode);

        String encrypted = MD5.encrypt(signKey);
        if(!sign.equals(encrypted)) {
            throw new HarpException(ResultCodeEnum.SIGN_ERROR);
        }
    }

    public Map<String, Object> preProcessData(HttpServletRequest request) {
        Map<String, String[]> requestMap = request.getParameterMap();
        return HttpRequestHelper.switchMap(requestMap);
    }
}
