package io.yao.harp.order.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.yao.common.rabbitmq.constant.MqConst;
import io.yao.common.rabbitmq.service.RabbitMQService;
import io.yao.harp.common.exception.HarpException;
import io.yao.harp.common.helper.HttpRequestHelper;
import io.yao.harp.common.result.ResultCodeEnum;
import io.yao.harp.common.utils.MD5;
import io.yao.harp.enums.OrderStatusEnum;
import io.yao.harp.hospital.client.HospitalFeignClient;
import io.yao.harp.model.order.OrderInfo;
import io.yao.harp.model.user.Patient;
import io.yao.harp.order.mapper.OrderInfoMapper;
import io.yao.harp.order.service.OrderService;
import io.yao.harp.order.service.XXPayService;
import io.yao.harp.user.client.PatientFeignClient;
import io.yao.harp.vo.hosp.ScheduleOrderVo;
import io.yao.harp.vo.msm.MsmVo;
import io.yao.harp.vo.order.OrderMqVo;
import io.yao.harp.vo.order.OrderQueryVo;
import io.yao.harp.vo.order.SignInfoVo;
import org.apache.poi.util.StringUtil;
import org.aspectj.weaver.ast.Or;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderServiceImpl extends
        ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderService {

    @Autowired
    private HospitalFeignClient hospitalFeignClient;

    @Autowired
    private PatientFeignClient patientFeignClient;

    @Autowired
    private RabbitMQService rabbitMQService;

    @Autowired
    private XXPayService xxPayService;

    @Override
    public Long saveOrder(String scheduleId, Long patientId) {
        ScheduleOrderVo scheduleOrderVo = hospitalFeignClient.getScheduleOrderVo(scheduleId);
        if(null == scheduleOrderVo) {
            throw new HarpException(ResultCodeEnum.PARAM_ERROR);
        }

        Patient patient = patientFeignClient.getPatient(patientId);
        if(null == patient) {
            throw new HarpException(ResultCodeEnum.PARAM_ERROR);
        }

        //当前时间不可以预约 TODO:注释回来
//        if(new DateTime(scheduleOrderVo.getStartTime()).isAfterNow()
//                || new DateTime(scheduleOrderVo.getEndTime()).isBeforeNow()) {
//            throw new HarpException(ResultCodeEnum.TIME_NO);
//        }

        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(scheduleOrderVo.getHoscode());
        if(null == signInfoVo) {
            throw new HarpException(ResultCodeEnum.PARAM_ERROR);
        }
        if(scheduleOrderVo.getAvailableNumber() <= 0) {
            throw new HarpException(ResultCodeEnum.NUMBER_NO);
        }

        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(scheduleOrderVo, orderInfo);
        String outTradeNo = System.currentTimeMillis() + ""+ new Random().nextInt(100);
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setScheduleId(scheduleOrderVo.getHosScheduleId());
        orderInfo.setUserId(patient.getUserId());
        orderInfo.setPatientId(patientId);
        orderInfo.setPatientName(patient.getName());
        orderInfo.setPatientPhone(patient.getPhone());
        orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
        this.save(orderInfo);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode",orderInfo.getHoscode());
        paramMap.put("depcode",orderInfo.getDepcode());
        paramMap.put("hosScheduleId",orderInfo.getScheduleId());
        paramMap.put("reserveDate",new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", orderInfo.getReserveTime());
        paramMap.put("amount",orderInfo.getAmount());
        paramMap.put("name", patient.getName());
        paramMap.put("certificatesType",patient.getCertificatesType());
        paramMap.put("certificatesNo", patient.getCertificatesNo());
        paramMap.put("sex",patient.getSex());
        paramMap.put("birthdate", patient.getBirthdate());
        paramMap.put("phone",patient.getPhone());
        paramMap.put("isMarry", patient.getIsMarry());
        paramMap.put("provinceCode",patient.getProvinceCode());
        paramMap.put("cityCode", patient.getCityCode());
        paramMap.put("districtCode",patient.getDistrictCode());
        paramMap.put("address",patient.getAddress());
        //联系人
        paramMap.put("contactsName",patient.getContactsName());
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo",patient.getContactsCertificatesNo());
        paramMap.put("contactsPhone",patient.getContactsPhone());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());
//        String sign = HttpRequestHelper.getSign(paramMap, signInfoVo.getSignKey());
        String sign = MD5.encrypt(signInfoVo.getSignKey());
        paramMap.put("sign", sign);

        JSONObject result = HttpRequestHelper.sendRequest(paramMap, signInfoVo.getApiUrl() + "/order/submitOrder/");

        if(result.getInteger("code") == 200) {
            JSONObject jsonObject = result.getJSONObject("data");
            //预约记录唯一标识（医院预约记录主键）
            String hosRecordId = jsonObject.getString("hosRecordId");
            //预约序号
            Integer number = jsonObject.getInteger("number");;
            //取号时间
            String fetchTime = jsonObject.getString("fetchTime");;
            //取号地址
            String fetchAddress = jsonObject.getString("fetchAddress");;
            //更新订单
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);
            baseMapper.updateById(orderInfo);
            //排班可预约数
            Integer reservedNumber = jsonObject.getInteger("reservedNumber");
            //排班剩余预约数
            Integer availableNumber = jsonObject.getInteger("availableNumber");
            //发送mq信息更新号源和短信通知
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            orderMqVo.setReservedNumber(reservedNumber);
            orderMqVo.setAvailableNumber(availableNumber);

            //短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate =
                    new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")
                            + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("amount", orderInfo.getAmount());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
                put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
            }};
            msmVo.setParam(param);

            orderMqVo.setMsmVo(msmVo);
            rabbitMQService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
        } else {
            throw new HarpException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        }
        return orderInfo.getId();

    }

    @Override
    public OrderInfo getOrder(String orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        return this.packOrderInfo(orderInfo);
    }

    @Override
    public IPage<OrderInfo> selectPage(Page<OrderInfo> pageParams, OrderQueryVo orderQueryVo) {
        // get orderQueryVo value
        String name = orderQueryVo.getKeyword(); // hospital name
        Long patientId = orderQueryVo.getPatientId();
        String patientName = orderQueryVo.getPatientName();
        String reserveDate = orderQueryVo.getReserveDate();
        String orderStatus = orderQueryVo.getOrderStatus();
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();


        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(name)) {
            wrapper.like("hosname", name);
        }
        if(!StringUtils.isEmpty(patientId)) {
            wrapper.eq("patient_id", patientId);
        }
        if(!StringUtils.isEmpty(patientName)) {
            wrapper.eq("patient_name", patientName);
        }
        if(!StringUtils.isEmpty(orderStatus)) {
            wrapper.eq("order_status", orderStatus);
        }
        if(!StringUtils.isEmpty(reserveDate)) {
            wrapper.ge("reserve_date", reserveDate);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time", createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("quit_time", createTimeEnd);
        }
        IPage<OrderInfo> orderInfoPage = baseMapper.selectPage(pageParams, wrapper);
        orderInfoPage.getRecords().forEach(this::packOrderInfo);
        return orderInfoPage;
    }

    @Override
    public Boolean cancelOrder(Long orderId) {
        // get order info
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        // whether satisfy cancel status
        if(orderId == null) {
            throw new HarpException(ResultCodeEnum.PARAM_ERROR);
        }
        if(orderInfo.getOrderStatus() == OrderStatusEnum.CANCLE.getStatus().intValue()) {
            return true;
        }
        // is already passed the allowed cancel time
        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
        if(quitTime.isBeforeNow()) {
            throw new HarpException(ResultCodeEnum.CANCEL_ORDER_NO);
        }

        // request hospital's own api (third platform)
        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(orderInfo.getHoscode());
        if(null == signInfoVo) {
            throw new HarpException(ResultCodeEnum.PARAM_ERROR);
        }
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("hoscode",orderInfo.getHoscode());
        reqMap.put("hosRecordId",orderInfo.getHosRecordId());
        reqMap.put("timestamp", HttpRequestHelper.getTimestamp());
//        String sign = HttpRequestHelper.getSign(reqMap, signInfoVo.getSignKey());
        String sign = MD5.encrypt(signInfoVo.getSignKey());
        reqMap.put("sign", sign);

        JSONObject result = HttpRequestHelper.sendRequest(reqMap, signInfoVo.getApiUrl()+"/order/updateCancelStatus");

        if(result.getInteger("code") != 200) {
            throw new HarpException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        } else {
            // if the order is able to refund
            if(orderInfo.getOrderStatus() == OrderStatusEnum.PAID.getStatus().intValue()) {
                Boolean isRefund = xxPayService.refund(orderId);
                if (isRefund == false) {
                    throw new HarpException(ResultCodeEnum.CANCEL_ORDER_FAIL);
                }
            }

            orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
            baseMapper.updateById(orderInfo);

            // rabbit mq to add an available order number
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(orderInfo.getScheduleId());
            System.out.println("退款：scheduleId = " + orderInfo.getScheduleId());
            // send text message
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            orderMqVo.setMsmVo(msmVo);
            rabbitMQService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
        }
        return true;
    }

    @Override
    public void patientTips() {
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("reserve_date", new DateTime().toString("yyyy-MM-dd"));
        queryWrapper.ne("order_status", OrderStatusEnum.CANCLE.getStatus());
        List<OrderInfo> orderInfos = baseMapper.selectList(queryWrapper);
        for(OrderInfo orderInfo : orderInfos) {
            //短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            rabbitMQService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVo);

        }
    }

    private OrderInfo packOrderInfo(OrderInfo orderInfo) {
        orderInfo.getParam().put("orderStatusString", OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        return orderInfo;
    }
}

