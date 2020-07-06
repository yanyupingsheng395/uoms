package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.service.UserTaskService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

/**
 * 导购任务
 * @author caohuixue
 */
@RestController
@RequestMapping("/userTask")
public class UserTaskController extends ApiBaseController{

    @Autowired
    private UserTaskService userTaskService;

    /**
     * 根据用户ID和商品ID获取相关的数据
     * @return
     */
    @RequestMapping("/getUserData")
    public ResponseBo getUserData(HttpServletRequest request,
                                  @RequestParam("signature")String signature,
                                  @RequestParam("timestamp")String timestamp,
                                  @RequestParam("userId")String userId,
                                  @RequestParam("productId")String productId) {
        String validate=validateLegality(request,signature,timestamp,userId,productId);
        if(StringUtils.isNotEmpty(validate))
        {
            return ResponseBo.error(validate);
        }else
        {
            return ResponseBo.okWithData(null, userTaskService.getUserData(userId, productId));
        }

    }

    /**
     * 获取推荐的商品数据
     * @param userId
     * @return
     */
    @RequestMapping("/getProductData")
    public ResponseBo getProductData(HttpServletRequest request,
                                     @RequestParam("signature")String signature,
                                     @RequestParam("timestamp")String timestamp,
                                     @RequestParam("userId")String userId) {
        String validate=validateLegality(request,signature,timestamp,userId);
        if(StringUtils.isNotEmpty(validate))
        {
            return ResponseBo.error(validate);
        }else
        {
            return ResponseBo.okWithData(null, userTaskService.getProductData(userId));
        }
    }

    /**
     * 获取用户购买历史数据
     * @param userId
     * @return
     */
    @RequestMapping("/getUserBuyHistory")
    public ResponseBo getUserBuyHistory(HttpServletRequest request,
                                        @RequestParam("signature")String signature,
                                        @RequestParam("timestamp")String timestamp,
                                        @RequestParam("userId")String userId) {

        String validate=validateLegality(request,signature,timestamp,userId);
        if(StringUtils.isNotEmpty(validate))
        {
            return ResponseBo.error(validate);
        }else
        {
            return ResponseBo.okWithData(null, userTaskService.getUserBuyHistory(userId));
        }

    }
}
