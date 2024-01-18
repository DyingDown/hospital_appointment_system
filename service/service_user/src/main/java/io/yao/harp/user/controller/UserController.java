package io.yao.harp.user.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.yao.harp.common.result.Result;
import io.yao.harp.model.user.UserInfo;
import io.yao.harp.user.service.UserInfoService;
import io.yao.harp.vo.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/user/")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    @Operation(summary = "get user list")
    @GetMapping("{page}/{limit}")
    public Result getUserList(@PathVariable Long page, @PathVariable Long limit,
                              UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> pageParams = new Page<>(page,limit);
        IPage<UserInfo> pageModel = userInfoService.selectPage(pageParams, userInfoQueryVo);
        return Result.ok(pageModel);
    }

    @Operation(summary = "Lock and Unlock user")
    @GetMapping("lock/{id}/{status}")
    public Result lockById(@PathVariable String id, @PathVariable Integer status) {
        userInfoService.lockById(id, status);
        return Result.ok();
    }

    @Operation(summary = "Get User Info")
    @GetMapping("show/{id}")
    public Result showUserInfo(@PathVariable Long id) {
        Map<String, Object> userInfo = userInfoService.showUserInfo(id);
        return Result.ok(userInfo);
    }


    @Operation(summary = "Approve or Disapprove user")
    @GetMapping("approval/{userId}/{authStatus}")
    public Result approveUser(@PathVariable Long userId, @PathVariable Integer authStatus) {
        userInfoService.approve(userId, authStatus);
        return Result.ok();
    }
}
