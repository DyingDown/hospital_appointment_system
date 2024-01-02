package io.yao.easyexcel;

import com.alibaba.excel.EasyExcel;

public class TestRead {
    public static void main(String[] args) {
        String filename = "D:\\Documents\\Code\\excel-test\\01.xlsx";

        EasyExcel.read(filename, UserDate.class, new ExcelListener()).sheet().doRead();
    }
}
