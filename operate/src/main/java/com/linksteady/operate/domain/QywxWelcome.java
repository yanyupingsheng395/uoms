package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author hxcao
 * @date 2020/9/3
 */
@Data
public class QywxWelcome {

    private Integer id;

    private String welcomeName;

    private String content;

    private String insertBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date insertDt;

    private String policyType;

    private String policyTypeTmp;

    private String picUrl;

    private String picId;

    private String linkTitle;

    private String linkUrl;

    private String linkDesc;

    private String linkPicurl;

    private String miniprogramTitle;

    private String miniprogramPage;

    private String qywxCouponId;

    private String qywxProductId;

    private String validFlag;

    private String status;
}
