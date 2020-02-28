package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.dao.ActivityProductMapper;
import com.linksteady.operate.domain.ActivityProduct;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.ActivityHeadService;
import com.linksteady.operate.service.ActivityProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2019-09-07
 */
@Slf4j
@Service
public class ActivityProductServiceImpl implements ActivityProductService {

    @Autowired
    private ShortUrlServiceImpl shortUrlService;

    @Autowired
    private ActivityProductMapper activityProductMapper;

    @Autowired
    private ActivityHeadMapper activityHeadMapper;

    @Autowired
    private ActivityHeadService activityHeadService;

    @Override
    public int getCount(String headId,String productId, String productName, String productAttr, String stage) {
        return activityProductMapper.getCount(headId, productId, productName, productAttr, stage);
    }

    @Override
    public List<ActivityProduct> getActivityProductListPage(int start, int end, String headId,String productId, String productName, String productAttr, String stage) {
        return activityProductMapper.getActivityProductListPage(start, end, headId, productId, productName, productAttr, stage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveActivityProduct(ActivityProduct activityProduct) {
        String time = String.valueOf(System.currentTimeMillis());
        // 添加商品更改数据状态
        activityHeadMapper.updateGroupChanged(time, activityProduct.getHeadId().toString(), activityProduct.getActivityStage(), "1");
        activityProductMapper.saveActivityProduct(activityProduct);

        generateProdMapping(String.valueOf(activityProduct.getHeadId()),activityProduct.getActivityStage());
    }

    @Override
    public ActivityProduct getProductById(String id) {
        return activityProductMapper.getProductById(id);
    }

    @Override
    public void updateActivityProduct(ActivityProduct activityProduct) {
        activityProductMapper.updateActivityProduct(activityProduct);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveActivityProductList(List<ActivityProduct> productList) {
        ActivityProduct activityProduct = productList.get(0);
        String time = String.valueOf(System.currentTimeMillis());
        activityHeadMapper.updateGroupChanged(time, activityProduct.getHeadId().toString(), activityProduct.getActivityStage(), "1");
        activityProductMapper.saveActivityProductList(productList);

        generateProdMapping(String.valueOf(activityProduct.getHeadId()),activityProduct.getActivityStage());
    }

    /**
     * todo 更新状态
     * 删除商品，删除完更新head表的数据状态
     * @param headId
     * @param stage
     * @param productIds
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(String headId, String stage, String productIds) {
        List<String> productList = Arrays.asList(productIds.split(","));
        String time = String.valueOf(System.currentTimeMillis());
        activityHeadMapper.updateGroupChanged(time, headId, stage, "1");
        activityProductMapper.deleteProduct(headId, stage, productList);

        generateProdMapping(headId,stage);
    }

    @Override
    public int validProductNum(String headId, String stage) {
        return activityProductMapper.validProductNum(headId, stage);
    }

    /**
     * 根据传入的productId 返回去对应的商品明细页短链接
     * @param productId
     * @return
     */
    @Override
    public String generateProductShortUrl(String productId,String sourceType) {
        return shortUrlService.genProdShortUrlByProdId(productId,sourceType);
    }

    @Override
    public int getSameProductCount(List<String> productIdList, String headId, String stage) {
        return activityProductMapper.getSameProductCount(productIdList, headId, stage);
    }

    @Override
    public void deleteRepeatData(List<ActivityProduct> productList, String headId, String stage) {
        activityProductMapper.deleteRepeatData(productList, headId, stage);
        generateProdMapping(headId,stage);
    }

    @Override
    public ActivityProduct geFirstProductInfo(String headId, String stage) {
        return activityProductMapper.geFirstProductInfo(headId,stage);
    }

    @Override
    public void insertActivityProdMapping(String headId, String stage) {
         activityProductMapper.insertActivityProdMapping(headId,stage);
    }

    @Override
    public void deleteActivityProdMapping(String headId, String stage) {
         activityProductMapper.deleteActivityProdMapping(headId,stage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteData(String headId) {
        activityProductMapper.deleteData(headId);
        //删除映射数据
        activityProductMapper.deleteActivityProdMapping(headId,"");
    }

    @Override
    public ResponseBo uploadExcel(MultipartFile file, String headId, String stage, String operateType) {
        List<String> headers = Arrays.asList("商品ID[数据类型：文本型]", "ERP货号[数据类型：文本型]", "名称[数据类型：文本型]", "最低单价（元/件）[数据类型：数值型]", "非活动售价（元/件）[数据类型：数值型]", "活动属性[主推，参活，正常][数据类型：文本型]");
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
                    try {
                        activityProduct.setProductId(row.getCell(0).getStringCellValue());
                    }catch (IllegalStateException e) {
                        throw new LinkSteadyException("\"商品ID\"数据类型与模板不一致，应改为文本型！");
                    }
                    // skuCode非必填
                    if(null != row.getCell(1)) {
                        try {
                            activityProduct.setSkuCode(row.getCell(1).getStringCellValue());
                        }catch (IllegalStateException e) {
                            throw new LinkSteadyException("\"ERP货号\"数据类型与模板不一致，应改为文本型！");
                        }
                    }
                    // 验证产品名称不超过最大长度
//                    if(row.getCell(2).getStringCellValue().length() > dailyProperties.getProdNameLen()) {
//                        throw new LinkSteadyException("商品名称长度超过系统设置！");
//                    }
                    try {
                        activityProduct.setProductName(row.getCell(2).getStringCellValue());
                    }catch (IllegalStateException e) {
                        throw new LinkSteadyException("\"名称\"数据类型与模板不一致，应改为文本型！");
                    }
                    double minPrice;
                    try {
                        minPrice = row.getCell(3).getNumericCellValue();
                    }catch (IllegalStateException e) {
                        throw new LinkSteadyException("\"最低单价\"数据类型与模板不一致，应改为数值型！");
                    }
                    double formalPrice;
                    try {
                        formalPrice = row.getCell(4).getNumericCellValue();
                    }catch (IllegalStateException e) {
                        throw new LinkSteadyException("\"非活动售价\"数据类型与模板不一致，应改为数值型！");
                    }
                    String attr = "";
                    try {
                        attr = row.getCell(5).getStringCellValue();
                    }catch (IllegalStateException e) {
                        throw new LinkSteadyException("\"活动属性\"数据类型与模板不一致，应改为文本型！");
                    }
                    double activityIntensity = minPrice/formalPrice * 100;
                    activityIntensity = Double.valueOf(String.format("%.2f", activityIntensity));
                    activityProduct.setMinPrice(minPrice);
                    activityProduct.setFormalPrice(formalPrice);
                    activityProduct.setActivityIntensity(activityIntensity);
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
                    activityProduct.setProductUrl(generateProductShortUrl(activityProduct.getProductId(),"S"));
                    productList.add(activityProduct);
                }
                if(!flag.get()) {
                    throw new LinkSteadyException("检测到上传数据与模板格式不符，请检查后重新上传！");
                }
                if(productList.size() != 0) {
                    List<String> productIdList = productList.stream().map(ActivityProduct::getProductId).collect(Collectors.toList());
                    // 获取相同productId的商品个数
                    int count = getSameProductCount(productIdList, headId, stage);
                    if(count > 0) {
                        deleteRepeatData(productList, headId, stage);
                        saveActivityProductList(productList);
                        if("update".equalsIgnoreCase(operateType)) {
                            activityHeadService.changeAndUpdateStatus(headId, stage);
                            log.info("更新短信模板,headId:{}的状态发生变更。", headId);
                        }
                        return ResponseBo.ok("当前Excel中共有" + count + "条记录与已有记录重复，旧记录已覆盖！");
                    }
                    if("update".equalsIgnoreCase(operateType)) {
                        activityHeadService.changeAndUpdateStatus(headId, stage);
                        log.info("更新短信模板,headId:{}的状态发生变更。", headId);
                    }
                    saveActivityProductList(productList);
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
     * 生成映射数据
     * @param headId
     * @param stage
     */
    private void generateProdMapping(String headId, String stage)
    {
        deleteActivityProdMapping(headId,stage);
        insertActivityProdMapping(headId,stage);
    }
}
