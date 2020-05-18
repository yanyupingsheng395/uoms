package com.linksteady.wxofficial.entity.bo;

import com.linksteady.common.domain.ResponseBo;
import lombok.Data;

/**
 * @author hxcao
 * @date 2020/4/29
 */
@Data
public class ApiResponseBo<T> {

    private String code;

    private T msg;

    private String data;

    private ResponseBo responseBo;

    public ResponseBo getResponseBo() {
        ResponseBo responseBo = new ResponseBo();
        responseBo.put("code", this.code);
        responseBo.put("msg", this.msg);
        responseBo.put("data", this.data);
        return responseBo;
    }
}
