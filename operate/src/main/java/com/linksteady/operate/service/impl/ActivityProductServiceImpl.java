package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.dao.ActivityProductMapper;
import com.linksteady.operate.domain.ActivityProduct;
import com.linksteady.operate.domain.ActivityProductUploadError;
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
import org.springframework.transaction.annotation.Propagation;
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
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public List<ActivityProductUploadError> uploadExcel(MultipartFile file, String headId, String uploadMethod, String repeatProduct) throws Exception {
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
        List<ActivityProductUploadError> errorList = Lists.newArrayList();
        List<ActivityProductUploadError> dataList = Lists.newArrayList();
        if (fileType.equalsIgnoreCase(xlsSuffix) || fileType.equalsIgnoreCase(xlsxSuffix)) {
            Workbook wb = null;
            try {
                InputStream is = file.getInputStream();
                if (fileType.equalsIgnoreCase(xlsSuffix)) {
                    wb = new HSSFWorkbook(is);
                } else if (fileType.equalsIgnoreCase(xlsxSuffix)) {
                    wb = new XSSFWorkbook(is);
                }
                Sheet sheet = wb.getSheetAt(0);
                if(null == sheet) {
                    errorList.add(new ActivityProductUploadError("系统只解析第一个sheet，当前文件第一个sheet为空"));
                }else {
                    for (int i=0; i<sheet.getLastRowNum(); i++) {
                        Row row = sheet.getRow(i);
                        // 校验文件第一行与模板是否一致
                        if (i == 0) {
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
                                errorList.add(new ActivityProductUploadError("检测到上传数据与模板格式不符，请检查后重新上传"));
                                break;
                            } else {
                                continue;
                            }
                        }
                        // 检查>=第二行数据
                        ActivityProduct activityProduct = new ActivityProduct();
                        activityProduct.setHeadId(Long.valueOf(headId));

                        // 商品ID
                        ActivityProductUploadError idError = new ActivityProductUploadError();
                        Optional<Cell> idOptional = Optional.ofNullable(row.getCell(0));
                        if(idOptional.isPresent()) {
                            try {
                                activityProduct.setProductId(row.getCell(0).getStringCellValue());
                            } catch (IllegalStateException e) {
                                idError.setErrorDesc("商品ID数据类型与模板不一致，应改为文本型！");
                                int errorRows = idError.getErrorRows();
                                idError.setErrorRows(++errorRows);
                                if(idError.getFirstErrorRow() == 0) {
                                    idError.setFirstErrorRow(i+1);
                                }
                                errorList.add(idError);
                            }
                        }else {
                            idError.setErrorDesc("商品ID为空");
                            int errorRows = idError.getErrorRows();
                            idError.setErrorRows(++errorRows);
                            if(idError.getFirstErrorRow() == 0) {
                                idError.setFirstErrorRow(i+1);
                            }
                            errorList.add(idError);
                        }

                        // 商品名称
                        ActivityProductUploadError nameError = new ActivityProductUploadError();
                        Optional<Cell> nameOptional = Optional.ofNullable(row.getCell(1));
                        if(nameOptional.isPresent()) {
                            try {
                                Optional.ofNullable(row.getCell(1)).map(Cell::getStringCellValue).ifPresent(activityProduct::setProductName);
                            } catch (IllegalStateException e) {
                                nameError.setErrorDesc("名称数据类型与模板不一致，应改为文本型！");
                                int errorRows = nameError.getErrorRows();
                                nameError.setErrorRows(++errorRows);
                                if(nameError.getFirstErrorRow() == 0) {
                                    nameError.setFirstErrorRow(i+1);
                                }
                                errorList.add(nameError);
                            }
                        }else {
                            nameError.setErrorDesc("名称为空");
                            int errorRows = nameError.getErrorRows();
                            nameError.setErrorRows(++errorRows);
                            if(nameError.getFirstErrorRow() == 0) {
                                nameError.setFirstErrorRow(i+1);
                            }
                            nameError.setIgnore(true);
                            errorList.add(nameError);
                        }

                        // 非活动日常单价
                        ActivityProductUploadError formalPriceError = new ActivityProductUploadError();
                        Optional<Cell> formalPriceOptional = Optional.ofNullable(row.getCell(2));
                        if(formalPriceOptional.isPresent() && formalPriceOptional.get().getNumericCellValue() != 0D) {
                            try {
                                Optional.of(row.getCell(2)).map(Cell::getNumericCellValue).ifPresent(activityProduct::setFormalPrice);
                            } catch (IllegalStateException e) {
                                formalPriceError.setErrorDesc("非活动日常单价数据类型与模板不一致，应改为数值型！");
                                int errorRows = formalPriceError.getErrorRows();
                                formalPriceError.setErrorRows(++errorRows);
                                if(formalPriceError.getFirstErrorRow() == 0) {
                                    formalPriceError.setFirstErrorRow(i+1);
                                }
                                errorList.add(formalPriceError);
                            }
                        }else {
                            formalPriceError.setErrorDesc("非活动日常单价为空");
                            int errorRows = formalPriceError.getErrorRows();
                            formalPriceError.setErrorRows(++errorRows);
                            if(formalPriceError.getFirstErrorRow() == 0) {
                                formalPriceError.setFirstErrorRow(i+1);
                            }
                            errorList.add(formalPriceError);
                        }

                        // 活动机制
                        ActivityProductUploadError groupIdError = new ActivityProductUploadError();
                        Optional<Cell> groupIdOptional = Optional.ofNullable(row.getCell(3));
                        if(groupIdOptional.isPresent()) {
                            try {
                                String groupId = null;
                                String groupName = Optional.of(row.getCell(3)).map(Cell::getStringCellValue).get();
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
                                    groupIdError.setErrorDesc("活动机制值与模板给定的值不一致");
                                    int errorRows = formalPriceError.getErrorRows();
                                    groupIdError.setErrorRows(++errorRows);
                                    if(groupIdError.getFirstErrorRow() == 0) {
                                        groupIdError.setFirstErrorRow(i+1);
                                    }
                                    errorList.add(groupIdError);
                                }else {
                                    activityProduct.setGroupId(groupId);
                                }
                            } catch (IllegalStateException e) {
                                groupIdError.setErrorDesc("活动机制数据类型与模板不一致，应改为文本型");
                                int errorRows = groupIdError.getErrorRows();
                                groupIdError.setErrorRows(++errorRows);
                                if(groupIdError.getFirstErrorRow() == 0) {
                                    groupIdError.setFirstErrorRow(i+1);
                                }
                                errorList.add(groupIdError);
                            }
                        }else {
                            groupIdError.setErrorDesc("活动机制为空");
                            int errorRows = groupIdError.getErrorRows();
                            groupIdError.setErrorRows(++errorRows);
                            if(groupIdError.getFirstErrorRow() == 0) {
                                groupIdError.setFirstErrorRow(i+1);
                            }
                            errorList.add(groupIdError);
                        }

                        // 活动通知体现最低单价
                        ActivityProductUploadError notifyMinPriceError = new ActivityProductUploadError();
                        Optional<Cell> notifyMinPriceOptional = Optional.ofNullable(row.getCell(4));
                        if(notifyMinPriceOptional.isPresent()  && notifyMinPriceOptional.get().getNumericCellValue() != 0D) {
                            try {
                                Optional.of(row.getCell(4)).map(Cell::getNumericCellValue).ifPresent(activityProduct::setNotifyMinPrice);
                            } catch (IllegalStateException e) {
                                notifyMinPriceError.setErrorDesc("活动通知体现最低单价数据类型与模板不一致，应改为数值型！");
                                int errorRows = notifyMinPriceError.getErrorRows();
                                notifyMinPriceError.setErrorRows(++errorRows);
                                if(notifyMinPriceError.getFirstErrorRow() == 0) {
                                    notifyMinPriceError.setFirstErrorRow(i+1);
                                }
                                errorList.add(notifyMinPriceError);
                            }
                        }else {
                            notifyMinPriceError.setErrorDesc("活动通知体现最低单价为空");
                            int errorRows = notifyMinPriceError.getErrorRows();
                            notifyMinPriceError.setErrorRows(++errorRows);
                            if(notifyMinPriceError.getFirstErrorRow() == 0) {
                                notifyMinPriceError.setFirstErrorRow(i+1);
                            }
                            errorList.add(notifyMinPriceError);
                        }

                        // 活动期间体现最低单价
                        ActivityProductUploadError minPriceError = new ActivityProductUploadError();
                        Optional<Cell> minPriceOptional = Optional.ofNullable(row.getCell(5));
                        if(minPriceOptional.isPresent() && minPriceOptional.get().getNumericCellValue() != 0D) {
                            try {
                                Optional.of(row.getCell(5)).map(Cell::getNumericCellValue).ifPresent(activityProduct::setMinPrice);
                            } catch (IllegalStateException e) {
                                minPriceError.setErrorDesc("活动期间体现最低单价数据类型与模板不一致，应改为数值型！");
                                int errorRows = minPriceError.getErrorRows();
                                minPriceError.setErrorRows(++errorRows);
                                if(minPriceError.getFirstErrorRow() == 0) {
                                    minPriceError.setFirstErrorRow(i+1);
                                }
                                errorList.add(minPriceError);
                            }
                        }else {
                            minPriceError.setErrorDesc("活动期间体现最低单价为空");
                            int errorRows = minPriceError.getErrorRows();
                            minPriceError.setErrorRows(++errorRows);
                            if(minPriceError.getFirstErrorRow() == 0) {
                                minPriceError.setFirstErrorRow(i+1);
                            }
                            errorList.add(minPriceError);
                        }
                        if(activityProduct.productValid()) {
                            activityProduct.setProductUrl(generateProductShortUrl(activityProduct.getProductId(), "S"));
                            productList.add(activityProduct);
                        }
                    }
                    if (productList.size() != 0) {
                        if(errorList.stream().filter(x -> !x.isIgnore()).count() == 0) {
                            saveUploadProductData(headId, productList, uploadMethod, repeatProduct);
                        }
                    } else {
                        errorList.add(new ActivityProductUploadError("上传的数据为空"));
                    }
                }
            } catch (IOException e) {
                log.error("上传excel失败", e);
            }
        } else {
            errorList.add(new ActivityProductUploadError("文件格式不符，只支持.xls,.xlsx后缀的文件！"));
        }
        List<ActivityProductUploadError> result = errorList.stream().filter(x -> !x.isIgnore()).collect(Collectors.toList());
        if(result.size() > 0) {
            Map<String, List<ActivityProductUploadError>> collect = result.stream().collect(Collectors.groupingBy(ActivityProductUploadError::getErrorDesc));
            collect.entrySet().stream().forEach(x->{
                long rows = x.getValue().stream().map(ActivityProductUploadError::getErrorRows).count();
                Integer first = x.getValue().stream().map(ActivityProductUploadError::getFirstErrorRow).min(Integer::compare).get();
                dataList.add(new ActivityProductUploadError(x.getKey(), Long.valueOf(rows).intValue(), first));
            });
        }
        return dataList;
    }


    /**
     * 验证上传商品信息
     * @param headId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void validProductInfo(String headId) {
        activityProductMapper.updateAllValidInfo(headId);
        activityProductMapper.updateValidInfo(headId);
    }

    @Override
    public int getCountByHeadId(String headId) {
        return activityProductMapper.getCountByHeadId(headId);
    }

    @Override
    public int validProduct(String headId) {
        return activityProductMapper.validProduct(headId);
    }

    @Override
    public List<String> getGroupIds(Long headId) {
        return activityProductMapper.getGroupIds(headId);
    }

    /**
     * 保存上传的数据
     */
    private boolean saveUploadProductData(String headId, List<ActivityProduct> productList, String uploadMethod, String repeatProduct) {
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
                productList = removeRepeat(productList, true);
                insertList = productList;
                deleteList = productList.stream().filter(x -> oldProductList.contains(x.getProductId())).collect(Collectors.toList());
            }
            // 覆盖
        } else if (uploadMethod.equalsIgnoreCase(uploadMethod1)) {
            // 忽略
            if (repeatProduct.equalsIgnoreCase(repeatProduct0)) {
                productList = removeRepeat(productList, false);
                deleteList = productList.stream().filter(x -> oldProductList.contains(x.getProductId())).collect(Collectors.toList());
                insertList = productList;
            } else if (repeatProduct.equalsIgnoreCase(repeatProduct1)) {
                // 覆盖
                productList = removeRepeat(productList, true);
                insertList = productList;
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
            log.error("上传商品数据发生DB错误:",e);
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
        if (dataList.size() != newList.size()) {
            List<String> repeatProductIds = dataList.stream().filter(x -> !newList.contains(x)).map(ActivityProduct::getProductId).distinct().collect(Collectors.toList());
            repeatProductIds.forEach(x -> {
                List<ActivityProduct> tmpList = dataList.stream().filter(y -> x.equalsIgnoreCase(y.getProductId())).collect(Collectors.toList());
                int skipIdx = override ? tmpList.size() - 1 : 0;
                tmpList.remove(skipIdx);
                dataList.removeAll(tmpList);
            });
        }
        return dataList;
    }
}
