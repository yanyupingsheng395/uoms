package com.linksteady.operate.controller;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.ManualHeader;
import com.linksteady.operate.domain.QywxManualError;
import com.linksteady.operate.domain.QywxManualHeader;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.ManualPushService;
import com.linksteady.operate.service.QywxManualPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 短信手动推送 controller
 *
 * @author hxcao
 * @date 2019/12/25
 */
@Slf4j
@RestController
@RequestMapping("/qywxmanual")
public class QywxManualPushController {

    @Autowired
    private QywxManualPushService qywxManualPushService;

    /**
     * 获取分页数据
     *
     * @param request
     * @return
     */
    @GetMapping("/getHeaderListPage")
    public ResponseBo getHeaderListPage(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        int count = qywxManualPushService.getHeaderListCount();
        List<QywxManualHeader> dataList = qywxManualPushService.getHeaderListData(limit,offset);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 保存头表和行表数据
     * @return
     */
    @PostMapping("/saveManualData")
    public synchronized ResponseBo saveManualData(@RequestParam("smsContent") String smsContent,
                                                  @RequestParam("file") MultipartFile file,
                                                  @RequestParam("mpTitle")  String mpTitle,
                                                  @RequestParam("mpUrl")  String mpUrl,
                                                  @RequestParam("mediaId")  String mediaId) {
        QywxManualError error =null;
        try {
             error = qywxManualPushService.saveManualData(smsContent, file, mpTitle, mpUrl, mediaId);
        } catch (IOException e) {
            e.printStackTrace();
            ResponseBo.error("文件解析异常！");
        } catch (LinkSteadyException e) {
            ResponseBo.error(e.getMessage());
        }
        return ResponseBo.okWithData(error.getErrorDesc(),error);
    }

    /**
     * 同步推送短信内容，防重复推送。锁+状态
     * @return
     */
    @PostMapping("/pushMessage")
    public synchronized ResponseBo pushMessage(@RequestParam("headId") Long headId) {
        String status = qywxManualPushService.getHeadStatus(headId);
        // 判读当前状态为已上传，待推送，则可以继续执行
        if(status.equalsIgnoreCase("0")) {
            try {
                qywxManualPushService.pushMessage(headId);
                return ResponseBo.ok();
            } catch (Exception e) {
                log.error("企业微信手动推送信息失败【{}】",e);
                return ResponseBo.error("推送信息失败！");
            }
        }else {
            return ResponseBo.error("当前状态不允许进行推送操作！");
        }
    }

    @GetMapping("/deleteData")
    public ResponseBo deleteData(@RequestParam("headId") Long headId) {
        qywxManualPushService.deleteData(headId);
        return ResponseBo.ok();
    }

    @RequestMapping("/download")
    public void fileDownload(HttpServletResponse response) throws IOException {
        String fileName = "manual_template_s.csv";
        String realFileName = System.currentTimeMillis() + "_" + fileName.substring(fileName.indexOf('_') + 1);
        ClassPathResource classPathResource = new ClassPathResource("excel/" + fileName);
        InputStream in = classPathResource.getInputStream();
        response.setHeader("Content-Disposition", "inline;fileName=" + java.net.URLEncoder.encode(realFileName, "utf-8"));
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        try (InputStream inputStream = in; OutputStream os = response.getOutputStream()) {
            byte[] b = new byte[2048];
            int length;
            while ((length = inputStream.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } catch (Exception e) {
            log.error("文件下载失败", e);
        }
    }
}
