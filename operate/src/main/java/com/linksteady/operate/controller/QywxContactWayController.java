package com.linksteady.operate.controller;

import com.google.common.collect.Lists;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.QywxContactWay;
import com.linksteady.operate.service.QywxContactWayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 * 渠道活码
 * @author huang
 */
@Slf4j
@Controller
public class QywxContactWayController extends BaseController {

    @Autowired
    QywxContactWayService qywxContactWayService;

    private static List<QywxContactWay> dataList= Lists.newArrayList();

    static{
        dataList.add(new QywxContactWay(1l,"https://wwcdn.weixin.qq.com/node/wework/images/qrcode_group_new.1e6c2aa1dc.png","1","荷乳有赞","荷兰乳牛有赞客服专员","dwz.cn/mGph4YnP",0,"2020-05-17 11:28","荷乳有赞客服"));
        dataList.add(new QywxContactWay(2l,"https://wwcdn.weixin.qq.com/node/wework/images/qrcode_group_new.1e6c2aa1dc.png","2","荷乳天猫旗舰店","荷兰乳牛天猫客服01,荷兰乳牛天猫客服02,荷兰乳牛天猫客服03","dwz.cn/YZS0q5u7",0,"2020-05-17 11:25","荷乳天猫旗舰店客服"));
        dataList.add(new QywxContactWay(3l,"https://wwcdn.weixin.qq.com/node/wework/images/qrcode_group_new.1e6c2aa1dc.png","2","荷乳京东","荷兰乳牛京东客服01,荷兰乳牛京东客服01","dwz.cn/VVS0ouwT",0,"2020-05-17 11:23","荷乳京东客服"));
        dataList.add(new QywxContactWay(4l,"https://wwcdn.weixin.qq.com/node/wework/images/qrcode_group_new.1e6c2aa1dc.png","1","优莎蓓爱天猫","优莎蓓爱天猫客服专员","dwz.cn/USmuZF3q",0,"2020-05-16 14:06","优莎蓓爱天猫客服"));
        dataList.add(new QywxContactWay(5l,"https://wwcdn.weixin.qq.com/node/wework/images/qrcode_group_new.1e6c2aa1dc.png","1","优莎蓓爱京东食品","优莎蓓爱京东客服专员","dwz.cn/2mkn0e3X",0,"2020-05-16 14:08","优莎蓓爱京东食品客服"));

        dataList.add(new QywxContactWay(6l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(7l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(8l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(9l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(10l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(11l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(12l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(13l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(14l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(6l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(7l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(8l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(9l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(10l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(11l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(12l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(13l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(14l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(6l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(7l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(8l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(9l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(10l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(11l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(12l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(13l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(14l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(6l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(7l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(8l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(9l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(10l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(11l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(12l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(13l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(14l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(6l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(7l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(8l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(9l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(10l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(11l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(12l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(13l,"","","","","",0,"2020-04-13 11:23",""));
        dataList.add(new QywxContactWay(14l,"","","","","",0,"2020-04-13 11:23",""));

    }

    /**
     * 获取所有的活码列表
     *
     * @return
     */
    @GetMapping("/contactWay/getContactWayData")
    @ResponseBody
    public ResponseBo getHeadList() {
        List<QywxContactWay> list=qywxContactWayService.getContactWayList();
        return ResponseBo.okOverPaging("", 0,list);
    }


    /**
     * 获取列表
     *
     * @param request
     * @return
     */
    @GetMapping("/contactWay/getList")
    @ResponseBody
    public ResponseBo getHeadList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        String qstate = request.getParam().get("qstate");
        String qremark=request.getParam().get("qremark");
        int count=dataList.size();
        List<QywxContactWay> list=dataList.subList(0,5);
//        int count =qywxContactWayService.getContactWayCount(qstate,qremark);
//        List<QywxContactWay> list=qywxContactWayService.getContactWayList(limit,offset,qstate,qremark);
        return ResponseBo.okOverPaging("", count,list);
    }


    /**
     * 根据ID获取二维码的详细信息
     * @param contactWayId
     * @return
     */
    @RequestMapping("/contactWay/getContactWayById")
    @ResponseBody
    public ResponseBo getContactWayById(Long contactWayId) {
        QywxContactWay qywxContactWay=qywxContactWayService.getContactWayById(contactWayId);
        return ResponseBo.okWithData(null,qywxContactWay);
    }

    /**
     * 更新
     */
    @RequestMapping("/contactWay/update")
    @ResponseBody
    public ResponseBo update(QywxContactWay qywxContactWay) {
        if(qywxContactWay.getUsersList().indexOf(",")!=-1)
        {
            qywxContactWay.setContactType("2");
        }else
        {
            qywxContactWay.setContactType("1");
        }

        qywxContactWay.setUpdateDt(new Date());
        qywxContactWay.setUpdateBy(getCurrentUser().getUsername());
        //固定值为2 表示生成二维码
        qywxContactWay.setScene("2");
        //外部客户添加时是否无需验证，默认为true
        qywxContactWay.setSkipVerify(true);
        try {
            qywxContactWayService.updateContractWay(qywxContactWay);
            return ResponseBo.ok();
        } catch (Exception e) {
            return ResponseBo.error(e.getMessage());
        }

    }

    /**
     * 新增
     */
    @RequestMapping("/contactWay/save")
    @ResponseBody
    public ResponseBo save(QywxContactWay qywxContactWay) {
        //渠道码类型 单人 or 多人
        if(qywxContactWay.getUsersList().indexOf(",")!=-1)
        {
            qywxContactWay.setContactType("2");
        }else
        {
            qywxContactWay.setContactType("1");
        }
        //固定值为2 表示生成二维码
        qywxContactWay.setScene("2");
        //外部客户添加时是否无需验证，默认为true
        qywxContactWay.setSkipVerify(true);

        qywxContactWay.setCreateDt(new Date());
        qywxContactWay.setUpdateDt(new Date());
        qywxContactWay.setCreateBy(getCurrentUser().getUsername());
        qywxContactWay.setUpdateBy(getCurrentUser().getUsername());
        try {
            qywxContactWayService.saveContactWay(qywxContactWay);
            return ResponseBo.ok();
        } catch (Exception e){
            return ResponseBo.error(e.getMessage());
        }
    }


    /**
     * 更新渠道活码对应的短链接
     * @param contactWayId
     * @param shortUrl
     * @return
     */
    @RequestMapping("/contactWay/updateShortUrl")
    @ResponseBody
    public ResponseBo updateShortUrl(Long contactWayId,String shortUrl) {
        qywxContactWayService.updateShortUrl(contactWayId,shortUrl,getCurrentUser().getUsername());
        return ResponseBo.ok();
    }

    /**
     * 删除功能
     * @return
     */
    @RequestMapping("/contactWay/delete")
    @ResponseBody
    public ResponseBo deleteContactWay(String configId) {
        try {
            qywxContactWayService.deleteContactWay(configId);
            return ResponseBo.ok();
        } catch (Exception e) {
            return ResponseBo.error(e.getMessage());
        }

    }


    @RequestMapping("/images/qrcode/{configId}")
    public String mappingToQrCode(Model model, @PathVariable(name="configId") String configId)
    {
        //根据configId查找对应的二维码地址
        String qrCode=qywxContactWayService.getQrcodeByConfigId(configId);
        if(StringUtils.isNotEmpty(qrCode))
        {
            model.addAttribute("qrCode",qrCode);
        }else
        {
            model.addAttribute("qrCode","/images/qrcode_error.png");
        }

        return "operate/contactWay/qrCode";
    }

}
