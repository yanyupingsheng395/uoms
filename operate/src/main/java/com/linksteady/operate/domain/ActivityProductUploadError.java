package com.linksteady.operate.domain;

import lombok.Data;

/**
 * 商品上传错误
 * @author hxcao
 * @date 2020/3/28
 */
@Data
public class ActivityProductUploadError {

    /**
     * 问题描述
      */
    private String errorDesc;

    /**
     * 出现次数
     */
    private int errorRows;

    /**
     * 首次出现在多少行
     */
    private int firstErrorRow;

    /**
     * 错误是否可以忽略，默认false
     */
    private boolean ignore = false;

    public ActivityProductUploadError() {}
    public ActivityProductUploadError(String errorDesc, int errorRows, int firstErrorRow) {
        this.errorDesc = errorDesc;
        this.errorRows = errorRows;
        this.firstErrorRow = firstErrorRow;
    }

    public ActivityProductUploadError(String errorDesc) {
        this.errorDesc = errorDesc;
    }
}
