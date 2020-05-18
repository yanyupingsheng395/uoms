package com.linksteady.wxofficial.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hxcao
 * @date 2020/4/20
 */
@Data
public class WxTagVo implements Serializable {
    private static final long serialVersionUID = -7722428695667031252L;
    private Long id;
    private String name;
    private Integer count;
}
