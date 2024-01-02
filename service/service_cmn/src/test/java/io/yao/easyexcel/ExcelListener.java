package io.yao.easyexcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.Map;

public class ExcelListener extends AnalysisEventListener<UserDate> {

    @Override
    public void invoke(UserDate userDate, AnalysisContext context) {
        System.out.println(userDate);
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        System.out.println("headMap info:" + headMap);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
