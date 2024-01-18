package io.yao.harp.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sun.javaws.IconUtil;
import io.yao.harp.common.exception.HarpException;
import io.yao.harp.common.result.ResultCodeEnum;
import io.yao.harp.hosp.repository.ScheduleRepository;
import io.yao.harp.hosp.service.DepartmentService;
import io.yao.harp.hosp.service.HospitalService;
import io.yao.harp.hosp.service.ScheduleService;
import io.yao.harp.model.hosp.BookingRule;
import io.yao.harp.model.hosp.Department;
import io.yao.harp.model.hosp.Hospital;
import io.yao.harp.model.hosp.Schedule;
import io.yao.harp.vo.hosp.BookingScheduleRuleVo;
import io.yao.harp.vo.hosp.ScheduleOrderVo;
import io.yao.harp.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.awt.print.Book;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    @Override
    public void save(Map<String, Object> parameterMap) {
        String jsonString = JSONObject.toJSONString(parameterMap);
        Schedule schedule = JSONObject.parseObject(jsonString, Schedule.class);

        Schedule scheduleExist = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());

        if(scheduleExist != null) {
            scheduleExist.setUpdateTime(new Date());
            scheduleExist.setIsDeleted(0);
            scheduleExist.setStatus(1);
            scheduleRepository.save(scheduleExist);
        } else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> findSchedulePage(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        schedule.setIsDeleted(0);
        schedule.setStatus(1);
        Example<Schedule> example = Example.of(schedule, matcher);
        Page<Schedule> all = scheduleRepository.findAll(example, pageable);
        return all;
    }

    @Override
    public void remove(String hoscode, String hosScheduleId) {
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if(schedule != null) {
            scheduleRepository.deleteById(schedule.getId());
        }
    }

    @Override
    public Map<String, Object> getScheduleRule(Long page, Long limit, String hoscode, String deptcode) {
        // select by hoscode and deptcode
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(deptcode);

        // group by workDate

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
                        .first("workDate").as("workDate")
                        // count orders num
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                // sort
                Aggregation.sort(Sort.Direction.DESC, "workDate"),
                // accomplish page function
                Aggregation.skip((page - 1) * limit),
                Aggregation.limit(limit)
        );

        AggregationResults<BookingScheduleRuleVo> aggregationResults = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggregationResults.getMappedResults();

        // get total num of group
        Aggregation totalAggreation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalBookingList = mongoTemplate.aggregate(totalAggreation, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalBookingList.getMappedResults().size();

        // data -> weekdays
        for(BookingScheduleRuleVo book : bookingScheduleRuleVoList) {
            book.setDayOfWeek(this.getDayOfWeek(new DateTime(book.getWorkDate() )));
        }

        // set return data
        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList", bookingScheduleRuleVoList);
        result.put("total", total);

        // get hospital name
        String hospName = hospitalService.getHospNameByHoscode(hoscode);
        Map<String, Object> baseMap = new HashMap<>();
        baseMap.put("hospName", hospName);

        result.put("baseMap", baseMap);

        return result;
    }

    @Override
    public List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate) {
        List<Schedule> scheduleList = scheduleRepository.getScheduleByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, new DateTime(workDate).toDate());
        scheduleList.stream().forEach(item -> {
            this.packSchedule(item);
        });
        return scheduleList;
    }

    @Override
    public Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode) {
        Map<String, Object> result = new HashMap<>();
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        if(hospital == null) {
            throw new HarpException(ResultCodeEnum.DATA_ERROR);
        }
        BookingRule bookingRule = hospital.getBookingRule();

        // get available dates
        IPage ipage = this.getListDate(page, limit, bookingRule);
        List<Date> dateLists = ipage.getRecords();

        // 获取可预约科室的剩余的号
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode)
                .and("workDate").in(dateLists);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("availableNumber").as("availableNumber")
                        .sum("reservedNumber").as("reservedNumber")
        );
        AggregationResults<BookingScheduleRuleVo> aggregationResults = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> scheduleVoList = aggregationResults.getMappedResults();

        // pack schedule content
        Map<Date, BookingScheduleRuleVo> scheduleRuleVoMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(scheduleVoList)) {
            scheduleRuleVoMap = scheduleVoList.stream().collect(
                    Collectors.toMap(BookingScheduleRuleVo::getWorkDate,
                            BookingScheduleRuleVo -> BookingScheduleRuleVo)
            );
        }
        // available booking rule
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<>();
        for(int i = 0; i < dateLists.size(); i ++) {
            Date date = dateLists.get(i);
            BookingScheduleRuleVo bookingScheduleRuleVo = scheduleRuleVoMap.get(date);
            if(bookingScheduleRuleVo == null) {
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                bookingScheduleRuleVo.setDocCount(0);
                bookingScheduleRuleVo.setAvailableNumber(-1);
            }
            bookingScheduleRuleVo.setWorkDate(date);
            bookingScheduleRuleVo.setWorkDateMd(date);
            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);

            //最后一页最后一条记录为即将预约   状态 0：正常 1：即将放号 -1：当天已停止挂号
            if(i == dateLists.size()-1 && page == ipage.getPages()) {
                bookingScheduleRuleVo.setStatus(1);
            } else {
                bookingScheduleRuleVo.setStatus(0);
            }
            //当天预约如果过了停号时间， 不能预约
            if(i == 0 && page == 1) {
                DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if(stopTime.isBeforeNow()) {
                    //停止预约
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }
            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);

        }
        //可预约日期规则数据
        result.put("bookingScheduleList", bookingScheduleRuleVoList);
        result.put("total", ipage.getTotal());
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospitalService.getHospNameByHoscode(hoscode));
        //科室
        Department department =departmentService.getDepartment(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        result.put("baseMap", baseMap);
        System.out.println(result);
        return result;
    }

    @Override
    public Schedule getById(String scheduleId) {
        System.out.println("排班id：" + scheduleId);
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        System.out.print("打印schedule");
        System.out.println(schedule);
        return this.packSchedule(schedule);
    }

    @Override
    public ScheduleOrderVo getScheduleOrderVo(String scheduleId) {
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        Schedule schedule = this.getById(scheduleId);
        if(schedule == null) {
            throw new HarpException(ResultCodeEnum.PARAM_ERROR);
        }
        Hospital hospital = hospitalService.getByHoscode(schedule.getHoscode());
        if(hospital == null) {
            throw new HarpException(ResultCodeEnum.PARAM_ERROR);
        }

        BookingRule bookingRule = hospital.getBookingRule();
        if(null == bookingRule) {
            throw new HarpException(ResultCodeEnum.PARAM_ERROR);
        }

        scheduleOrderVo.setHoscode(schedule.getHoscode());
        scheduleOrderVo.setHosname(hospitalService.getHospNameByHoscode(schedule.getHoscode()));
        scheduleOrderVo.setDepcode(schedule.getDepcode());
        scheduleOrderVo.setDepname(departmentService.getDepNameByHoscodeAndDepcode(schedule.getHoscode(), schedule.getDepcode()));
        scheduleOrderVo.setHosScheduleId(schedule.getHosScheduleId());
        scheduleOrderVo.setAvailableNumber(schedule.getAvailableNumber());
        scheduleOrderVo.setTitle(schedule.getTitle());
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        scheduleOrderVo.setAmount(schedule.getAmount());

        //退号截止天数（如：就诊前一天为-1，当天为0）
        int quitDay = bookingRule.getQuitDay();
        DateTime quitTime = this.getDateTime(new DateTime(schedule.getWorkDate()).plusDays(quitDay).toDate(), bookingRule.getQuitTime());
        scheduleOrderVo.setQuitTime(quitTime.toDate());

        //预约开始时间
        DateTime startTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        scheduleOrderVo.setStartTime(startTime.toDate());

        //预约截止时间
        DateTime endTime = this.getDateTime(new DateTime().plusDays(bookingRule.getCycle()).toDate(), bookingRule.getStopTime());
        scheduleOrderVo.setEndTime(endTime.toDate());

        //当天停止挂号时间
        DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
        scheduleOrderVo.setStartTime(startTime.toDate());
        return scheduleOrderVo;

    }

    @Override
    public Schedule getByHosScheduleId(String hosScheduleId) {
        return scheduleRepository.getScheduleByHosScheduleId(hosScheduleId);
    }

    // update schedule info used for mq
    @Override
    public void update(Schedule schedule) {
        schedule.setUpdateTime(new Date());
        scheduleRepository.save(schedule);
    }

    private IPage getListDate(Integer page, Integer limit, BookingRule bookingRule) {
        // 当天放号时间
        DateTime releaseTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        // 预约周期
        Integer cycle = bookingRule.getCycle();
        if(releaseTime.isBeforeNow()) {
            cycle += 1;
        }
        List<Date> availableDates = new ArrayList<>();
        for(int i = 0; i < cycle; i ++) {
            DateTime dateTime = new DateTime().plusDays(i);
            String  dateString = dateTime.toString("yyyy-MM-dd");
            availableDates.add(new DateTime(dateString).toDate());
        }
        List<Date> pageDataList = new ArrayList<>();
        int start = (page - 1) * limit;
        int end = page * limit;
        if(end > availableDates.size()) {
            end = availableDates.size();
        }
        for(int i = start; i < end; i ++) {
            pageDataList.add(availableDates.get(i));
        }
        IPage<Date> dateIPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, 7, availableDates.size());
        dateIPage.setRecords(pageDataList);
        System.out.println(dateIPage);
        return dateIPage;
    }

    /**
     * 将Date日期（yyyy-MM-dd HH:mm）转换为DateTime
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " "+ timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }

    private Schedule packSchedule(Schedule item) {
        item.getParam().put("hosname", hospitalService.getHospNameByHoscode(item.getHoscode()));
        item.getParam().put("depname", departmentService.getDepNameByHoscodeAndDepcode(item.getHoscode(), item.getDepcode()));
        item.getParam().put("dayOfWeek", this.getDayOfWeek(new DateTime(item.getWorkDate())));
        return item;
    }

    /**
     * 根据日期获取周几数据
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "Sun.";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "Mon.";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "Tue.";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "Wed.";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "Thu.";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "Fri.";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "Sat.";
            default:
                break;
        }
        return dayOfWeek;
    }
}
