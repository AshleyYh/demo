package com.yuhang.demo.common.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class LogStatusConverter implements Converter<Integer> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    public WriteCellData<?> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String statusStr = (value != null && value == 0) ? "正常" : "异常";
        return new WriteCellData<>(statusStr);
    }
}
