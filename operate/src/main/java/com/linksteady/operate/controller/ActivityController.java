package com.linksteady.operate.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.util.CollectionUtils;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private ActivityProductService activityProductService;

    @Autowired
    private ActivityUserGroupService activityUserGroupService;

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
     * 商品文件批量上传
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/uploadExcel")
    public ResponseBo uploadExcel(@RequestParam("file") MultipartFile file, @RequestParam String headId, @RequestParam String stage) {
        List<ActivityProduct> productList = Lists.newArrayList();
        String originalFilename = file.getOriginalFilename();
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (fileType.equalsIgnoreCase(".xlsx") || fileType.equalsIgnoreCase(".xls")) {
            InputStream is;
            Workbook wb = null;
            try {
                is = file.getInputStream();
                if (fileType.equalsIgnoreCase(".xls")) {
                    wb = new HSSFWorkbook(is);
                } else if (fileType.equalsIgnoreCase(".xlsx")) {
                    wb = new XSSFWorkbook(is);
                }
                Sheet sheet = wb.getSheetAt(0);
                for (Row row : sheet) {
                    row.cellIterator().next();
                    if(row.getCell(0).getRowIndex() == 0) {
                        continue;
                    }
                    ActivityProduct activityProduct = new ActivityProduct();
                    activityProduct.setHeadId(Long.valueOf(headId));
                    activityProduct.setActivityStage(stage);
                    activityProduct.setProductId(row.getCell(0).getStringCellValue());
                    activityProduct.setProductName(row.getCell(2).getStringCellValue());
                    activityProduct.setMinPrice(row.getCell(3).getNumericCellValue());
                    activityProduct.setFormalPrice(row.getCell(4).getNumericCellValue());
                    activityProduct.setActivityIntensity(row.getCell(5).getNumericCellValue());
                    activityProduct.setProductAttr(row.getCell(6).getStringCellValue());
                    productList.add(activityProduct);
                }
                if(productList.size() != 0) {
                    activityProductService.saveActivityProductList(productList);
                }else {
                    return ResponseBo.error("上传的数据为空！");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return ResponseBo.error("只支持.xls,.xlsx后缀的文件！");
        }
        return ResponseBo.ok("文件上传成功！");
    }

    /**
     * 保存活动基本信息
     *
     * @param activityHead
     * @return
     */
    @PostMapping("/saveActivityHead")
    public ResponseBo saveActivityHead(ActivityHead activityHead) {
        int headId = activityHeadService.saveActivityHead(activityHead);
        if (activityHead.getHeadId() == null) {
            return ResponseBo.okWithData("活动信息保存成功！", headId);
        } else {
            return ResponseBo.okWithData("活动信息更新成功！", headId);
        }
    }

    /**
     * 获取活动商品页
     *
     * @return
     */
    @GetMapping("/getActivityProductPage")
    public ResponseBo getActivityProductPage(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String headId = request.getParam().get("headId");
        String productId = request.getParam().get("productId");
        String productName = request.getParam().get("productName");
        String productAttr = request.getParam().get("productAttr");
        String stage = request.getParam().get("stage");
        int count = activityProductService.getCount(headId, productId, productName, productAttr, stage);
        List<ActivityProduct> productList = activityProductService.getActivityProductListPage(start, end, headId, productId, productName, productAttr, stage);
        return ResponseBo.okOverPaging(null, count, productList);
    }

    /**
     * 保存商品信息
     *
     * @param activityProduct
     * @param headId
     * @return
     */
    @PostMapping("/saveActivityProduct")
    public ResponseBo saveActivityProduct(ActivityProduct activityProduct, String headId) {
        activityProduct.setHeadId(Long.valueOf(headId));
        activityProductService.saveActivityProduct(activityProduct);
        return ResponseBo.ok();
    }

    /**
     * 活动商品表模板下载
     *
     * @param response
     * @throws IOException
     */
    @RequestMapping("/downloadFile")
    public void fileDownload(HttpServletResponse response) throws IOException {
        String fileName = "activity_product_template.xlsx";
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

    /**
     * 获取产品信息
     *
     * @param id
     * @return
     */
    @GetMapping("/getProductById")
    public ResponseBo getProductById(@RequestParam("id") String id) {
        return ResponseBo.okWithData(null, activityProductService.getProductById(id));
    }

    /**
     * 更新产品信息
     *
     * @param activityProduct
     * @return
     */
    @PostMapping("/updateActivityProduct")
    public ResponseBo updateActivityProduct(ActivityProduct activityProduct) {
        activityProductService.updateActivityProduct(activityProduct);
        return ResponseBo.ok();
    }

    /**
     * 用户群组的分页数据
     * @param request
     * @return
     */
    @GetMapping("/getActivityUserGroupPage")
    public ResponseBo getActivityUserGroupPage(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String stage = request.getParam().get("stage");
        String headId = request.getParam().get("headId");
        int count = activityUserGroupService.getCount(headId, stage);
        List<ActivityGroup> activityGroups = activityUserGroupService.getUserGroupPage(headId, stage, start, end);
        return ResponseBo.okOverPaging(null, count, activityGroups);
    }

    /**
     * 不分页的数据
     * @param headId
     * @param stage
     * @return
     */
    @GetMapping("/getActivityUserGroupList")
    public List<ActivityGroup> getActivityUserGroupPage(@RequestParam String headId, @RequestParam String stage) {
        List<ActivityGroup> activityGroups = activityUserGroupService.getUserGroupList(headId, stage);
        return activityGroups;
    }

    /**
     * 获取模板消息数据
     * @return
     */
    @GetMapping("/getTemplateTableData")
    public ResponseBo getTemplateTableData() {
        return ResponseBo.okWithData(null, activityHeadService.getTemplateTableData());
    }

    @GetMapping("/updateGroupTemplate")
    public ResponseBo updateGroupTemplate(@RequestParam String groupId, @RequestParam String code) {
        activityUserGroupService.updateGroupTemplate(groupId, code);
        return ResponseBo.ok();
    }

    /**
     * 获取任务计划
     * @param headId
     * @return
     */
    @RequestMapping("/getPlanList")
    public ResponseBo getPlanList(@RequestParam String headId) {
        List<ActivityPlan> planList = activityHeadService.getPlanList(headId);
        Map<String, List<ActivityPlan>> result = planList.stream().collect(Collectors.groupingBy(ActivityPlan::getStage));
        return ResponseBo.okWithData(null, result);
    }

    @GetMapping("/getActivityName")
    public ResponseBo getActivityName(@RequestParam String headId) {
        return ResponseBo.okWithData(null, activityHeadService.getActivityName(headId));
    }

    @PostMapping("/submitActivity")
    public ResponseBo submitActivity(@RequestParam String headId, @RequestParam String stage) {
        activityHeadService.submitActivity(headId, stage);
        return ResponseBo.ok();
    }
}
