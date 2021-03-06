package com.linksteady.qywx.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.Base64Img;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.qywx.constant.FilePathConsts;
import com.linksteady.qywx.domain.FollowUser;
import com.linksteady.qywx.domain.QywxContactWay;
import com.linksteady.qywx.service.FollowUserService;
import com.linksteady.qywx.service.QywxBaseDataService;
import com.linksteady.qywx.service.QywxContactWayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 渠道活码
 *
 * @author huang
 */
@Slf4j
@Controller
public class QywxContactWayController extends BaseController {

    @Autowired
    QywxContactWayService qywxContactWayService;

    @Autowired
    QywxBaseDataService baseDataService;

    @Autowired
    FollowUserService followUserService;

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
        AtomicReference<String> qstate = new AtomicReference<>("");
        Optional.ofNullable(request.getParam()).ifPresent(x -> qstate.set(x.get("qstate")));
        int count = qywxContactWayService.getContactWayCount(qstate.get());
        List<QywxContactWay> list = qywxContactWayService.getContactWayList(limit, offset, qstate.get());
        return ResponseBo.okOverPaging("", count, list);
    }

    /**
     * 获取列表(已配置了url)
     *
     * @param request
     * @return
     */
    @GetMapping("/contactWay/getValidUrlList")
    @ResponseBody
    public ResponseBo getValidUrlList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        int count = qywxContactWayService.getContactWayValidUrlCount();
        List<QywxContactWay> list = qywxContactWayService.getContactWayValidUrlList(limit, offset);
        return ResponseBo.okOverPaging("", count, list);
    }


    /**
     * 根据ID获取二维码的详细信息
     *
     * @param contactWayId
     * @return
     */
    @RequestMapping("/contactWay/getContactWayById")
    @ResponseBody
    public ResponseBo getContactWayById(Long contactWayId) {
        QywxContactWay qywxContactWay = qywxContactWayService.getContactWayById(contactWayId);
        return ResponseBo.okWithData(null, qywxContactWay);
    }

    /**
     * 更新
     */
    @RequestMapping("/contactWay/update")
    @ResponseBody
    public ResponseBo update(QywxContactWay qywxContactWay) {
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
            String message = e.getMessage();
            if (message.indexOf("uo_qywx_contact_way_u1") != -1) {
                return ResponseBo.error("更新失败，当前渠道已经存在！");
            } else {
                log.error("更新渠道活码失败，{}", e);
                return ResponseBo.error("更新渠道活码失败！");
            }
        }

    }

    /**
     * 新增
     */
    @RequestMapping("/contactWay/save")
    @ResponseBody
    public ResponseBo save(QywxContactWay qywxContactWay) {
        String userName = getCurrentUser().getUsername();
        try {
            qywxContactWayService.saveContactWay(qywxContactWay, userName);
            return ResponseBo.ok();
        } catch (Exception e) {
            String message = e.getMessage();
            if (message.indexOf("uo_qywx_contact_way_u1") != -1) {
                return ResponseBo.error("保存失败，当前渠道已经存在！");
            } else {
                log.error("保存渠道活码失败，{}", e);
                return ResponseBo.error("保存渠道活码失败！");
            }
        }
    }


    /**
     * 更新渠道活码对应的短链接
     *
     * @param contactWayId
     * @param shortUrl
     * @return
     */
    @RequestMapping("/contactWay/updateShortUrl")
    @ResponseBody
    public ResponseBo updateShortUrl(Long contactWayId, String shortUrl) {
        qywxContactWayService.updateShortUrl(contactWayId, shortUrl, getCurrentUser().getUsername());
        return ResponseBo.ok();
    }

    /**
     * 删除功能
     *
     * @return
     */
    @RequestMapping("/contactWay/delete")
    @ResponseBody
    public ResponseBo deleteContactWay(String configId,Long contactWayId) {
        try {
            qywxContactWayService.deleteContactWay(configId,contactWayId);
            return ResponseBo.ok("删除成功！");
        } catch (Exception e) {
            log.debug("删除渠道活码失败，{}", e);
            return ResponseBo.error(e.getMessage());
        }
    }

    @RequestMapping("/images/qrcode/{configId}")
    public String mappingToQrCode(Model model, @PathVariable(name = "configId") String configId) {
        //根据configId查找对应的二维码地址
        QywxContactWay qywxContactWay = qywxContactWayService.getQrcodeByConfigId(configId);
        if (StringUtils.isNotEmpty(qywxContactWay.getQrCode())) {
            model.addAttribute("qrCode", qywxContactWay.getQrCode());
        } else {
            model.addAttribute("qrCode", "/images/qrcode_error.png");
        }

        return "operate/contactWay/qrCode";
    }

    /**
     * 二维码下载
     *
     * @param configId
     * @return
     */
    @RequestMapping("/contactWay/download")
    public void contactWayDownLoad(String configId, HttpServletResponse response) {
        //根据configId查找对应的二维码地址
        QywxContactWay qywxContactWay = qywxContactWayService.getQrcodeByConfigId(configId);

        InputStream is = OkHttpUtil.downLoadFile(qywxContactWay.getQrCode());

        String fileName = qywxContactWay.getState() + ".png";
        try {
            // 设置下载的响应头信息
            response.setHeader("Content-Disposition",
                    "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
            OutputStream os = response.getOutputStream();
            byte[] buffer = new byte[1024]; // 图片文件流缓存池
            int index;
            while ((index = is.read(buffer)) != -1) {
                os.write(buffer, 0, index);
                os.flush();
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取组织架构数据树
     * @return
     */
    @RequestMapping("/contactWay/getDept")
    @ResponseBody
    public ResponseBo getDept(){
        List<Map<String, Object>> dept =null;
        try {
            dept = baseDataService.getDept();
        }catch (Exception e){
            return ResponseBo.error("未获取到部门！");
        }
        return ResponseBo.okWithData(null, dept);
    }

    /**
     * 获取组织架构数据树
     * @return
     */
    @RequestMapping("/contactWay/getFollowUserList")
    @ResponseBody
    public ResponseBo getUser(){
        List<FollowUser> userlist =null;
        try {
            userlist = followUserService.getFollowUserList();
        }catch (Exception e){
            return ResponseBo.error("未获取到成员！");
        }
        return ResponseBo.okWithData(null, userlist);
    }
}
