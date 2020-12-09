package com.linksteady.operate.controller;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.FileUtils;
import com.linksteady.operate.domain.QywxManualError;
import com.linksteady.operate.domain.QywxManualHeader;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.QywxManualPushService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
 * 企业微信手动推送 controller
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
                                                  @RequestParam("mediaId")  String mediaId) throws Exception {
        if(FileUtils.multipartFileToFile(file)==null){
            return ResponseBo.error("上传文件,请重新上传数据！");
        }
        if(StringUtils.isEmpty(smsContent)){
            return ResponseBo.error("推送内容不能为空,请重新上传数据！");
        }
        if(StringUtils.isEmpty(mediaId)){
            return ResponseBo.error("小程序封面ID不能为空,请重新上传数据！");
        }
        if(StringUtils.isEmpty(mpTitle)){
            return ResponseBo.error("小程序标题不能为空,请重新上传数据！");
        }
        if(StringUtils.isEmpty(mpUrl)){
            return ResponseBo.error("小程序链接不能为空,请重新上传数据！");
        }
        QywxManualError error =null;
        try {
             error = qywxManualPushService.saveManualData(smsContent, file, mpTitle, mpUrl, mediaId);
        } catch (IOException e) {
            e.printStackTrace();
            ResponseBo.error("文件解析异常！");
        } catch (LinkSteadyException e) {
            return  ResponseBo.error(e.getMessage());
        }
        return ResponseBo.okWithData(error.getErrorDesc(),error);
    }

    /**
     * 推送企业微信消息，防重复推送。锁+状态
     * @return
     */
    @PostMapping("/pushMessage")
    public synchronized ResponseBo pushMessage(@RequestParam("headId") Long headId) {
        QywxManualHeader header=qywxManualPushService.getManualHeader(headId);
        String content = header.getTextContent();
        String mediald = header.getMpMediald();
        String title = header.getMpTitle();
        String url = header.getMpUrl();
        if(StringUtils.isEmpty(content)){
            return ResponseBo.error("推送内容不能为空,请重新上传数据推送！");
        }
        if(StringUtils.isEmpty(mediald)){
            return ResponseBo.error("小程序封面ID不能为空,请重新上传数据推送！");
        }
        if(StringUtils.isEmpty(title)){
            return ResponseBo.error("小程序标题不能为空,请重新上传数据推送！");
        }
        if(StringUtils.isEmpty(url)){
            return ResponseBo.error("小程序链接不能为空,请重新上传数据推送！");
        }

        String status = qywxManualPushService.getHeadStatus(headId);
        // 判读当前状态为已上传，待推送，则可以继续执行
        if(status.equalsIgnoreCase("0")) {
            try {
                qywxManualPushService.pushMessage(header);
                return ResponseBo.ok();
            } catch (Exception e) {
                log.error("企业微信手动推送信息失败【{}】",e);
                return ResponseBo.error("推送信息失败！");
            }
        }else {
            return ResponseBo.error("当前状态不允许进行推送操作！");
        }
    }

    /**
     * 删除数据
     * @param headId
     * @return
     */
    @GetMapping("/deleteData")
    public ResponseBo deleteData(@RequestParam("headId") Long headId) {
        //校验状态是否未执行
        String status = qywxManualPushService.getHeadStatus(headId);
        if(!"0".equals(status)){
            return ResponseBo.error("该记录的当前状态不支持删除操作！");
        }
        qywxManualPushService.deleteData(headId);
        return ResponseBo.ok();
    }

    /**
     * 下载模板
     * @param response
     * @throws IOException
     */
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
