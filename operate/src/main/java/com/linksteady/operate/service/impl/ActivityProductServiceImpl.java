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
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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

    @Override
    public int getCount(String headId, String productId, String productName, String groupId) {
        return activityProductMapper.getCount(headId, productId, productName, groupId);
    }

    @Override
    public List<ActivityProduct> getActivityProductListPage(int start, int end, String headId, String productId, String productName, String groupId) {
        return activityProductMapper.getActivityProductListPage(start, end, headId, productId, productName, groupId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveActivityProduct(ActivityProduct activityProduct) {
        activityProductMapper.saveActivityProduct(activityProduct);
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
        activityProductMapper.saveActivityProductList(productList);
    }

    /**
     * todo 更新状态
     * 删除商品，删除完更新head表的数据状态
     *
     * @param headId
     * @param stage
     * @param productIds
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long headId, String stage, String productIds) {
        List<String> productList = Arrays.asList(productIds.split(","));
        long time = System.currentTimeMillis();
        activityHeadMapper.updateGroupChanged(time, headId, stage, "1");
        activityProductMapper.deleteProduct(headId, stage, productList);
    }

    @Override
    public int validProductNum(Long headId, String stage) {
        return activityProductMapper.validProductNum(headId, stage);
    }

    /**
     * 根据传入的productId 返回去对应的商品明细页短链接
     *
     * @param productId
     * @return
     */
    @Override
    public String generateProductShortUrl(String productId, String sourceType) {
        return shortUrlService.genProdShortUrlByProdId(productId, sourceType);
    }

    @Override
    public int getSameProductCount(List<String> productIdList, Long headId, String stage) {
        return activityProductMapper.getSameProductCount(productIdList, headId, stage);
    }

    @Override
    public void deleteRepeatData(List<ActivityProduct> productList, Long headId, String stage) {
        activityProductMapper.deleteRepeatData(productList, headId, stage);
    }

    @Override
    public ActivityProduct geFirstProductInfo(Long headId, String stage) {
        return activityProductMapper.geFirstProductInfo(headId, stage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteData(Long headId) {
        activityProductMapper.deleteData(headId);
    }

    /**
     * 文件上传
     *
     * @param file
     * @param headId
     * @param uploadMethod  0追加，1覆盖
     * @param repeatProduct 0忽略 1覆盖
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadExcel(MultipartFile file, String headId, String uploadMethod, String repeatProduct) throws Exception {
        String xlsSuffix = ".xls";
        String xlsxSuffix = ".xlsx";
        // 表头
        List<String> headers = Arrays.asList(
                "商品ID<文本型>", "名称<文本型>", "非活动日常单价（元/件）<数值型>", "活动机制<文本型>", "活动通知体现最低单价（元/件）<数值型>", "活动期间体现最低单价（元/件）<数值型>"
        );
        AtomicBoolean flag = new AtomicBoolean(true);
        List<ActivityProduct> productList = Lists.newArrayList();
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (fileType.equalsIgnoreCase(xlsSuffix) || fileType.equalsIgnoreCase(xlsxSuffix)) {
            InputStream is;
            Workbook wb = null;
            try {
                is = file.getInputStream();
                if (fileType.equalsIgnoreCase(xlsSuffix)) {
                    wb = new HSSFWorkbook(is);
                } else if (fileType.equalsIgnoreCase(xlsxSuffix)) {
                    wb = new XSSFWorkbook(is);
                }
                Sheet sheet = wb.getSheetAt(0);
                for (Row row : sheet) {
                    if (row.getCell(0).getRowIndex() == 0) {
                        Iterator<Cell> cellIterator = row.cellIterator();
                        cellIterator.forEachRemaining(x -> {
                            // 可能会出现空列的情况
                            if (!x.getStringCellValue().equalsIgnoreCase("")) {
                                if (headers.indexOf(x.getStringCellValue()) == -1) {
                                    flag.set(false);
                                }
                            }
                        });
                        if (!flag.get()) {
                            break;
                        } else {
                            continue;
                        }
                    }
                    ActivityProduct activityProduct = new ActivityProduct();
                    activityProduct.setHeadId(Long.valueOf(headId));
                    try {
                        activityProduct.setProductId(row.getCell(0).getStringCellValue());
                    } catch (IllegalStateException e) {
                        throw new LinkSteadyException("\"商品ID\"数据类型与模板不一致，应改为文本型！");
                    }
                    try {
                        activityProduct.setProductName(row.getCell(1).getStringCellValue());
                    } catch (IllegalStateException e) {
                        throw new LinkSteadyException("\"名称\"数据类型与模板不一致，应改为文本型！");
                    }
                    try {
                        activityProduct.setFormalPrice(row.getCell(2).getNumericCellValue());
                    } catch (IllegalStateException e) {
                        throw new LinkSteadyException("\"非活动日常单价\"数据类型与模板不一致，应改为数值型！");
                    }
                    try {
                        String groupId = "";
                        String groupName = row.getCell(3).getStringCellValue();
                        switch (groupName) {
                            case "活动价":
                                groupId = "1";
                                break;
                            case "满件打折":
                                groupId = "2";
                                break;
                            case "满元减钱":
                                groupId = "3";
                                break;
                            case "特价":
                                groupId = "4";
                                break;
                            default:
                                break;
                        }
                        if (StringUtils.isEmpty(groupId)) {
                            throw new LinkSteadyException("\"活动机制\"数据应为下拉项中某一项！");
                        }
                        activityProduct.setGroupId(groupId);
                    } catch (IllegalStateException e) {
                        throw new LinkSteadyException("\"活动机制\"数据类型与模板不一致，应改为数值型！");
                    }
                    try {
                        activityProduct.setNotifyMinPrice(row.getCell(3).getNumericCellValue());
                    } catch (IllegalStateException e) {
                        throw new LinkSteadyException("\"活动通知体现最低单价\"数据类型与模板不一致，应改为数值型！");
                    }
                    try {
                        activityProduct.setMinPrice(row.getCell(4).getNumericCellValue());
                    } catch (IllegalStateException e) {
                        throw new LinkSteadyException("\"活动期间体现最低单价\"数据类型与模板不一致，应改为数值型！");
                    }
                    activityProduct.setProductUrl(generateProductShortUrl(activityProduct.getProductId(), "S"));
                    productList.add(activityProduct);
                }
                if (!flag.get()) {
                    throw new LinkSteadyException("检测到上传数据与模板格式不符，请检查后重新上传！");
                }
                if (productList.size() != 0) {
                    boolean unexpectedRollback = saveUploadProductData(headId, productList, uploadMethod, repeatProduct);
                    if (unexpectedRollback) {
                        throw new UnexpectedRollbackException(
                                "Transaction rolled back because it has been marked as rollback-only");
                    }
                } else {
                    throw new LinkSteadyException("上传的数据为空！");
                }
            } catch (IOException e) {
                log.error("上传excel失败", e);
                throw new LinkSteadyException("上传excel失败，请检查!");
            }
        } else {
            throw new LinkSteadyException("只支持.xls,.xlsx后缀的文件！");
        }
    }

    /**
     * 保存上传的数据
     */
    private boolean saveUploadProductData(String headId, List<ActivityProduct> productList, String uploadMethod, String repeatProduct) throws Exception {
        String uploadMethod0 = "0";
        String repeatProduct0 = "0";
        String repeatProduct1 = "1";
        String uploadMethod1 = "1";
        List<ActivityProduct> insertList = Lists.newArrayList();
        List<ActivityProduct> deleteList = Lists.newArrayList();
        List<String> oldProductList = activityProductMapper.getProductIdByHeadId(headId);
        // 追加
        if (uploadMethod.equalsIgnoreCase(uploadMethod0)) {
            // 忽略
            if (repeatProduct.equalsIgnoreCase(repeatProduct0)) {
                productList = removeRepeat(productList, false);
                insertList = productList.stream().filter(x -> !oldProductList.contains(x.getProductId())).collect(Collectors.toList());
            } else if (repeatProduct.equalsIgnoreCase(repeatProduct1)) {
                // 覆盖
                insertList = removeRepeat(productList, true);
                deleteList = productList.stream().filter(x -> oldProductList.contains(x.getProductId())).collect(Collectors.toList());
            }
            // 覆盖
        } else if (uploadMethod.equalsIgnoreCase(uploadMethod1)) {
            // 忽略
            if (repeatProduct.equalsIgnoreCase(repeatProduct0)) {
                productList = removeRepeat(productList, true);
                insertList = productList.stream().filter(x -> !oldProductList.contains(x.getProductId())).collect(Collectors.toList());
            } else if (repeatProduct.equalsIgnoreCase(repeatProduct1)) {
                // 覆盖
                insertList = removeRepeat(productList, false);
                deleteList = productList.stream().filter(x -> oldProductList.contains(x.getProductId())).collect(Collectors.toList());
            }
        }
        try {
            if(deleteList.size() > 0) {
                List<String> productIdList = deleteList.stream().map(ActivityProduct::getProductId).collect(Collectors.toList());
                activityProductMapper.deleteDataList(headId, productIdList);
            }
            if(insertList.size() > 0) {
                activityProductMapper.saveActivityProductList(insertList);
            }
        }catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 上传的商品数据中含有重复数据的去重
     * @param dataList
     * @param override
     * @return
     */
    private List<ActivityProduct> removeRepeat(List<ActivityProduct> dataList, boolean override) {
        List<ActivityProduct> newList = dataList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ActivityProduct :: getProductId))), ArrayList::new));
        if (dataList.size() == newList.size()) {
            return dataList;
        }else {
            if(override) {
                newList.forEach(x->{
                    List<ActivityProduct> tmpList = dataList.stream().filter(y -> y.getProductId().equalsIgnoreCase(x.getProductId())).collect(Collectors.toList());
                    dataList.remove(tmpList.get(tmpList.size()-1));
                });
            }else {
                newList.forEach(x->{
                    List<ActivityProduct> tmpList = dataList.stream().filter(y -> y.getProductId().equalsIgnoreCase(x.getProductId())).collect(Collectors.toList());
                    dataList.remove(tmpList.get(0));
                });
            }
            return dataList;
        }
    }
}
