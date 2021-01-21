package com.linksteady.operate.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.FileUtils;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.*;
import com.linksteady.operate.vo.FollowUserVO;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2019-08-13
 */
@Slf4j
@RestController
@RequestMapping("/qywxActivity")
public class QywxActivityController {

    @Autowired
    private QywxActivityHeadService qywxActivityHeadService;

    @Autowired
    private QywxActivityProductService qywxActivityProductService;

    @Autowired
    private QywxActivityUserGroupService qywxActivityUserGroupService;

    @Autowired
    private QywxActivityPlanService qywxActivityPlanService;

    @Autowired
    private QywxActivityEffectService qywxActivityEffectService;

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

        List<ActivityHead> dataList = qywxActivityHeadService.getDataListOfPage(limit, offset, name, date, status);
        int count = qywxActivityHeadService.getDataCount(name, date, status);
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
                                  @RequestParam("repeatProduct") String repeatProduct,
                                  @RequestParam("activityType") String activityType) {
        List<ActivityProductUploadError> errorList;
        try {
            errorList = qywxActivityProductService.uploadExcel(file, headId, uploadMethod, repeatProduct, activityType);
            validProductInfo(headId);
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
    public ResponseBo saveActivityHead(ActivityHead activityHead, String coupons) {
        int headId = qywxActivityHeadService.saveActivityHead(activityHead, coupons);
        return ResponseBo.okWithData("活动信息保存成功,接下来请添加活动商品！", headId);
    }

    /**
     * 获取活动商品页
     *
     * @return
     */
    @GetMapping("/getActivityProductPage")
    public ResponseBo getActivityProductPage(Integer limit, Integer offset, String headId, String productId, String productName,
                                             String groupId, String activityType) {
        int count = qywxActivityProductService.getCount(headId, productId, productName, groupId,activityType);
        List<ActivityProduct> productList = qywxActivityProductService.getActivityProductListPage(limit,offset, headId, productId, productName, groupId,activityType);
        return ResponseBo.okOverPaging(null, count, productList);
    }

    /**
     * 验证商品信息
     * @param headId
     * @return
     */
    @GetMapping("/validProductInfo")
    public ResponseBo validProductInfo(@RequestParam("headId") String headId) {
        qywxActivityProductService.validProductInfo(headId);
        return ResponseBo.ok();
    }

    /**
     * 保存商品信息
     * @return
     */
    @PostMapping("/saveActivityProduct")
    public ResponseBo saveActivityProduct(ActivityProduct activityProduct) {
        activityProduct.setProductUrl(qywxActivityProductService.generateProductShortUrl(activityProduct.getProductId(),"S"));
        qywxActivityProductService.saveActivityProduct(activityProduct);
        validProductInfo(activityProduct.getHeadId().toString());
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
        return ResponseBo.okWithData(null, qywxActivityProductService.getProductById(id));
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
            // 由于短信中的商品名称不从此处获取，因此最大长度为数据库所支持的长度
            if(activityProduct.getProductName().length() >= 64) {
                throw new LinkSteadyException("商品名称超过系统设置！");
            }
            qywxActivityProductService.updateActivityProduct(activityProduct);
            validProductInfo(String.valueOf(activityProduct.getHeadId()));
            return ResponseBo.ok();
        } catch (LinkSteadyException e) {
            return ResponseBo.error(e.getMessage());
        }
    }

    @GetMapping("/updateGroupTemplate")
    public ResponseBo updateGroupTemplate(@RequestParam Long headId,@RequestParam Long groupId, @RequestParam Long code) {
        qywxActivityUserGroupService.updateGroupTemplate(headId, groupId, code);
        return ResponseBo.ok();
    }

    @GetMapping("/getActivityName")
    public ResponseBo getActivityName(@RequestParam String headId) {
        return ResponseBo.okWithData(null, qywxActivityHeadService.getActivityName(headId));
    }

    /**
     * 保存计划
     * @param headId
     * @param type 通知 or 期间
     * @return
     */
    @PostMapping("/submitActivity")
    public ResponseBo submitActivity(@RequestParam Long headId, @RequestParam String type) {
        //生成计划明细数据
        List<QywxActivityPlan> planList = qywxActivityPlanService.getPlanList(headId);
        List<QywxActivityPlan> filterPlanList = planList.stream().filter(y -> y.getPlanType().equalsIgnoreCase(type)).collect(Collectors.toList());
        if(filterPlanList.size() == 0) {
            qywxActivityPlanService.savePlanList(headId,type);
        }
        return ResponseBo.ok();
    }

    @PostMapping("/deleteProduct")
    public ResponseBo deleteProduct(@RequestParam String ids) {
        qywxActivityProductService.deleteProduct(ids);
        return ResponseBo.ok();
    }

    /**
     * 提交计划之前验证条件是否满足
     *   1. 活动商品，至少有一个活动商品；
     *   2. 活动商品全部校验通过；
     *   3. 活动转化率数据为最新；
     *   4. 文案全部配置，且校验通过。
     * @return
     */
    @GetMapping("/validSubmit")
    public ResponseBo validSubmit(@RequestParam Long headId, @RequestParam String type) {
        Map<String, String> data = Maps.newHashMap();
        List<String> products = qywxActivityProductService.getNotValidProductCount(headId);
        if(products.size() == 0) {
            data.put("error", "至少需要一个有效商品！");
        }

        // 验证所有群组是否配置消息模板 0：合法，非0不合法
        int templateIsNullCount = qywxActivityUserGroupService.validGroupTemplateWithGroup(headId, type);
        if(templateIsNullCount > 0) {
            data.put("error", "部分群组文案没有配置");
        }

        long notValidProductCount = products.stream().filter(x->x.equalsIgnoreCase("N")).count();
        if(notValidProductCount > 0) {
            data.put("error", "存在校验不通过的商品！");
        }
        return ResponseBo.okWithData(null, data);
    }


    /**
     * 删除活动运营
     * @param headId
     * @return
     */
    @PostMapping("/deleteActivity")
    public ResponseBo deleteActivity(@RequestParam Long headId) {
        int count = qywxActivityHeadService.getDeleteCount(headId);
        if(count == 1) {
            qywxActivityHeadService.deleteData(headId);
        }else {
            return ResponseBo.error("该活动当前状态不支持删除操作！");
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
        return ResponseBo.okWithData(null, qywxActivityEffectService.getEffectMainKpi(headId, kpiType));
    }

    /**
     * 获取头部信息
     * @param headId
     * @return
     */
    @GetMapping("/getEffectInfo")
    public ResponseBo getEffectInfo(@RequestParam("headId") Long headId) {
        ActivityEffect activityEffect=qywxActivityEffectService.getEffectInfo(headId);

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
     * @param type
     * @return
     */
    @GetMapping("/getGroupList")
    public List<ActivityGroup> getGroupList(@RequestParam("headId") Long headId, @RequestParam("type") String type) {
        return qywxActivityUserGroupService.getUserGroupList(headId, type);
    }

    @PostMapping("/downloadExcel")
    public ResponseBo excel(@RequestParam("headId") String headId,@RequestParam("activityType") String activityType) throws InterruptedException {
        List<ActivityProduct> list = Lists.newLinkedList();
        List<Callable<List<ActivityProduct>>> tmp = Lists.newLinkedList();
        int count = qywxActivityProductService.getCountByHeadId(headId);
        ExecutorService service = Executors.newFixedThreadPool(10);
        int pageSize = 1000;
        int pageNum = count % pageSize == 0 ? count / pageSize : (count / pageSize) + 1;
        DecimalFormat df = new DecimalFormat("#.#");
        for (int i = 0; i < pageNum; i++) {
            int finalI = i;
            tmp.add(() -> {
                int limit = pageSize;
                int offset = finalI * pageSize;
                List<ActivityProduct> activityProductListPage = qywxActivityProductService.getActivityProductListPage(limit, offset, headId, "", "", "",activityType);
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
    public ResponseBo validProduct(@RequestParam String headId) {
        return ResponseBo.okWithData(null, qywxActivityProductService.validProduct(headId));
    }


    @GetMapping("/checkProductId")
    public boolean checkProductId(@RequestParam String headId, @RequestParam String productId) {
        return qywxActivityProductService.checkProductId(headId, productId);
    }

    @RequestMapping("/validUserGroup")
    public ResponseBo validUserGroup(@RequestParam String headId) {
        qywxActivityUserGroupService.validUserGroup(headId);
        return ResponseBo.ok();
    }

    /**
     * 获取企业微信成员列表
     *
     * @return
     */
    @GetMapping("/getFollowUserList")
    public ResponseBo getFollowUserList(Long headId) {
        List<FollowUserVO> dataList = qywxActivityHeadService.getAllFollowUserList(headId);
        return ResponseBo.okWithData(null, dataList);
    }

    /**
     * 获取任务的详细信息
     *
     * @param headId
     * @return
     */
    @GetMapping("/getOverAllInfo")
    public ResponseBo getOverAllInfo(@RequestParam Long headId) {
        return ResponseBo.okWithData(null, qywxActivityHeadService.getActivityHeadEffectById(headId));
    }

    /**
     * 获取转化的明细数据
     * @param
     * @return
     */
    @GetMapping("/getConvertDetailData")
    public ResponseBo getConvertDetailData(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        Long headId = Long.parseLong(request.getParam().get("headId"));

        List<QywxActivityConvertDetail> qywxActivityConverDailyList = qywxActivityHeadService.getConvertDetailData(limit, offset, headId);
        int count = qywxActivityHeadService.getConvertDetailCount(headId);
        return ResponseBo.okOverPaging(null, count, qywxActivityConverDailyList);
    }

    @GetMapping("/getUserStatics")
    public ResponseBo getUserStatics(Long headId, String followUserId) {
        QywxActivityStaffEffect qywxActivityStaffEffect = qywxActivityHeadService.getActivityStaffEffect(headId, followUserId);
        return ResponseBo.okWithData(null, qywxActivityStaffEffect);
    }


}
