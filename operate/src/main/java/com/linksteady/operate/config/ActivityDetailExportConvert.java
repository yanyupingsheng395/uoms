package com.linksteady.operate.config;

import com.linksteady.common.util.poi.convert.ExportConvert;

/**
 * @author hxcao
 * @date 2019-11-13
 */
public class ActivityDetailExportConvert implements ExportConvert {

    @Override
    public String handler(Object val) {
        return val.toString();
    }
}
