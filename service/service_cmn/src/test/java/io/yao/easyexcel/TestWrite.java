package io.yao.easyexcel;

import com.alibaba.excel.EasyExcel;
import io.yao.harp.model.acl.User;

import java.util.ArrayList;
import java.util.List;

public class TestWrite {
    public static void main(String[] args) {
        String path = "D:\\Documents\\Code\\excel-test\\01.xlsx";

        List<UserDate> list = new ArrayList<>();

        for(int i = 0; i < 10; i ++) {
            UserDate userdate = new UserDate();
            userdate.setUid(i);
            userdate.setUsername("yao" + i);
            list.add(userdate);
        }

        EasyExcel.write(path, UserDate.class).sheet("user information")
                .doWrite(list);
    }
}
