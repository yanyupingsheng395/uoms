package com.linksteady.wxofficial.controller;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.wxofficial.common.wechat.service.OperateService;
import com.linksteady.wxofficial.config.WxProperties;
import com.linksteady.wxofficial.entity.bo.ApiResponseBo;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.mp.bean.menu.WxMpMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2020/4/28
 */
@RestController
@RequestMapping("/wxMenu")
public class WxMenuController {

    @Autowired
    private OperateService operateService;

    @Autowired
    private WxProperties wxProperties;

    @RequestMapping("/getMenuList")
    public ResponseBo getMenuList() {
        String url = wxProperties.getServiceDomain() + wxProperties.getMenuListUrl();
        ApiResponseBo<WxMpMenu> apiResponseBo = JSONObject.parseObject(operateService.getDataList(url)).toJavaObject(ApiResponseBo.class);
        return apiResponseBo.getResponseBo();
    }

    @RequestMapping("/addMenu")
    public ResponseBo addMenu(String data) throws UnsupportedEncodingException {
        AtomicInteger key = new AtomicInteger(0);
        data = URLDecoder.decode(data,"UTF-8");
        WxMenu wxMenu = WxMenu.fromJson(data);
        List<WxMenuButton> wxMenuButtons = setKey(key, wxMenu.getButtons());
        wxMenu.setButtons(wxMenuButtons);
        String url = wxProperties.getServiceDomain() + wxProperties.getMenuAddUrl();
        Map<String, Object> param = Maps.newHashMap();
        param.put("wxMenu", wxMenu);
        ApiResponseBo apiResponseBo = JSONObject.parseObject(operateService.saveData(url, param)).toJavaObject(ApiResponseBo.class);
        return apiResponseBo.getResponseBo();
    }

    /**
     * 为click类型的菜单添加key值。
     * @param key
     * @param wxMenuButtons
     * @return
     */
    private List<WxMenuButton> setKey(AtomicInteger key, List<WxMenuButton> wxMenuButtons) {
        return wxMenuButtons.stream().peek(x -> {
            if(x.getType().equalsIgnoreCase("click")) {
                x.setKey(String.valueOf(key.getAndAdd(1)));
                List<WxMenuButton> subButtons = x.getSubButtons();
                setKey(key, subButtons);
                x.setSubButtons(subButtons);
            }
        }).collect(Collectors.toList());
    }
}
