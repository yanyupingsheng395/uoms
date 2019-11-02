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
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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
     * 商品文件批量上传
     *
     * @return
     */
    @PostMapping("/uploadExcel")
    public ResponseBo uploadExcel(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        InputStream is;
        Workbook wb = null;
        try{
            is = file.getInputStream();
            if(fileType.equalsIgnoreCase(".xls")) {
                wb = new HSSFWorkbook(is);
            } else if(fileType.equalsIgnoreCase(".xlsx")) {
                wb = new XSSFWorkbook(is);
            }
            Sheet sheet = wb.getSheetAt(0);
            for(Row row:sheet) {
                int rowNum = row.getRowNum();
                if(rowNum > 0) {
                    System.out.println(row.getCell(0));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存活动基本信息
     * @param activityHead
     * @return
     */
    @PostMapping("/saveActivityHead")
    public ResponseBo saveActivityHead(ActivityHead activityHead) {
        int headId = activityHeadService.saveActivityHead(activityHead);
        if(activityHead.getHeadId() == null) {
            return ResponseBo.okWithData("活动信息保存成功！", headId);
        }else {
            return ResponseBo.okWithData("活动信息更新成功！", headId);
        }
    }

    /**
     * 获取活动商品页
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
}
