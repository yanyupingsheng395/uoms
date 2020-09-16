package com.linksteady.qywx.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.domain.SpuInfo;
import com.linksteady.qywx.domain.UserBuyHistory;
import com.linksteady.qywx.service.UserTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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
                                        @RequestParam("userId")String userId,
                                        @RequestParam("spuId")Long spuId) {
        try {
            validateLegality(request,signature,timestamp,userId,String.valueOf(spuId));
            Map<String,Object> result= Maps.newHashMap();
            //整体
            if(null==spuId||spuId==-1l)
            {
                //用户在类目上的价值敏感度
                result.put("userValue","");
                //用户在类目上的生命周期阶段
                result.put("lifeCycle","");
            }else
            {
                //用户在类目上的价值敏感度
                result.put("userValue","高价值低敏感");
                //用户在类目上的生命周期阶段
                result.put("lifeCycle","成长期");
            }

            //用户涉及的类目
            List<SpuInfo> spuList=userTaskService.getSpuList(userId);

            //用户的购买统计数据
            Map<String,String> userStats=userTaskService.getUserStatis(userId);

            //用户购买历史
            List<UserBuyHistory> userBuyHistoryList=userTaskService.getUserBuyHistory(userId);

            //构造返回数据
            result.put("spuList",spuList);
            result.put("userStats",userStats);
            result.put("userBuyHistoryList",userBuyHistoryList);
            return ResponseBo.okWithData(null, result);
        } catch (Exception e) {
            return ResponseBo.error(e.getMessage());
        }
    }
}
