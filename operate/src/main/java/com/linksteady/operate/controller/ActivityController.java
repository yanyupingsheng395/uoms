package com.linksteady.operate.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.FileUtils;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.domain.enums.ActivityPlanTypeEnum;
import com.linksteady.operate.domain.enums.ActivityStageEnum;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.*;
import com.linksteady.operate.thrift.ActivityThriftClient;
import com.linksteady.operate.vo.ActivityGroupVO;
import com.linksteady.operate.vo.DailyPersonalVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

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
    private PushProperties pushProperties;

    @Autowired
    private ActivityEffectService activityEffectService;

    @Autowired
    private ActivityTemplateService activityTemplateService;

    @Autowired
    private ActivityCovService activityCovService;

    @Autowired
    private ShortUrlService shortUrlService;

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
     * @param headId
     * @param uploadMethod  上传模式：0追加，1覆盖
     * @param repeatProduct 重复商品：0忽略，1覆盖
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/uploadExcel")
    public ResponseBo uploadExcel(@RequestParam("file") MultipartFile file, @RequestParam String headId,
                                  @RequestParam("uploadMethod") String uploadMethod, @RequestParam("repeatProduct") String repeatProduct) {
        try {
            activityProductService.uploadExcel(file, headId, uploadMethod, repeatProduct);
            validProductInfo(headId);
        } catch (LinkSteadyException e){
            return ResponseBo.error(e.getMessage());
        }catch (Exception e) {
            log.error("上传商品列表出错", e);
            return ResponseBo.error("上传商品出现未知错误！");
        }
        return ResponseBo.ok();
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
        return ResponseBo.okWithData("活动信息保存成功！", headId);
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
        String groupId = request.getParam().get("groupId");
        int count = activityProductService.getCount(headId, productId, productName, groupId);
        List<ActivityProduct> productList = activityProductService.getActivityProductListPage(start, end, headId, productId, productName, groupId);
        return ResponseBo.okOverPaging(null, count, productList);
    }

    /**
     * 验证商品信息
     * @param headId
     * @return
     */
    @GetMapping("/validProductInfo")
    public ResponseBo validProductInfo(@RequestParam("headId") String headId) {
        activityProductService.validProductInfo(headId);
        return ResponseBo.ok();
    }

    // todo 商品名称不能超过短信预设的，商品ID防重复
    /**
     * 保存商品信息
     * @param activityProduct
     * @param headId
     * @return
     */
    @PostMapping("/saveActivityProduct")
    public ResponseBo saveActivityProduct(ActivityProduct activityProduct, String headId, String operateType) {
        activityProduct.setHeadId(Long.valueOf(headId));
        activityProduct.setProductUrl(activityProductService.generateProductShortUrl(activityProduct.getProductId(),"S"));
        activityProductService.saveActivityProduct(activityProduct);
        validProductInfo(headId);
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
            if(activityProduct.getProductName().length() > pushProperties.getProdNameLen()) {
                throw new LinkSteadyException("商品名称超过系统设置！");
            }
            activityProductService.updateActivityProduct(activityProduct);
            validProductInfo(String.valueOf(activityProduct.getHeadId()));
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
        Long headId = Long.parseLong(request.getParam().get("headId"));
        int count = activityUserGroupService.getCount(headId, stage);
        List<ActivityGroup> activityGroups = activityUserGroupService.getUserGroupPage(headId, stage, start, end);
        return ResponseBo.okOverPaging(null, count, activityGroups);
    }

    @GetMapping("/updateGroupTemplate")
    public ResponseBo updateGroupTemplate(@RequestParam Long headId,@RequestParam String groupId, @RequestParam String code, @RequestParam String stage, @RequestParam String operateType) {
        activityUserGroupService.updateGroupTemplate(headId, groupId, code, stage);
        return ResponseBo.ok();
    }

    @GetMapping("/getActivityName")
    public ResponseBo getActivityName(@RequestParam String headId) {
        return ResponseBo.okWithData(null, activityHeadService.getActivityName(headId));
    }

    /**
     * 提交计划
     * @param headId
     * @param stage
     * @param type
     * @return
     */
    @PostMapping("/submitActivity")
    public ResponseBo submitActivity(@RequestParam Long headId, @RequestParam String stage, @RequestParam String type) {
        activityPlanService.savePlanList(headId, stage, type);
        activityHeadService.updateStatus(headId, stage, "todo", type);
        return ResponseBo.ok();
    }

    @PostMapping("/deleteProduct")
    public ResponseBo deleteProduct(@RequestParam Long headId, @RequestParam String stage, @RequestParam String operateType, @RequestParam String productIds) {
        activityProductService.deleteProduct(headId, stage, productIds);
        validProductInfo(String.valueOf(headId));
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
        // 验证所有群组是否配置消息模板 0：合法，非0不合法
        int templateIsNullCount = -1;
        String data = "";
        if(type.equalsIgnoreCase(ActivityPlanTypeEnum.During.getPlanTypeCode())) {
            templateIsNullCount = activityUserGroupService.validGroupTemplate(headId, stage, type);
            data = "部分群组模板消息未配置！";
        }else if(type.equalsIgnoreCase(ActivityPlanTypeEnum.Notify.getPlanTypeCode())){
            List<String> groupIds = activityProductService.getGroupIds(headId);
            if(groupIds.size() == 0) {
                groupIds = Collections.singletonList("5");
                data = "活动商品数为0，且不参加活动群组模板未配置！";
            }else {
                data = "上传的商品的与已配置文案的活动群组不符！";
            }
            templateIsNullCount = activityUserGroupService.validGroupTemplateWithGroup(headId, stage, type, groupIds);
        }
        if(templateIsNullCount == 0) {
            data = "";
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
            activityPlanService.deleteData(headId);
            activityUserGroupService.deleteData(headId);
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
    public ResponseBo getEffectMainKpi(@RequestParam("headId") String headId, @RequestParam("kpiType") String kpiType) {
        return ResponseBo.okWithData(null, activityEffectService.getEffectMainKpi(headId, kpiType));
    }

    /**
     * 获取头部信息
     * @param headId
     * @return
     */
    @GetMapping("/getEffectInfo")
    public ResponseBo getEffectInfo(@RequestParam("headId") String headId) {
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
     * 保存文案信息
     */
    @PostMapping("/saveSmsTemplate")
    public ResponseBo saveSmsTemplate(ActivityTemplate activityTemplate) {
        activityTemplateService.saveTemplate(activityTemplate);
        return ResponseBo.ok();
    }

    @GetMapping("/getSmsTemplateList")
    public ResponseBo getSmsTemplateList(ActivityTemplate activityTemplate) {
        return ResponseBo.okWithData(null, activityTemplateService.getSmsTemplateList(activityTemplate));
    }

    /**
     * 为群组设置文案
     * @return
     */
    @PostMapping("/setSmsCode")
    public ResponseBo setSmsCode(@RequestParam("groupId") String groupId, @RequestParam("tmpCode") String tmpCode,
                                 @RequestParam("headId") Long headId, @RequestParam("type") String type, @RequestParam("stage") String stage) {
        activityUserGroupService.setSmsCode(groupId, tmpCode, headId, type, stage);
        activityUserGroupService.validUserGroup(headId.toString(), stage);
        return ResponseBo.ok();
    }

    @GetMapping("/getTemplate")
    public ResponseBo getTemplate(@RequestParam("code") String code) {
        return ResponseBo.okWithData(null, activityTemplateService.getTemplate(code));
    }

    @PostMapping("/updateSmsTemplate")
    public ResponseBo updateSmsTemplate(ActivityTemplate activityTemplate) {
        activityTemplateService.update(activityTemplate);
        return ResponseBo.ok();
    }

    @PostMapping("/deleteTmp")
    public ResponseBo deleteTmp(@RequestParam("code") String code) {
        activityTemplateService.deleteTemplate(code);
        return ResponseBo.ok();
    }

    /**
     * 屏蔽测试-短信模板
     * @return
     */
    @GetMapping("/getReplacedTmp")
    public ResponseBo getReplacedTmp(@RequestParam("code") String code) {
        return ResponseBo.okWithData(null, activityTemplateService.getReplacedTmp(code));
    }

    /**
     * 获取默认转化率的数据
     * 如果COV_INFO表中没有数据，则获取COV_LIST中的数据
     *
     * @return
     */
    @GetMapping("/geConvertInfo")
    public ResponseBo geConvertInfo(@RequestParam("headId") String headId, @RequestParam("stage") String stage) {
        return ResponseBo.okWithData(null, activityCovService.geConvertInfo(headId, stage));
    }

    @GetMapping("/getCovList")
    public ResponseBo getCovList() {
        return ResponseBo.okWithData(null, activityCovService.getCovList());
    }

    /**
     * 存入
     * @param headId
     * @param covListId
     * @param stage
     * @return
     */
    @PostMapping("/insertCovInfo")
    public ResponseBo insertCovInfo(@RequestParam("headId") String headId, @RequestParam("covListId") String covListId, @RequestParam("stage") String stage) {
        activityCovService.insertCovInfo(headId, covListId, stage);
        return ResponseBo.ok();
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

    @PostMapping("/updateCovInfo")
    public ResponseBo updateCovInfo(@RequestParam("headId") String headId, @RequestParam("stage") String stage, @RequestParam("covId") String covId) {
        activityCovService.updateCovInfo(headId, stage, covId);
        return ResponseBo.ok();
    }

    /**
     * 校验当前短信模板是否被引用
     * @return
     */
    @GetMapping("/checkTmpIsUsed")
    public ResponseBo checkTmpIsUsed(@RequestParam("tmpCode") String tmpCode) {
        return ResponseBo.okWithData(null, activityUserGroupService.checkTmpIsUsed(tmpCode));
    }

    @PostMapping("/downloadExcel")
    public ResponseBo excel(@RequestParam("headId") String headId) throws InterruptedException {
        List<ActivityProduct> list = Lists.newLinkedList();
        List<Callable<List<ActivityProduct>>> tmp = Lists.newLinkedList();
        int count = activityProductService.getCountByHeadId(headId);
        ExecutorService service = Executors.newFixedThreadPool(10);
        int pageSize = 1000;
        int pageNum = count % pageSize == 0 ? count / pageSize : (count / pageSize) + 1;
        for (int i = 0; i < pageNum; i++) {
            int idx = i;
            tmp.add(() -> {
                int start = idx * pageSize + 1;
                int end = (idx + 1) * pageSize;
                end = end > count ? count : end;
                return activityProductService.getActivityProductListPage(start, end, headId, "", "", "");
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
    public ResponseBo validProduct(@RequestParam String headId) {
        return ResponseBo.okWithData(null, activityProductService.validProduct(headId));
    }

    /**
     * 长链转短链
     * @param url 长链
     * @return
     */
    @GetMapping("/convertShortUrl")
    public ResponseBo convertShortUrl(@RequestParam String url) {
        String shortUrl = "";
        if(StringUtils.isNotEmpty(url)) {
            shortUrl = shortUrlService.genShortUrlDirect(url, "M");
        }
        return ResponseBo.okWithData(null, shortUrl);
    }


    /**
     * 移除群组的券关系
     * @param headId
     * @param stage
     * @param smsCode
     * @param groupId
     * @return
     */
    @PostMapping("/removeSmsSelected")
    public ResponseBo removeSmsSelected(@RequestParam String headId, @RequestParam String stage, @RequestParam String smsCode, @RequestParam String groupId) {
        activityUserGroupService.removeSmsSelected(headId, stage, smsCode, groupId);
        return ResponseBo.ok();
    }
}
