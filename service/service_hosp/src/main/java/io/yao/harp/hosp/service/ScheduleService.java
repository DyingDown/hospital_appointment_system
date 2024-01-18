package io.yao.harp.hosp.service;

import io.yao.harp.model.hosp.Schedule;
import io.yao.harp.vo.hosp.ScheduleOrderVo;
import io.yao.harp.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    void save(Map<String, Object> parameterMap);

    Page<Schedule> findSchedulePage(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    void remove(String hoscode, String hosScheduleId);

    // Get schedule info by hoscode and deptcode
    Map<String, Object> getScheduleRule(Long page, Long limit, String hoscode, String deptcode);

    List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate);

    Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);

    Schedule getById(String scheduleId);

    ScheduleOrderVo getScheduleOrderVo(String scheduleId);

    Schedule getByHosScheduleId(String hosScheduleId);


    // used for mq
    void update(Schedule schedule);
}
