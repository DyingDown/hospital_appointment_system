package io.yao.harp.task.schedule;

import io.yao.common.rabbitmq.constant.MqConst;
import io.yao.common.rabbitmq.service.RabbitMQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduledTask {

    @Autowired
    private RabbitMQService rabbitMQService;

    //@Scheduled(cron = "0 0 8 * * ?")
    @Scheduled(cron = "0/30 * * * * ?")
    public void remindPatient() {
        rabbitMQService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK, MqConst.ROUTING_TASK_8, "");
    }
}
