package io.yao.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserDate {

    @ExcelProperty(value = "用户编号", index = 0)
    private int uid;

    @ExcelProperty(value = "user name", index = 1)
    private String username;
}
