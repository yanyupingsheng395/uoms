package com.linksteady.qywx.controller;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 导购任务
 * @author caohuixue
 */
@RestController
@RequestMapping("/userTask")
public class UserTaskController {

    @Autowired
    private UserTaskService userTaskService;

    /**
     * 根据用户ID和商品ID获取相关的数据
     * @return
     */
    @GetMapping("/getUserData")
    public ResponseBo getUserData(String userId, String productId) {
        return ResponseBo.okWithData(null, userTaskService.getUserData(userId, productId));
    }

    /**
     * 获取推荐的商品数据
     * @param userId
     * @return
     */
    @GetMapping("/getProductData")
    public ResponseBo getProductData(String userId) {
        return ResponseBo.okWithData(null, userTaskService.getProductData(userId));
    }

    @GetMapping("/getUserBuyHistory")
    public ResponseBo getUserBuyHistory(String userId) {
        return ResponseBo.okWithData(null, userTaskService.getUserBuyHistory(userId));
    }
}
