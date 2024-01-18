package io.yao.harp.user.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.yao.harp.common.result.Result;
import io.yao.harp.common.utils.AuthContextHolder;
import io.yao.harp.model.user.UserInfo;
import io.yao.harp.user.service.UserInfoService;
import io.yao.harp.vo.user.LoginVo;
import io.yao.harp.vo.user.UserAuthVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Information API")
public class UserInfoApiController {
    @Autowired
    private UserInfoService userService;

    @Operation(summary = "User Login By Phone")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo) {
        Map<String, Object> userInfo = userService.login(loginVo);
        return Result.ok(userInfo);
    }


    @Operation(summary = "Auth Info")
    @PostMapping("auth/userAuth")
    public Result userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request) {
        userService.userAuth(AuthContextHolder.getUserId(request), userAuthVo);
        return Result.ok();
    }

    @Operation(summary = "Get user Info")
    @GetMapping("auth/getUserInfo")
    public Result getUserInfo(HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        UserInfo userInfo = userService.getById(userId);
        return Result.ok(userInfo);
    }

}
