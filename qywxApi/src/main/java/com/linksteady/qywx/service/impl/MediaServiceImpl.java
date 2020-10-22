package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.service.MediaService;
import org.springframework.stereotype.Service;

@Service
public class MediaServiceImpl implements MediaService {

    /**
     * identityType 可选的值有 PRODUCT表示商品 COUPON表示优惠券
     * @param identityType
     * @param identityId
     * @return
     */
    @Override
    public String getMpMediaId(String identityType,Long identityId) {

        //1. 根据identityType和identityId到 uo_qywx_media_img找记录
        // 1.1如果能找到，判断media_expire_date是否失效，如果未失效，则直接返回找到的mediaId 如果失效，则调用生成的逻辑(内容来自于当前表)
        //

        //1.2 如果找不到，则根据identityType和identityId=-1的记录，继续判断是否失效，未失效，直接返回，失效，则调用生成逻辑(内容来自于当前表)

        //1.2继续找不到，调用生成逻辑(内容来于查找图片逻辑)



        //生成逻辑(同步代码块)：拿到图片内容，调用企业微信媒体素材接口，接口会返回mediaId和expire_date, 拿到以后更新uo_qywx_media_img


        //查找图片？ 商品根据商品ID到w_product_media中取 获取不到则取uo_qywx_param中的media_content
        // 优惠券则直接取 uo_qywx_param中的coupon_media_content



        return null;
    }
}
