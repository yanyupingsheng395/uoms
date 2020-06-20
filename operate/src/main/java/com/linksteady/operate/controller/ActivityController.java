package com.linksteady.operate.controller;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.FileUtils;
import com.linksteady.operate.config.PushConfig;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.*;
import com.linksteady.operate.service.impl.RedisMessageServiceImpl;
import com.linksteady.operate.thrift.ActivityService;
import com.linksteady.operate.thrift.ActivityThriftClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
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
    private PushConfig pushConfig;

    @Autowired
    private ActivityEffectService activityEffectService;

    @Autowired
    private ActivityCovService activityCovService;

    @Autowired
    private ActivityThriftClient activityThriftClient;

    /**
     * 获取头表的分页数据
     */
    @GetMapping("/gePageOfHead")
    public ResponseBo gePageOfHead(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        String name = request.getParam().get("name");
        String date = request.getParam().get("date");
        String status = request.getParam().get("status");

        List<ActivityHead> dataList = activityHeadService.getDataListOfPage(limit, offset, name, date, status);
        int count = activityHeadService.getDataCount(name);

        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 商品文件批量上传
     * @param headId
     * @param uploadMethod  上传模式：0追加，1覆盖
     * @param repeatProduct 重复商品：0忽略，1覆盖
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/uploadExcel")
    public ResponseBo uploadExcel(@RequestParam("file") MultipartFile file, @RequestParam String headId,
                                  @RequestParam("uploadMethod") String uploadMethod,
                                  @RequestParam("repeatProduct") String repeatProduct, @RequestParam("stage") String stage,
                                  @RequestParam("activityType") String activityType) {
        List<ActivityProductUploadError> errorList;
        try {
            errorList = activityProductService.uploadExcel(file, headId, uploadMethod, repeatProduct, stage, activityType);
            validProductInfo(headId, stage);
        } catch (Exception e) {
            log.error("上传商品列表出错", e);
            return ResponseBo.error("上传商品出现未知错误！");
        }
        return ResponseBo.okWithData(null, errorList);
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
        return ResponseBo.okWithData("活动信息保存成功,接下来请添加活动商品！", headId);
    }

    /**
     * 获取活动商品页
     *
     * @return
     */
    @GetMapping("/getActivityProductPage")
    public ResponseBo getActivityProductPage(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        String headId = request.getParam().get("headId");
        String productId = request.getParam().get("productId");
        String productName = request.getParam().get("productName");
        String groupId = request.getParam().get("groupId");
        String activityStage = request.getParam().get("activityStage");
        String activityType = request.getParam().get("activityType");
        int count = activityProductService.getCount(headId, productId, productName, groupId, activityStage, activityType);
        List<ActivityProduct> productList = activityProductService.getActivityProductListPage(limit,offset, headId, productId, productName, groupId, activityStage, activityType);
        return ResponseBo.okOverPaging(null, count, productList);
    }

    /**
     * 验证商品信息
     * @param headId
     * @return
     */
    @GetMapping("/validProductInfo")
    public ResponseBo validProductInfo(@RequestParam("headId") String headId, @RequestParam("stage") String stage) {
        activityProductService.validProductInfo(headId, stage);
        return ResponseBo.ok();
    }

    // todo 商品名称不能超过短信预设的，商品ID防重复
    /**
     * 保存商品信息
     * @return
     */
    @PostMapping("/saveActivityProduct")
    public ResponseBo saveActivityProduct(ActivityProduct activityProduct) {
        activityProduct.setProductUrl(activityProductService.generateProductShortUrl(activityProduct.getProductId(),"S"));
        activityProductService.saveActivityProduct(activityProduct);
        validProductInfo(activityProduct.getHeadId().toString(), activityProduct.getActivityStage());
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
        try {
            if(activityProduct.getProductName().length() > pushConfig.getProdNameLen()) {
                throw new LinkSteadyException("商品名称超过系统设置！");
            }
            activityProductService.updateActivityProduct(activityProduct);
            validProductInfo(String.valueOf(activityProduct.getHeadId()), activityProduct.getActivityStage());
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
        int limit = request.getLimit();
        int offset = request.getOffset();
        String stage = request.getParam().get("stage");
        Long headId = Long.parseLong(request.getParam().get("headId"));
        int count = activityUserGroupService.getCount(headId, stage);
        List<ActivityGroup> activityGroups = activityUserGroupService.getUserGroupPage(headId, stage, limit,offset);
        return ResponseBo.okOverPaging(null, count, activityGroups);
    }

    @GetMapping("/updateGroupTemplate")
    public ResponseBo updateGroupTemplate(@RequestParam Long headId,@RequestParam Long groupId, @RequestParam Long code, @RequestParam String stage, @RequestParam String operateType) {
        activityUserGroupService.updateGroupTemplate(headId, groupId, code, stage);
        return ResponseBo.ok();
    }

    @GetMapping("/getActivityName")
    public ResponseBo getActivityName(@RequestParam String headId) {
        return ResponseBo.okWithData(null, activityHeadService.getActivityName(headId));
    }

    /**
     * 保存计划
     * @param headId
     * @param stage
     * @param type
     * @return
     */
    @PostMapping("/submitActivity")
    public ResponseBo submitActivity(@RequestParam Long headId, @RequestParam String stage, @RequestParam String type) {
        //生成计划明细数据
        List<ActivityPlan> planList = activityPlanService.getPlanList(headId);
        List<ActivityPlan> filterPlanList = planList.stream().filter(x -> x.getStage().equalsIgnoreCase(stage)).filter(y -> y.getPlanType().equalsIgnoreCase(type)).collect(Collectors.toList());
        if(filterPlanList.size() == 0) {
            activityPlanService.savePlanList(headId, stage, type);
        }
        return ResponseBo.ok();
    }

    @PostMapping("/deleteProduct")
    public ResponseBo deleteProduct(@RequestParam String ids) {
        activityProductService.deleteProduct(ids);
        return ResponseBo.ok();
    }

    /**
     * 提交计划之前验证条件是否满足
     * 1.验证所有群组是否配置消息模板
     * 2.验证商品是否为空
     * @return
     */
    @GetMapping("/validSubmit")
    public ResponseBo validSubmit(@RequestParam Long headId, @RequestParam String stage, @RequestParam String type) {
        Map<String, String> data = Maps.newHashMap();
        // 验证所有群组是否配置消息模板 0：合法，非0不合法

        int templateIsNullCount = activityUserGroupService.validGroupTemplateWithGroup(headId, stage, type);
        if(templateIsNullCount > 0) {
            data.put("error", "部分群组文案没有配置");
            updateInfoStatus(headId.toString(), stage, type,  "group", "0");
        }else {
            updateInfoStatus(headId.toString(), stage, type,  "group", "1");
        }
        return ResponseBo.okWithData(null, data);
    }

    /**
     * 获取数据更改状态
     * @param headId
     * @param stage
     * @return
     */
    @GetMapping("/getDataChangedStatus")
    public ResponseBo getDataChangedStatus(@RequestParam Long headId, @RequestParam String stage) {
        return ResponseBo.okWithData(null, activityHeadService.getDataChangedStatus(headId, stage));
    }

    /**
     * 删除活动运营
     * @param headId
     * @return
     */
    @PostMapping("/deleteActivity")
    public ResponseBo deleteActivity(@RequestParam Long headId) {
        int count = activityHeadService.getDeleteCount(headId);
        if(count == 1) {
            activityHeadService.deleteData(headId);
            activityProductService.deleteData(headId);
            activityPlanService.deletePlan(headId);
            activityUserGroupService.deleteData(headId);
            //todo 对应的活动明细尚未删除

        }else {
            return ResponseBo.error("该计划当前状态不支持删除操作！");
        }
        return ResponseBo.ok();
    }


    /**
     * 获取主要指标
     * @param headId
     * @param kpiType
     * @return
     */
    @GetMapping("/getEffectMainKpi")
    public ResponseBo getEffectMainKpi(@RequestParam("headId") Long headId, @RequestParam("kpiType") String kpiType) {
        return ResponseBo.okWithData(null, activityEffectService.getEffectMainKpi(headId, kpiType));
    }

    /**
     * 获取头部信息
     * @param headId
     * @return
     */
    @GetMapping("/getEffectInfo")
    public ResponseBo getEffectInfo(@RequestParam("headId") Long headId) {
        ActivityEffect activityEffect=activityEffectService.getEffectInfo(headId);

        if(null==activityEffect)
        {
            return ResponseBo.error("活动效果尚未计算!");
        }
        Map<String,Object> result= Maps.newHashMap();

        result.put("beginDt",new SimpleDateFormat("yyyy年MM月dd日").format(activityEffect.getPushDate()));
        result.put("userCount",activityEffect.getSuccessNum());
        return ResponseBo.ok(result);
    }

    /**
     * 获取用户群组信息
     * @param headId
     * @param stage
     * @param type
     * @return
     */
    @GetMapping("/getGroupList")
    public List<ActivityGroup> getGroupList(@RequestParam("headId") Long headId, @RequestParam("stage") String stage, @RequestParam("type") String type) {
        return activityUserGroupService.getUserGroupList(headId, stage, type);
    }

    /**
     * 获取默认转化率的数据
     * 如果COV_INFO表中没有数据，则获取COV_LIST中的数据
     *
     * @return
     */
    @GetMapping("/getConvertInfo")
    public List<ActivityCovInfo> geConvertInfo(@RequestParam("headId") String headId, @RequestParam("stage") String stage) {
        return Lists.newArrayList(activityCovService.getConvertInfo(headId, stage));
    }

    @GetMapping("/getCovList")
    public ResponseBo getCovList(
            @RequestParam String headId, @RequestParam String stage
    ) {
        return ResponseBo.okWithData(null, activityCovService.getCovList(headId, stage));
    }

    /**
     * 测算转化率
     * @return
     */
    @GetMapping("/calculateCov")
    public ResponseBo calculateCov(@RequestParam("headId") String headId, @RequestParam("stage") String stage, @RequestParam("changedCovId") String changedCovId,
                                   @RequestParam("defaultCovId") String defaultCovId) {
        return ResponseBo.okWithData(null, activityCovService.calculateCov(headId, stage, changedCovId, defaultCovId));
    }

    /**
     * 新增或更新活动给定阶段的转化率数据
     * @param headId
     * @param stage
     * @param covId
     * @return
     */
    @PostMapping("/updateCovInfo")
    public ResponseBo updateCovInfo(@RequestParam("headId") long headId, @RequestParam("stage") String stage, @RequestParam("covId") String covId) {
        activityCovService.updateCovInfo(headId, stage, covId);
        return ResponseBo.ok();
    }

    @PostMapping("/downloadExcel")
    public ResponseBo excel(@RequestParam("headId") String headId, @RequestParam("activityStage") String activityStage, @RequestParam("activityType") String activityType) throws InterruptedException {
        List<ActivityProduct> list = Lists.newLinkedList();
        List<Callable<List<ActivityProduct>>> tmp = Lists.newLinkedList();
        int count = activityProductService.getCountByHeadId(headId);
        ExecutorService service = Executors.newFixedThreadPool(10);
        int pageSize = 1000;
        int pageNum = count % pageSize == 0 ? count / pageSize : (count / pageSize) + 1;
        DecimalFormat df = new DecimalFormat("#.#");
        for (int i = 0; i < pageNum; i++) {
            int idx = i;
            tmp.add(() -> {
                int start = idx * pageSize + 1;
                int end = (idx + 1) * pageSize;
                end = Math.min(end, count);
                int limit = end - start + 1;
                int offset = start -1;
                List<ActivityProduct> activityProductListPage = activityProductService.getActivityProductListPage(limit, offset, headId, "", "", "", activityStage, activityType);
                activityProductListPage.stream().forEach(x->{
                    if(activityType.equalsIgnoreCase("NOTIFY")) {
                        x.setNotifyMinPrice(df.format(x.getMinPrice()));
                        x.setNotifyProfit(df.format(x.getActivityProfit()));
                    }
                    if(activityType.equalsIgnoreCase("DURING")) {
                        x.setDuringMinPrice(df.format(x.getMinPrice()));
                        x.setDuringProfit(df.format(x.getActivityProfit()));
                    }
                });
                return activityProductListPage;
            });
        }

        List<Future<List<ActivityProduct>>> futures = service.invokeAll(tmp);
        futures.stream().forEach(x -> {
            try {
                list.addAll(x.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        try {
            return FileUtils.createExcelByPOIKit("活动运营商品表", list, ActivityProduct.class);
        } catch (Exception e) {
            log.error("导出活动运营商品表失败", e);
            return ResponseBo.error("导出活动运营商品表失败，请联系网站管理员！");
        }
    }

    @GetMapping("/validProduct")
    public ResponseBo validProduct(@RequestParam String headId, @RequestParam("stage") String stage, @RequestParam String type) {
        return ResponseBo.okWithData(null, activityProductService.validProduct(headId, stage, type));
    }


    @GetMapping("/checkProductId")
    public boolean checkProductId(@RequestParam String headId, @RequestParam String activityType, @RequestParam String activityStage, @RequestParam String productId) {
        if(StringUtils. isEmpty(activityType)) {
            return true;
        }
        return activityProductService.checkProductId(headId, activityType, activityStage, productId);
    }

    private final ReentrantLock reentrantLock = new ReentrantLock();

    @GetMapping("/genCovInfo")
    public ResponseBo genCovInfo(@RequestParam String headId, @RequestParam String activityStage) {
        long result = -1;
        reentrantLock.lock();
        try {
            activityThriftClient.open();
            result = activityThriftClient.getActivityService().genPredictCovData(Long.parseLong(headId), activityStage);
        }catch (TException e) {
            log.info("生成转化率数据出错", e);
        } finally {
            reentrantLock.unlock();
            activityThriftClient.close();
        }
        return ResponseBo.okWithData(null, result);
    }

    /**
     * 校验商品和群组信息
     * 商品:记录数为0，存在验证不通过==0，否则==1
     * 群组:存在商品和群组不一致的==0，否则==1
     *
     * @return
     */
    @RequestMapping("/updateInfoStatus")
    public ResponseBo updateInfoStatus(@RequestParam String headId, @RequestParam String activityStage,
                                       @RequestParam String activityType, @RequestParam String key, @RequestParam String value) {
        activityHeadService.updateInfoStatus(headId, activityStage, activityType, key, value);
        return ResponseBo.ok();
    }
}
