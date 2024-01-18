package io.yao.harp.user.service.impl;


import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.yao.harp.common.exception.HarpException;
import io.yao.harp.common.helper.JwtHelper;
import io.yao.harp.common.result.ResultCodeEnum;
import io.yao.harp.enums.AuthStatusEnum;
import io.yao.harp.model.user.Patient;
import io.yao.harp.model.user.UserInfo;
import io.yao.harp.user.mapper.UserInfoMapper;
import io.yao.harp.user.service.PatientService;
import io.yao.harp.user.service.UserInfoService;
import io.yao.harp.vo.user.LoginVo;
import io.yao.harp.vo.user.UserAuthVo;
import io.yao.harp.vo.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private RedisTemplate<String, String > redisTemplate;

    @Autowired
    private PatientService patientService;

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();

        // 验证参数
        if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new HarpException(ResultCodeEnum.PARAM_ERROR);
        }

        // check code is same
        String verifycationCode = redisTemplate.opsForValue().get(phone);
        if(!code.equals(verifycationCode)) {
            throw new HarpException(ResultCodeEnum.CODE_ERROR);
        }

        // phone number is already in use
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        UserInfo userInfo = baseMapper.selectOne(wrapper);
        if(userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setName("");
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            this.save(userInfo);
        }

        // check if user is disabled
        if(userInfo.getStatus() == 0) {
            throw new HarpException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        // TODO: record login status

        Map<String, Object> userMap = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        String token = JwtHelper.createToken(userInfo.getId(), name);

        userMap.put("name", name);
        userMap.put("token", token);
        return userMap;
    }

    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        UserInfo userInfo = baseMapper.selectById(userId);
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        baseMapper.updateById(userInfo);
    }

    @Override
    public UserInfo getById(Long userId) {
        return baseMapper.selectById(userId);
    }

    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> pageParams, UserInfoQueryVo userInfoQueryVo) {

        //UserInfoQueryVo获取条件值
        String name = userInfoQueryVo.getKeyword(); //用户名称
        Integer status = userInfoQueryVo.getStatus();//用户状态
        Integer authStatus = userInfoQueryVo.getAuthStatus(); //认证状态
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin(); //开始时间
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd(); //结束时间
        //对条件值进行非空判断
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(name)) {
            wrapper.like("name",name);
        }
        if(!StringUtils.isEmpty(status)) {
            wrapper.eq("status",status);
        }
        if(!StringUtils.isEmpty(authStatus)) {
            wrapper.eq("auth_status",authStatus);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time",createTimeEnd);
        }
        IPage<UserInfo> pages = baseMapper.selectPage(pageParams, wrapper);
        pages.getRecords().forEach(this::packUserInfo);
        return pages;
    }

    @Override
    public void lockById(String id, Integer status) {
        if(status.intValue() == 0 || status.intValue() == 1) {
            UserInfo userInfo = baseMapper.selectById(id);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }
    }

    @Override
    public Map<String, Object> showUserInfo(Long id) {
        Map<String, Object> user = new HashMap<>();
        UserInfo userInfo = this.packUserInfo(baseMapper.selectById(id));
        user.put("userInfo", userInfo);
        List<Patient> patients = patientService.getPatientByUserId(id);
        user.put("patientList", patients);
        return user;
    }

    // 2 approved, -1 disapproved
    @Override
    public void approve(Long userId, Integer authStatus) {
         if(authStatus.intValue() == 2 || authStatus.intValue() == -1) {
             UserInfo userInfo = baseMapper.selectById(userId);
             userInfo.setAuthStatus(authStatus);
             baseMapper.updateById(userInfo);
         }
    }

    private UserInfo packUserInfo(UserInfo userInfo) {
        //处理认证状态编码
        userInfo.getParam().put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        //处理用户状态 0  1
        String statusString = userInfo.getStatus().intValue()==0 ?"锁定" : "正常";
        userInfo.getParam().put("statusString",statusString);
        return userInfo;
    }
}
