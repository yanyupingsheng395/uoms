package com.linksteady.operate.controller;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.FileUtils;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.*;
import com.linksteady.operate.thrift.ActivityThriftClient;
import com.linksteady.operate.vo.ActivityGroupVO;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;
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

    @Autowired
    ActivitySummaryService activitySummaryService;

    @Autowired
    private ActivityDetailService activityDetailService;

    @Autowired
    private DailyProperties dailyProperties;

    @Autowired
    private ActivityEffectService activityEffectService;

    // 活动阶段：预热
    private final String STAGE_PREHEAT = "preheat";
    // 活动阶段：正式
    private final String STAGE_FORMAL = "formal";

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
    public ResponseBo uploadExcel(@RequestParam("file") MultipartFile file, @RequestParam String headId, @RequestParam String stage, @RequestParam String operateType) {
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
                            // 可能会出现空列的情况
                            if(!x.getStringCellValue().equalsIgnoreCase("")) {
                                if(headers.indexOf(x.getStringCellValue()) == -1) {
                                    flag.set(false);
                                }
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
                    // skuCode非必填
                    if(null != row.getCell(1)) {
                        activityProduct.setSkuCode(row.getCell(1).getStringCellValue());
                    }
                    // 验证产品名称不超过最大长度
//                    if(row.getCell(2).getStringCellValue().length() > dailyProperties.getProdNameLen()) {
//                        throw new LinkSteadyException("商品名称长度超过系统设置！");
//                    }
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
                            default: attr = "";
                    }
                    activityProduct.setProductAttr(attr);
                    activityProduct.setProductUrl(activityProductService.generateProductShortUrl(activityProduct.getProductId(),"S"));
                    productList.add(activityProduct);
                }
                if(!flag.get()) {
                    throw new LinkSteadyException("检测到上传数据与模板格式不符，请检查后重新上传！");
                }
                if(productList.size() != 0) {
                    List<String> productIdList = productList.stream().map(ActivityProduct::getProductId).collect(Collectors.toList());
                    // 获取相同productId的商品个数
                    int count = activityProductService.getSameProductCount(productIdList, headId, stage);
                    if(count > 0) {
                        activityProductService.deleteRepeatData(productList, headId, stage);
                        activityProductService.saveActivityProductList(productList);
                        if("update".equalsIgnoreCase(operateType)) {
                            changeAndUpdateStatus(headId, stage);
                            log.info("更新短信模板,headId:{}的状态发生变更。", headId);
                        }
                        return ResponseBo.ok("当前Excel中共有" + count + "条记录与已有记录重复，旧记录已覆盖！");
                    }
                    if("update".equalsIgnoreCase(operateType)) {
                        changeAndUpdateStatus(headId, stage);
                        log.info("更新短信模板,headId:{}的状态发生变更。", headId);
                    }
                    activityProductService.saveActivityProductList(productList);
                    return ResponseBo.ok("文件上传成功！");
                }else {
                    throw new LinkSteadyException("上传的数据为空！");
                }
            } catch (IOException e) {
                log.error("上传excel失败", e);
                return ResponseBo.error("上传excel失败，请检查。");
            } catch (LinkSteadyException e) {
                log.error("解析excel失败", e);
                return ResponseBo.error(e.getMessage());
            } catch (IllegalArgumentException e) {
                return ResponseBo.error("最低单价，非活动售价为数值型，其余为字符型。");
            } catch (NullPointerException e) {
                return ResponseBo.error("解析excel失败，除ERP货号外，其余列的值必填！");
            }catch (Exception ex) {
                log.error("解析excel失败", ex);
                return ResponseBo.error("解析excel失败，请检查。");
            }
        } else {
            return ResponseBo.error("只支持.xls,.xlsx后缀的文件！");
        }
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
    public ResponseBo saveActivityProduct(ActivityProduct activityProduct, String headId, String operateType) {
        try {
            activityProduct.setHeadId(Long.valueOf(headId));
            double minPrice = activityProduct.getMinPrice();
            double formalPrice = activityProduct.getFormalPrice();
            double activityIntensity = minPrice/formalPrice * 100;
            activityIntensity = Double.valueOf(String.format("%.2f", activityIntensity));
            activityProduct.setActivityIntensity(activityIntensity);
            activityProduct.setProductUrl(activityProductService.generateProductShortUrl(activityProduct.getProductId(),"S"));

            List<String> productIdList = Collections.singletonList(activityProduct.getProductId());
            int count = activityProductService.getSameProductCount(productIdList, headId, activityProduct.getActivityStage());
            if(count > 0) {
                return ResponseBo.error("已有相同商品ID！");
            }
            if(activityProduct.getProductName().length() > dailyProperties.getProdNameLen()) {
                throw new LinkSteadyException("商品名称超过系统设置！");
            }
            activityProductService.saveActivityProduct(activityProduct);
            if("update".equalsIgnoreCase(operateType)) {
                changeAndUpdateStatus(headId, activityProduct.getActivityStage());
                log.info("新增商品,headId:{}的状态发生变更。", headId);
            }
        } catch (LinkSteadyException ex){
            return ResponseBo.error(ex.getMessage());
        }catch (Exception ex) {
            log.error("新增商品失败", ex);
            return ResponseBo.error("新增商品失败");
        }
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
    public ResponseBo updateActivityProduct(ActivityProduct activityProduct, String operateType) {
        try {
            if(activityProduct.getProductName().length() > dailyProperties.getProdNameLen()) {
                throw new LinkSteadyException("商品名称超过系统设置！");
            }
            activityProductService.updateActivityProduct(activityProduct);
            if("update".equalsIgnoreCase(operateType)) {
                changeAndUpdateStatus(activityProduct.getHeadId().toString(), activityProduct.getActivityStage());
                log.info("修改商品,headId:{}的状态发生变更。", activityProduct.getHeadId());
            }
            return ResponseBo.ok();
        } catch (LinkSteadyException e) {
            return ResponseBo.error(e.getMessage());
        }
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
     * 获取短信模板示例
     * @param headId
     * @param stage
     * @return
     */
    @GetMapping("/getActivityUserList")
    public ResponseBo getActivityUserList(@RequestParam String headId, @RequestParam String stage) {
        DecimalFormat df = new DecimalFormat("#.##");
        //获取第一个商品信息
        ActivityProduct activityProduct=activityProductService.geFirstProductInfo(headId,stage);

        if(null==activityProduct)
        {
            //返回提示信息
            return ResponseBo.error("请上传至少一个活动商品,才能预览推送内容示例！");
        }else {
            List<ActivityGroupVO> result=Lists.newArrayList();
            ActivityGroupVO vo;

            //模板填充
            List<ActivityGroup> activityGroups = activityUserGroupService.getActivityUserList(headId, stage);
            for(ActivityGroup group:activityGroups)
            {
                vo=new ActivityGroupVO();
                vo.setGroupName(group.getGroupName());
                vo.setInGrowthPath(group.getInGrowthPath());
                vo.setActiveLevel(group.getActiveLevel());

                //可替换的变量有 ${PROD_NAME} ${PRICE} ${PROD_URL}
                if(!StringUtils.isEmpty(group.getSmsTemplateContent()))
                {
                    String content=group.getSmsTemplateContent().replace("${PROD_NAME}",activityProduct.getProductName())
                    .replace("${PRICE}", df.format(activityProduct.getMinPrice()))
                    .replace("${PROD_URL}",activityProduct.getProductUrl());

                    vo.setContent(content);
                }else
                {
                    vo.setContent("");
                }

                //可替换的变量有 ${PROD_NAME}
                if(!StringUtils.isEmpty(group.getSmsTemplateContentNormal()))
                {
                    String content=group.getSmsTemplateContentNormal().replace("${PROD_NAME}",activityProduct.getProductName());
                    vo.setContentNormal(content);

                }else
                {
                    vo.setContentNormal("");
                }

                result.add(vo);
            }
            return ResponseBo.okWithData("",result);
        }
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
    public ResponseBo updateGroupTemplate(@RequestParam String headId,@RequestParam String groupId, @RequestParam String code, @RequestParam String stage, @RequestParam String operateType) {
        activityUserGroupService.updateGroupTemplate(headId, groupId, code, stage);
        if("update".equalsIgnoreCase(operateType)) {
            changeAndUpdateStatus(headId, stage);
            log.info("更新短信模板,headId:{}的状态发生变更。", headId);
        }
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
    public ResponseBo submitActivity(@RequestParam String headId, @RequestParam String stage, @RequestParam String operateType) {
        if("update".equalsIgnoreCase(operateType)) {
            submitAndUpdateStatus(headId, stage);
        }else {
            activityHeadService.submitActivity(headId, stage);
        }
        return ResponseBo.ok();
    }


    @RequestMapping("/test")
    public ResponseBo test() {
        try {
            activityThriftClient.open();
           Map<Integer,String> predictCnt=activityThriftClient.getActivityService().getPredictCnt(1,"0");
        } catch (TException e) {
           //进行异常上报
            log.error("获取活动预估人数异常,",e);
        }finally {
            activityThriftClient.close();
        }
        return ResponseBo.ok();
    }

    @PostMapping("/deleteProduct")
    public ResponseBo deleteProduct(@RequestParam String headId, @RequestParam String stage, @RequestParam String operateType, @RequestParam String productIds) {
        activityProductService.deleteProduct(headId, stage, productIds);
        if("update".equalsIgnoreCase(operateType)) {
            changeAndUpdateStatus(headId, stage);
            log.info("删除商品,headId:{}的状态发生变更。", headId);
        }
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

    @GetMapping("/getUserGroupList")
    public List<ActivitySummary> getUserGroupList(@RequestParam String headId, @RequestParam String planDtWid) {
        List<ActivitySummary> activitySummaryList = activitySummaryService.getUserGroupList(headId, planDtWid);
        ActivitySummary activitySummary = new ActivitySummary();
        activitySummary.setGroupName("总计");
        if(activitySummaryList.size() == 0) {
            activitySummary.setGroupUserCnt(0L);
        }else {
            Long sum = activitySummaryList.stream().map(ActivitySummary::getGroupUserCnt).distinct().reduce(Long::sum).get();
            activitySummary.setGroupUserCnt(sum);
        }
        activitySummaryList.add(activitySummary);
        return activitySummaryList;
    }

    @GetMapping("/getDetailPage")
    public ResponseBo getDetailPage(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String headId = request.getParam().get("headId");
        String planDtWid = request.getParam().get("planDtWid");
        int count = activityDetailService.getDataCount(headId, planDtWid);
        List<ActivityDetail>  dataList = activityDetailService.getPageList(start, end, headId, planDtWid);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 导出名单
     * @param headId
     * @return
     * @throws InterruptedException
     */
    @PostMapping("/downloadDetail")
    public ResponseBo downloadDetail(@RequestParam("headId") String headId, @RequestParam("planDtWid") String planDtWid) throws InterruptedException {
        List<ActivityDetail> list = Lists.newLinkedList();
        List<Callable<List<ActivityDetail>>> tmp = Lists.newLinkedList();
        int count = activityDetailService.getDataCount(headId, planDtWid);
        ExecutorService service = Executors.newFixedThreadPool(10);
        int pageSize = 1000;
        int pageNum = count % pageSize == 0 ? count / pageSize : (count / pageSize) + 1;
        for (int i = 0; i < pageNum; i++) {
            int idx = i;
            tmp.add(() -> {
                int start = idx * pageSize + 1;
                int end = (idx + 1) * pageSize;
                end = end > count ? count : end;
                return activityDetailService.getPageList(start, end, headId, planDtWid);
            });
        }

        List<Future<List<ActivityDetail>>> futures = service.invokeAll(tmp);
        futures.stream().forEach(x-> {
            try {
                list.addAll(x.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error("活动运营获取结果失败", e);
            }
        });
        try {
            return FileUtils.createExcelByPOIKit(planDtWid + "_运营名单表", list, ActivityDetail.class);
        } catch (Exception e) {
            log.error("导出活动运营个体结果表失败", e);
            return ResponseBo.error("导出活动运营个体结果表失败，请联系网站管理员！");
        }
    }

    /**
     * 开始执行计划
     * @return
     */
    @PostMapping("/startPush")
    public synchronized ResponseBo startPush(@RequestParam String headId, @RequestParam String planDateWid, @RequestParam String stage) {
        String todoStatus = "1";
        String doingStatus = "2";
        String status = activityPlanService.getStatus(headId, planDateWid);
        if(!todoStatus.equalsIgnoreCase(status)) {
            return ResponseBo.error("当前计划的状态不支持该操作！");
        }

        //写入推送表
        activityPlanService.insertToPushListLarge(headId, planDateWid);

        activityPlanService.updateStatus(headId, planDateWid, doingStatus);

        //更新头表的状态为执行中
        if(!StringUtils.isEmpty(stage)&&"preheat".equals(stage))
        {
            activityHeadService.updatePreheatHeadToDoing(headId);
        }else if(!StringUtils.isEmpty(stage)&&"formal".equals(stage))
        {
            activityHeadService.updateFormalHeadToDoing(headId);
        }

        return ResponseBo.ok();
    }

    /**
     * 停止执行计划
     * @return
     */
    @PostMapping("/stopPush")
    public ResponseBo stopPush(@RequestParam String headId, @RequestParam String planDateWid) {
        String doingStatus = "2";
        String stoppedStatus = "4";
        String status = activityPlanService.getStatus(headId, planDateWid);
        if(!doingStatus.equalsIgnoreCase(status)) {
            return ResponseBo.error("当前计划的状态不支持该操作！");
        }
        //停止 修改push_list_large表中对应记录的状态为F (失败)
        activityPlanService.updatePushListLargeToFaild(headId, planDateWid);

        activityPlanService.updateStatus(headId, planDateWid, stoppedStatus);
        return ResponseBo.ok();
    }

    @PostMapping("/deleteActivity")
    public ResponseBo deleteActivity(@RequestParam String headId) {
        int count = activityHeadService.getDeleteCount(headId);
        if(count == 1) {
            activityHeadService.deleteData(headId);
            activityProductService.deleteData(headId);
            activityPlanService.deleteData(headId);
            activityUserGroupService.deleteData(headId);
        }else {
            return ResponseBo.error("该计划当前状态不支持删除操作！");
        }
        return ResponseBo.ok();
    }

    /**
     * 获取头表状态
     * @param headId
     * @param stage
     * @return
     */
    private String getHeadStatus(String headId, String stage) {
        String status = "";
        if(STAGE_PREHEAT.equalsIgnoreCase(stage)) {
            status = activityHeadService.getStatus(headId, stage);
        }
        if(STAGE_FORMAL.equalsIgnoreCase(stage)) {
            status = activityHeadService.getStatus(headId, stage);
        }
        return status;
    }

    /**
     * 如果是待执行，执行中，修改了产品、文案，则变更状态为待计划；
     * @param headId
     * @param stage
     */
    private void changeAndUpdateStatus(String headId, String stage) {
        String status = getHeadStatus(headId, stage);
        if(status.equalsIgnoreCase("todo") || stage.equalsIgnoreCase("doing")) {
            activityHeadService.updateStatus(headId, stage, "edit");
        }
    }

    /**
     *  提交计划，判断是否有执行中2、执行完3 已停止4的plan,如果存在，变更状态为执行中，否则变更状态为待执行；
      */
    private void submitAndUpdateStatus(String headId, String stage) {
        int count = activityPlanService.getStatusCount(headId, stage, Arrays.asList("2", "3", "4"));
        String status;
        if(count > 0) {
            status = "doing";
        }else {
            status = "todo";
        }
        activityHeadService.updateStatus(headId, stage, status);
    }

    /**
     * 获取主要指标
     * @param headId
     * @param pushKpi
     * @return
     */
    @GetMapping("/getEffectMainKpi")
    public ResponseBo getEffectMainKpi(@RequestParam("headId") String headId, @RequestParam("pushKpi") String pushKpi) {
        return ResponseBo.okWithData(null, activityEffectService.getEffectMainKpi(headId, pushKpi));
    }

    /**
     * 获取头部信息
     * @param headId
     * @return
     */
    @GetMapping("/getEffectInfo")
    public ResponseBo getEffectInfo(@RequestParam("headId") String headId) {
        return ResponseBo.okWithData(null, activityEffectService.getEffectInfo(headId));
    }

    /**
     * 获取全部指标
     * @param headId
     * @param pushKpi
     * @return
     */
    @GetMapping("/getEffectAllKpi")
    public ResponseBo getEffectAllKpi(@RequestParam("headId") String headId, @RequestParam("pushKpi") String pushKpi) {
        return ResponseBo.okWithData(null, activityEffectService.getEffectAllKpi(headId, pushKpi));
    }
}
