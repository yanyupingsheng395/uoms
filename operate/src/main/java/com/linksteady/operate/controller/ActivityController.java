package com.linksteady.operate.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.ActivityConfig;
import com.linksteady.operate.domain.ActivityHead;
import com.linksteady.operate.domain.ActivityProduct;
import com.linksteady.operate.domain.ActivityUser;
import com.linksteady.operate.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.ReadPendingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-13
 */
@Slf4j
@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private ActivityHeadService activityHeadService;

    @Autowired
    private ActivityDetailService activityDetailService;

    @Autowired
    private ActivityWeightService activityWeightService;

    @Autowired
    private ActivityConfigService activityConfigService;

    @Autowired
    private ActivityUserService activityUserService;

    @Autowired
    private ActivityProductService activityProductService;

    /**
     * 获取头表的分页数据
     */
    @GetMapping("/gePageOfHead")
    public ResponseBo gePageOfHead(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String name = request.getParam().get("name");

        List<ActivityHead> dataList = activityHeadService.getDataListOfPage(start, end, name);
        int count = activityHeadService.getDataCount(name);

        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 用户成长数量分布图
     *
     * @param endDt
     * @return
     */
    @GetMapping("/getUserDataCount")
    public ResponseBo getUserDataCount(String startDt, String endDt, String dateRange) {
        return ResponseBo.okWithData(null, activityDetailService.getUserCountData(startDt, endDt, dateRange));
    }

    /**
     * 活动权重指数分布图
     *
     * @param startDt
     * @param endDt
     * @return
     */
    @GetMapping("/getWeightIdx")
    public ResponseBo getWeightIdx(String startDt, String endDt, String dateRange) {
        return ResponseBo.okWithData(null, activityWeightService.getWeightIdx(startDt, endDt, dateRange));
    }

    @GetMapping("/updateData")
    public ResponseBo updateData(String headId, String startDt, String endDt, String dateRange, String actType) {
        activityHeadService.updateData(headId, startDt, endDt, dateRange, actType);
        return ResponseBo.ok();
    }

    /**
     * 活动头表新增数据
     * @return
     */
    @GetMapping("/addData")
    public ResponseBo addData(ActivityHead activityHead) {
        return ResponseBo.okWithData(null, activityHeadService.addData(activityHead));
    }

    @GetMapping("/getDataById")
    public ResponseBo getDataById(String headId) {
        return ResponseBo.okWithData(null, activityHeadService.getDataById(headId));
    }

    /**
     * 获取自主类型的开始和结束日期
     *
     * @return
     */
    @GetMapping("/getStartAndEndDate")
    public ResponseBo getStartAndEndDate() {
        Map<String, Object> result = Maps.newHashMap();
        result.put("start", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        result.put("end", LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return ResponseBo.okWithData(null, result);
    }

    /**
     * 日历上新增活动
     * @param config
     * @return
     */
    @PostMapping("/saveActivityConfig")
    public ResponseBo saveActivityConfig(ActivityConfig config) {
        Map<String, String> effectDate = activityWeightService.getEffectDate(config.getStartDate(), config.getEndDate());
        config.setBeforeDate(effectDate.get("begin"));
        config.setAfterDate(effectDate.get("end"));
        activityConfigService.save(config);
        return ResponseBo.ok();
    }

    /**
     * 获取日历上的活动
     * @return
     */
    @GetMapping("/getActivityConfig")
    public ResponseBo getActivityConfig() {
        return ResponseBo.okWithData(null, activityConfigService.getActivityConfigList());
    }

    @GetMapping("/getActivityConfigByType")
    public ResponseBo getActivityConfigByType(@RequestParam String type) {
        return ResponseBo.okWithData(null, activityConfigService.getActivityConfigByType(type));
    }

    @GetMapping("/getActivityConfigById")
    public ResponseBo getActivityConfigById(@RequestParam String id) {
        return ResponseBo.okWithData(null, activityConfigService.getActivityConfigById(id));
    }

    /**
     * 获取活动运营用户群体
     * @return
     */
    @GetMapping("getActivityUserListPage")
    public ResponseBo getActivityUserListPage(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String startDate = request.getParam().get("startDate");
        String endDate = request.getParam().get("endDate");
        int total = activityUserService.getCount(startDate, endDate);
        List<ActivityUser> activityUsers = activityUserService.getActivityUserListPage(start, end, startDate, endDate);
        return ResponseBo.okOverPaging(null, total, activityUsers);
    }

    /**
     * 获取活动运营商品方案
     */
    @GetMapping("/getActivityProductListPage")
    public ResponseBo getActivityProductListPage(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String headId = request.getParam().get("headId");
        int total = activityProductService.getCount(headId);
        List<ActivityProduct> activityProducts = activityProductService.getActivityProductListPage(start, end, headId);
        return ResponseBo.okOverPaging(null, total, activityProducts);
    }

    /**
     * 根据开始结束日期获取商品优惠信息并插入到数据库
     * @param startDate
     * @param endDate
     * @return
     */
    @GetMapping("/saveActivityProduct")
    public ResponseBo saveActivityProduct(String startDate, String endDate, String headId) {
        activityProductService.saveActivityProductBySql(startDate, endDate, headId);
        return ResponseBo.ok();
    }

    /**
     * 获取用户数在优惠券上的分布数据（箱线图）
     * @return
     */
    @GetMapping("/getCouponBoxData")
    public ResponseBo getCouponBoxData(String productId, String startDate, String endDate) {
        return ResponseBo.okWithData(null, activityProductService.getCouponBoxData(productId, startDate, endDate));
    }


    /**
     * 活动获取影响开始日期和结束日期
     * @return
     */
    @GetMapping("/getEffectDateInfo")
    public ResponseBo getEffectDateInfo(String beginDate,String endDate) {
        Map<String,String> result=activityWeightService.getEffectDate(beginDate,endDate);
        return ResponseBo.okWithData(null, result);
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response) throws Exception{
        String fileName = "单品宝.xlsx";
        File file = ResourceUtils.getFile("classpath:templates/excel_template/123.xlsx");
        response.setContentType("application/vnd..ms-excel");
        response.setHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(fileName, "utf-8"));
        response.setCharacterEncoding("utf-8");

        try (InputStream inputStream = new FileInputStream(file); OutputStream os = response.getOutputStream()) {
            byte[] b = new byte[2048];
            int length;
            while ((length = inputStream.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("downloadFile")
    public void fileDownload(HttpServletResponse response) throws IOException {
        String fileName = "单品宝.xlsx";
        if (StringUtils.isNotBlank(fileName) && !fileName.endsWith(".xlsx") && !fileName.endsWith(".csv"))
            throw new RuntimeException("不支持该类型文件下载");
        String realFileName = System.currentTimeMillis() + "_" + fileName.substring(fileName.indexOf('_') + 1);

        File file = new File("file/danpinbao.xlsx");
        if (!file.exists())
            throw new RuntimeException("文件未找到");
        response.setHeader("Content-Disposition", "inline;fileName=" + java.net.URLEncoder.encode(realFileName, "utf-8"));
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        try (InputStream inputStream = new FileInputStream(file); OutputStream os = response.getOutputStream()) {
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
