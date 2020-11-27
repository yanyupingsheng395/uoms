package com.linksteady.qywx.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.qywx.domain.FriendsGuide;
import com.linksteady.qywx.domain.QywxUser;
import com.linksteady.qywx.service.QywxService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class FriendCirlceGuideController extends BaseController {


    @Autowired
    QywxService qywxService;
    /**
     * 获取当前用户的朋友圈方案
     */
    @RequestMapping("/friend/guideList")
    @ResponseBody
    public Map<String, Object> guideList(HttpServletRequest httpServletRequest, QueryRequest request) {
        //获取当前用户及公司
        //获取当前页数、每页数量

        List<FriendsGuide> list= Lists.newArrayList();

        list.add(new FriendsGuide("20200911","20200911-01","阿瓦提长绒棉浴巾,纯棉超柔婴儿浴巾","浴巾",""));
        list.add(new FriendsGuide("20200911","20200911-02","华夫格多功能毯","休闲毯",""));
        list.add(new FriendsGuide("20200911","20200911-03","老粗布条格凉席三件套,天然蔺草席三件套","凉席",""));
        list.add(new FriendsGuide("20200912","20200912-01","30支精梳埃及浴巾,SPIMA棉条纹浴巾","浴巾",""));
        list.add(new FriendsGuide("20200912","20200912-02","三层纱蓬松褶皱毛巾被","休闲毯",""));
        return super.selectByPageNumSize(request, () -> list);
    }

    /**
     * 生成朋友圈方案
     */
    @RequestMapping("/friend/generatePolicy")
    @ResponseBody
    public ResponseBo guideList(@RequestParam(name = "pushInterval") int pushInterval,
                                @RequestParam(name = "pushNum") int pushNum,
                                @RequestParam(name = "productNum") int productNum,
                                @RequestParam(name = "periodNum") int periodNum,
                                @RequestParam(name = "pushStartDt") String pushStartDt,
                                HttpServletRequest httpServletRequest
                                ) {
        QywxUser user=(QywxUser)httpServletRequest.getSession().getAttribute("user");
         if(pushInterval>3||pushInterval<1)
         {
             return ResponseBo.error("商品朋友圈推送频率只能介于1-3之间!");
         }

        if(productNum!=1)
        {
            return ResponseBo.error("每条朋友圈体现商品数只能为1!");
        }


        if(pushNum>15||pushNum<1)
        {
            return ResponseBo.error("每天可发送商品朋友圈数只能介于1-15!");
        }

        if(periodNum>3||periodNum<1)
        {
            return ResponseBo.error("生成方案的周期数只能介于1-3之间!");
        }

        LocalDate pushStartDate=null;
        try {
            //判断日期参数
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            pushStartDate = LocalDate.parse(pushStartDt, formatter);
        } catch (Exception e) {
            return ResponseBo.error("商品朋友圈开始推送时间格式不正确!");
        }

        if(pushStartDate.isAfter(LocalDate.now()))
        {
            return ResponseBo.error("商品朋友圈开始推送时间不能早于当前日期!");
        }

        log.info("朋友圈发圈接收到的参数为{}-{}-{}-{}-{}",pushInterval,pushNum,productNum,periodNum,pushStartDt);
        //发送到用户成长系统
        //推送到用户成长端
        String timestamp=String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
        String corpId=qywxService.getCorpId();

        Map<String,String> param= Maps.newHashMap();
        param.put("timestamp", timestamp);
        param.put("corpId",corpId);
        param.put("pushInterval",String.valueOf(pushInterval));
        param.put("pushNum",String.valueOf(pushNum));
        param.put("productNum",String.valueOf(productNum));
        param.put("periodNum",String.valueOf(periodNum));
        param.put("pushStartDt",pushStartDt);
        param.put("followUserId",user.getUserId());
        param.put("signature", SHA1.gen(timestamp,corpId,
                String.valueOf(pushInterval),
                String.valueOf(pushNum),
                String.valueOf(productNum),
                String.valueOf(periodNum),
                pushStartDt,
                user.getUserId()
                ));

        //推送到用户成长端
        String result="";
       // String result= OkHttpUtil.postRequestByFormBody(qywxService.getOperateUrl(corpId)+ OperateConsts.GENERATE_FRIEND_POLICY,param);
        JSONObject jsonObject = new JSONObject();
        if(StringUtils.isNotEmpty(result)){
            jsonObject = JSON.parseObject(result);
        }
        if(null==jsonObject||200!=jsonObject.getIntValue("code"))
        {
            return ResponseBo.ok();
        }else
        {
            return ResponseBo.error(jsonObject.getString("msg"));
        }

    }
}
