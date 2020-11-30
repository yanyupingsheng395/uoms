package com.linksteady.qywx.vo;

import com.linksteady.common.domain.enums.ConfigEnum;
import com.linksteady.common.service.ConfigService;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * 每日运营渠道信息配置的VO
 */
@Data
public class SmsConfigVO {


    /**
     * 渠道名称长度
     */
    private Integer sourceNameLen;

    /**
     * 商品名称长度
     */
    private Integer prodNameLen;

    /**
     * 签名信息
     * @param sourceName
     * @return
     */
    private String signature;

    /**
     * 退订信息
     * @param sourceName
     * @return
     */
    private String unsubscribe;

    /**
     * 签名信息长度
     * @param sourceName
     * @return
     */
    private Integer signatureLen;

    /**
     * 退订信息长度
     * @param sourceName
     * @return
     */
    private Integer unsubscribeLen;

    private static SmsConfigVO instance=new SmsConfigVO();

    private SmsConfigVO()
    {

    }

    public static SmsConfigVO getInstance(ConfigService configService)
    {
        //重新加载属性
        instance.setSourceNameLen(Integer.parseInt(configService.getValueByName(ConfigEnum.sourceNameLen.getKeyCode())));
        instance.setProdNameLen(Integer.parseInt(configService.getValueByName(ConfigEnum.prodNameLen.getKeyCode())));
        String signature=configService.getValueByName(ConfigEnum.signature.getKeyCode());
        String unsubscribe=configService.getValueByName(ConfigEnum.unsubscribe.getKeyCode());
        instance.setSignature(signature);
        instance.setUnsubscribe(unsubscribe);
        instance.setSignatureLen(StringUtils.isEmpty(signature)?0:signature.length());
        instance.setUnsubscribeLen(StringUtils.isEmpty(unsubscribe)?0:unsubscribe.length());
        return instance;
    }


}
