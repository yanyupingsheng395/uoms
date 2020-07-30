package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.service.UserTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 导购任务
 * @author caohuixue
 */
@Slf4j
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
        try {
            validateLegality(request,signature,timestamp,userId,productId);
        } catch (Exception e) {
            return ResponseBo.error(e.getMessage());
        }
        return ResponseBo.okWithData(null, userTaskService.getUserData(userId, productId));
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
        try {
            validateLegality(request,signature,timestamp,userId);
        } catch (Exception e) {
            return ResponseBo.error(e.getMessage());
        }
        return ResponseBo.okWithData(null, userTaskService.getProductData(userId));
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
        try {
            validateLegality(request,signature,timestamp,userId);
        } catch (Exception e) {
            return ResponseBo.error(e.getMessage());
        }
        return ResponseBo.okWithData(null, userTaskService.getUserBuyHistory(userId));
    }
}
