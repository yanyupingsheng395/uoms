package com.linksteady.operate.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.service.*;
import com.linksteady.operate.thrift.ActivityThriftClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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

    @Autowired
    private ActivityPlanService activityPlanService;

    @Autowired
    ActivityThriftClient activityThriftClient;

    /**
     * 获取头表的分页数据
     */
    @GetMapping("/gePageOfHead")
    public ResponseBo gePageOfHead(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String name = request.getParam().get("name");
        String date = request.getParam().get("date");
        String status = request.getParam().get("status");

        List<ActivityHead> dataList = activityHeadService.getDataListOfPage(start, end, name, date, status);
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
        List<String> headers = Arrays.asList("商品ID", "ERP货号", "名称", "最低单价（元/件）", "非活动售价（元/件）", "活动属性【主推，参活，正常】");
        AtomicBoolean flag = new AtomicBoolean(true);
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
                    if(row.getCell(0).getRowIndex() == 0) {
                        Iterator<Cell> cellIterator = row.cellIterator();
                        cellIterator.forEachRemaining(x->{
                            if(headers.indexOf(x.getStringCellValue()) == -1) {
                                flag.set(false);
                            }
                        });
                        if(!flag.get()) {
                            break;
                        }else {
                            continue;
                        }
                    }
                    ActivityProduct activityProduct = new ActivityProduct();
                    activityProduct.setHeadId(Long.valueOf(headId));
                    activityProduct.setActivityStage(stage);
                    activityProduct.setProductId(row.getCell(0).getStringCellValue());
                    activityProduct.setProductName(row.getCell(2).getStringCellValue());
                    double minPrice = row.getCell(3).getNumericCellValue();
                    double formalPrice = row.getCell(4).getNumericCellValue();
                    double activityIntensity = minPrice/formalPrice * 100;
                    activityIntensity = Double.valueOf(String.format("%.2f", activityIntensity));
                    activityProduct.setMinPrice(minPrice);
                    activityProduct.setFormalPrice(formalPrice);
                    activityProduct.setActivityIntensity(activityIntensity);
                    String attr = row.getCell(5).getStringCellValue();
                    switch (attr) {
                        case "主推":
                            attr = "0";
                            break;
                        case "参活":
                            attr = "1";
                            break;
                        case "正常":
                            attr = "2";
                            break;
                            default: attr = "";
                    }
                    activityProduct.setProductAttr(attr);
                    activityProduct.setProductUrl(activityProductService.generateProductShortUrl(activityProduct.getProductId()));
                    productList.add(activityProduct);
                }
                if(!flag.get()) {
                    return ResponseBo.error("检测到数据与模板格式不符，请检查后重新上传！");
                }
                if(productList.size() != 0) {
                    // 获取相同productId的商品个数
                    int count = activityProductService.getSameProductCount(productList, headId, stage);
                    if(count > 0) {
                        activityProductService.deleteRepeatData(productList, headId, stage);
                        activityProductService.saveActivityProductList(productList);
                        return ResponseBo.ok("当前Excel中共有" + count + "条记录与已有记录重复，旧记录已覆盖！");
                    }
                    activityProductService.saveActivityProductList(productList);
                    return ResponseBo.ok("文件上传成功！");
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
        activityProduct.setProductUrl(activityProductService.generateProductShortUrl(activityProduct.getProductId()));
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
//        ActivityGroup activityGroup = new ActivityGroup();
//        activityGroup.setGroupName("总计");
//        activityGroup.setGroupUserCnt(0L);
//        activityGroups.add(activityGroup);
        return activityGroups;
    }

    /**
     * 获取短信模板示例
     * @param headId
     * @param stage
     * @return
     */
    @GetMapping("/getActivityUserList")
    public List<ActivityGroup> getActivityUserList(@RequestParam String headId, @RequestParam String stage) {
        List<ActivityGroup> activityGroups = activityUserGroupService.getActivityUserList(headId, stage);
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
        ActivityHead activityHead = activityHeadService.findById(headId);
        List<ActivityPlan> planList = activityPlanService.getPlanList(headId);
        Map<String, List<ActivityPlan>> result = Maps.newHashMap();
        if("1".equalsIgnoreCase(activityHead.getHasPreheat())) {
            result = planList.stream().collect(Collectors.groupingBy(ActivityPlan::getStage));
        }else {
            result.put("formal", planList);
        }
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


    @RequestMapping("/test")
    public ResponseBo test() {
        try {
            activityThriftClient.open();
           Map<Integer,String> predictCnt=activityThriftClient.getActivityService().getPredictCnt(1,"0");

            System.out.println(predictCnt);
        } catch (TException e) {
           //进行异常上报
            log.error("获取活动预估人数异常,",e);
        }finally {
            activityThriftClient.close();
        }
        return ResponseBo.ok();
    }

    @PostMapping("/deleteProduct")
    public ResponseBo deleteProduct(@RequestParam String headId, @RequestParam String stage, @RequestParam String productIds) {
        activityProductService.deleteProduct(headId, stage, productIds);
        return ResponseBo.ok();
    }

    /**
     * 提交计划之前验证条件是否满足
     * 1.验证所有群组是否配置消息模板
     * 2.验证商品是否为空
     * @return
     */
    @GetMapping("/validSubmit")
    public ResponseBo validSubmit(@RequestParam String headId, @RequestParam String stage) {
        // 验证所有群组是否配置消息模板 0：合法，非0不合法
        int templateIsNullCount = activityUserGroupService.validGroupTemplate(headId, stage);
        // 验证商品数，大于0合法，为0不合法
        int productNum = activityProductService.validProductNum(headId, stage);

        if(productNum == 0) {
            return ResponseBo.error("商品数不能为零！");
        }

        if(templateIsNullCount != 0) {
            return ResponseBo.error("部分群组模板消息未配置！");
        }
        return ResponseBo.ok();
    }

    /**
     * 获取数据更改状态
     * @param headId
     * @param stage
     * @return
     */
    @GetMapping("/getDataChangedStatus")
    public ResponseBo getDataChangedStatus(@RequestParam String headId, @RequestParam String stage) {
        return ResponseBo.okWithData(null, activityHeadService.getDataChangedStatus(headId, stage));
    }

}
