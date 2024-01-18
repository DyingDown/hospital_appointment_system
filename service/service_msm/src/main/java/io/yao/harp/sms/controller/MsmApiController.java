package io.yao.harp.sms.controller;


import com.alibaba.excel.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.yao.harp.common.result.Result;
import io.yao.harp.sms.service.MsmService;
import io.yao.harp.sms.utils.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/msm")
public class MsmApiController {

    @Autowired
    private MsmService msmService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Operation(summary = "Send verification code message")
    @GetMapping("send/{phone}")
    public Result sendCode(@PathVariable String phone) {
        // first check if code exists
        String code = redisTemplate.opsForValue().get(phone);
        if(!StringUtils.isEmpty(code)) {
            return Result.ok();
        }
        // no code in redis, generate a new code
        code = RandomUtils.getSixBitRandom();
        boolean isSend = msmService.send(phone, code);
        if(!isSend) {
            return Result.fail().message("Failed to send code");
        }
        redisTemplate.opsForValue().set(phone, code, 2, TimeUnit.MINUTES);
        return Result.ok();
    }
}
