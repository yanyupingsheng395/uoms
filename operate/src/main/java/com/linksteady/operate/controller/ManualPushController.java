package com.linksteady.operate.controller;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.ManualHeader;
import com.linksteady.operate.service.ManualPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 短信手动推送 controller
 *
 * @author hxcao
 * @date 2019/12/25
 */
@Slf4j
@RestController
@RequestMapping("/manual")
public class ManualPushController {

    @Autowired
    private ManualPushService manualPushService;

    /**
     * 获取分页数据
     *
     * @param request
     * @return
     */
    @GetMapping("/getHeaderListPage")
    public ResponseBo getHeaderListPage(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String scheduleDate = request.getParam().get("scheduleDate");
        int count = manualPushService.getHeaderListCount(scheduleDate);
        List<ManualHeader> dataList = manualPushService.getHeaderListData(start, end, scheduleDate);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 保存头表和行表数据
     * @return
     */
    @PostMapping("/saveManualData")
    public synchronized ResponseBo saveManualData(@RequestParam("smsContent") String smsContent, @RequestParam("file") MultipartFile file, @RequestParam("sendType")  String sendType, String pushDate) throws IOException {
        manualPushService.saveManualData(smsContent, file, sendType, pushDate);
        return ResponseBo.ok();
    }

    /**
     * 同步推送短信内容，防重复推送。锁+状态
     * @return
     */
    @PostMapping("/pushMessage")
    public synchronized ResponseBo pushMessage(@RequestParam("headId") String headId, @RequestParam("pushType") String pushType) {
        String status = manualPushService.getHeadStatus(headId);
        // 判读当前状态为已上传，待推送，则可以继续执行
        if(status.equalsIgnoreCase("0")) {
            manualPushService.pushMessage(headId, pushType);
        }else {
            return ResponseBo.error("记录已被另一用户操作，请重新进行！");
        }
        return ResponseBo.ok();
    }

    @GetMapping("/getPushInfo")
    public ResponseBo getPushInfo(@RequestParam("headId") String headId) {
        return ResponseBo.okWithData(null, manualPushService.getPushInfo(headId));
    }

    @GetMapping("/deleteData")
    public ResponseBo deleteData(@RequestParam("headId") String headId) {
        manualPushService.deleteData(headId);
        return ResponseBo.ok();
    }

    @RequestMapping("/download")
    public void fileDownload(HttpServletResponse response) throws IOException {
        String fileName = "manual_template.txt";
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
