package io.yao.harp.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.yao.harp.model.user.UserInfo;
import io.yao.harp.vo.user.LoginVo;
import io.yao.harp.vo.user.UserAuthVo;
import io.yao.harp.vo.user.UserInfoQueryVo;

import java.util.Map;

public interface UserInfoService {
    Map<String, Object> login(LoginVo loginVo);

    // add  user auth info
    void userAuth(Long userId, UserAuthVo userAuthVo);

    UserInfo getById(Long userId);

    IPage<UserInfo> selectPage(Page<UserInfo> pageParams, UserInfoQueryVo userInfoQueryVo);

    void lockById(String id, Integer status);

    Map<String, Object> showUserInfo(Long id);

    void approve(Long userId, Integer authStatus);
}
