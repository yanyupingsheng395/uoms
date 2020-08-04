package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.OkHttpUtil;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
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
        int count =qywxContactWayService.getContactWayCount(qstate);
        List<QywxContactWay> list=qywxContactWayService.getContactWayList(limit,offset,qstate);
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
            String message=e.getMessage();
            if(message.indexOf("uo_qywx_contact_way_u1")!=-1)
            {
                return ResponseBo.error("新增失败，当前渠道已经存在！");
            }else
            {
                log.error("新增渠道活码失败，{}",e);
                return ResponseBo.error("新增渠道活码失败！");
            }
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
            String message=e.getMessage();
            if(message.indexOf("uo_qywx_contact_way_u1")!=-1)
            {
                return ResponseBo.error("保存失败，当前渠道已经存在！");
            }else
            {
                log.error("保存渠道活码失败，{}",e);
                return ResponseBo.error("保存渠道活码失败！");
            }

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
            return ResponseBo.ok("删除成功！");
        } catch (Exception e) {
            log.error("删除渠道活码失败，{}",e);
            return ResponseBo.error("删除失败！");
        }
    }

    @RequestMapping("/images/qrcode/{configId}")
    public String mappingToQrCode(Model model, @PathVariable(name="configId") String configId)
    {
        //根据configId查找对应的二维码地址
        QywxContactWay qywxContactWay=qywxContactWayService.getQrcodeByConfigId(configId);
        if(StringUtils.isNotEmpty(qywxContactWay.getQrCode()))
        {
            model.addAttribute("qrCode",qywxContactWay.getQrCode());
        }else
        {
            model.addAttribute("qrCode","/images/qrcode_error.png");
        }

        return "operate/contactWay/qrCode";
    }

    /**
     * 二维码下载
     * @param configId
     * @return
     */
    @RequestMapping("/contactWay/download")
    public void contactWayDownLoad(String configId, HttpServletResponse response)
    {
        //根据configId查找对应的二维码地址
        QywxContactWay qywxContactWay=qywxContactWayService.getQrcodeByConfigId(configId);

        InputStream is=OkHttpUtil.downLoadFile(qywxContactWay.getQrCode());

        String fileName=qywxContactWay.getState()+".png";
        try {
            // 设置下载的响应头信息
            response.setHeader("Content-Disposition",
                    "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
            OutputStream os = response.getOutputStream();
            byte [] buffer = new byte[1024]; // 图片文件流缓存池
            int index;
            while((index = is.read(buffer)) != -1){
                os.write(buffer,0,index);
                os.flush();
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
