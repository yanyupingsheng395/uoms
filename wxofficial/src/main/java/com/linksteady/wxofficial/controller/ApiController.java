package com.linksteady.wxofficial.controller;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.wxofficial.common.wechat.service.OperateService;
import com.linksteady.wxofficial.config.WxProperties;
import com.linksteady.wxofficial.service.WxOfficialUserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.open.api.WxOpenFastMaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author hxcao
 * @date 2020/4/22
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {

    @Autowired
    private OperateService operateService;

    @Autowired
    private WxOfficialUserService wxOfficialUserService;

    @Autowired
    private WxProperties wxProperties;

    /**
     * 事件回调接口，用于处理微信事件
     * @return
     */
    @RequestMapping("/eventReceive")
    public ResponseBo receive(@RequestBody String data) {
        log.info("获取到事件的回调：" + data);
        return ResponseBo.ok();
    }

    /**
     * 用户同步的数据接口
     * @param data
     * @return
     */
    @RequestMapping("/syncUser")
    public ResponseBo syncUser(@RequestBody String data) {
        log.info("接收到返回的同步用户的数据：" + data);
        try {
            wxOfficialUserService.setSyncUserList(data);
        } catch (Exception e) {
            log.error("同步用户数据发生异常", e);
            return ResponseBo.error();
        }
        return ResponseBo.ok();
    }

    /**
     * 发起同步用户的请求
     * @return
     */
    @GetMapping("/callSyncUser")
    public ResponseBo callSyncUser() {
        String url = wxProperties.getServiceDomain() + wxProperties.getSyncUserListUrl();
        log.info("请求同步用户数据");
        operateService.getDataList(url);
        return ResponseBo.ok();
    }
}
