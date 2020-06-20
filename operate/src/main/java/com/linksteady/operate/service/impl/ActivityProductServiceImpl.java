package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.dao.ActivityProductMapper;
import com.linksteady.operate.domain.ActivityHead;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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
    public int getCount(String headId, String productId, String productName, String groupId, String activityStage, String activityType) {
        return activityProductMapper.getCount(headId, productId, productName, groupId, activityStage, activityType);
    }

    @Override
    public List<ActivityProduct> getActivityProductListPage(int limit, int offset, String headId, String productId, String productName, String groupId, String activityStage, String activityType) {
        return activityProductMapper.getActivityProductListPage(limit, offset, headId, productId, productName, groupId, activityStage, activityType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveActivityProduct(ActivityProduct activityProduct) {
        calculateProductMinPrice(activityProduct);
        activityProductMapper.saveActivityProduct(activityProduct);
    }

    /**
     * 计算商品的最低价和利益点
     *
     * @param activityProduct
     * @return
     */
    private ActivityProduct calculateProductMinPrice(ActivityProduct activityProduct) {
        Long headId = activityProduct.getHeadId();
        ActivityHead activityHead = activityHeadMapper.findById(headId);
        String platThreadHold = activityHead.getPlatThreshold();
        String platDeno = activityHead.getPlatDeno();
        String platDiscount = activityHead.getPlatDiscount();
        String groupId = activityProduct.getGroupId();
        double shop_discount = 0;
        double activityPrice = activityProduct.getActivityPrice();
        switch (groupId) {
            // 满件打折
            case "9":
                shop_discount = activityPrice * (1 - activityProduct.getDiscountSize());
                activityProduct.setActivityProfit(activityProduct.getDiscountSize());
                break;
            // 满元减钱
            case "10":
                if (activityPrice >= activityProduct.getDiscountThreadhold()) {
                    shop_discount = activityProduct.getDiscountDeno();
                } else {
                    shop_discount = activityPrice * (activityProduct.getDiscountDeno() / activityProduct.getDiscountThreadhold());
                }
                activityProduct.setActivityProfit(activityProduct.getDiscountDeno());
                break;
            // 特价秒杀
            case "11":// 预售付尾立减
            case "12":
                shop_discount = activityProduct.getDiscountAmount();
                activityProduct.setActivityProfit(activityProduct.getDiscountAmount());
                break;
            // 预售付尾立减
            case "13":
                shop_discount = 0;
                activityProduct.setActivityProfit(0D);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + groupId);
        }
        // 包含平台优惠
        double plat_discount = 0;
        if (platDiscount.equalsIgnoreCase("1")) {
            double platDiscountSize = Double.valueOf(platDeno) / Double.parseDouble(platThreadHold);
            double multiple = activityPrice / Double.parseDouble(platThreadHold);
            if (multiple < 1) {
                plat_discount = activityPrice * platDiscountSize;
            } else {
                plat_discount = Math.floor(multiple) * Double.valueOf(platDeno) + (activityPrice - Math.floor(multiple) * Double.valueOf(platThreadHold)) * platDiscountSize;
            }
        }

        // 品类优惠，仅限本次使用，后续删除
        double spu_discount = 0;
        if (activityPrice < 300) {
            spu_discount = activityPrice * (20 / 300D);
        } else {
            spu_discount = 20;
        }

        activityPrice = (activityPrice - shop_discount - plat_discount - spu_discount) < 0 ? 0 : activityPrice - shop_discount - plat_discount - spu_discount;
        activityProduct.setMinPrice(Math.round(activityPrice));
        return activityProduct;
    }

    @Override
    public ActivityProduct getProductById(String id) {
        return activityProductMapper.getProductById(id);
    }

    @Override
    public void updateActivityProduct(ActivityProduct activityProduct) {
        activityProduct = calculateProductMinPrice(activityProduct);
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
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        long time = System.currentTimeMillis();
        activityProductMapper.deleteProduct(idList);
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
    public List<ActivityProductUploadError> uploadExcel(MultipartFile file, String headId, String uploadMethod,
                                                        String repeatProduct, String stage, String activityType) throws Exception {
        String xlsSuffix = ".xls";
        String xlsxSuffix = ".xlsx";
        // 表头
        List<String> headers = Arrays.asList(
                "商品ID", "商品名称", "日常商品单价\n（元/件）", "活动商品单价\n（元/件）", "店铺活动机制", "满件打折\n（折）", "满元减钱门槛\n（元）", "满元减钱面额\n（元）", "立减金额\n（元）", "利益点适用场景"
        );
        AtomicBoolean flag = new AtomicBoolean(true);
        List<ActivityProduct> productList = Lists.newArrayList();
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        List<ActivityProductUploadError> errorList = Lists.newArrayList();
        List<ActivityProductUploadError> dataList = Lists.newArrayList();
        if (!(fileType.equalsIgnoreCase(xlsSuffix) || fileType.equalsIgnoreCase(xlsxSuffix))) {
            errorList.add(new ActivityProductUploadError("文件格式不符，只支持.xls,.xlsx后缀的文件！"));
            return errorList;
        }
        Workbook wb = null;
        try {
            InputStream is = file.getInputStream();
            if (fileType.equalsIgnoreCase(xlsSuffix)) {
                wb = new HSSFWorkbook(is);
            } else if (fileType.equalsIgnoreCase(xlsxSuffix)) {
                wb = new XSSFWorkbook(is);
            }
            Sheet sheet = wb.getSheetAt(0);
            if (null == sheet) {
                errorList.add(new ActivityProductUploadError("系统只解析第一个sheet，当前文件第一个sheet为空"));
            } else {
                for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                    int validCount = 0;
                    Row row = sheet.getRow(i);
                    //获取第一行的的数据
                    if (null == row) {
                        errorList.add(new ActivityProductUploadError("行为空", i + 1));
                        continue;
                    }
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
                    // 商品ID
                    String productId = "";
                    Cell cell0 = row.getCell(0);
                    if (null == cell0 || cell0.getCellType() == 3) {
                        validCount++;
                        errorList.add(new ActivityProductUploadError("商品ID为空", i + 1));
                    } else {
                        if (cell0.getCellType() == 1) {
                            productId = cell0.getStringCellValue();
                        } else {
                            errorList.add(new ActivityProductUploadError("商品ID数据类型有误，应改为文本型", i + 1));
                        }
                    }

                    // 商品名称
                    String productName = "";
                    Cell cell1 = row.getCell(1);
                    if (null == cell1 || cell1.getCellType() == 3) {
                        validCount++;
                        errorList.add(new ActivityProductUploadError("商品名称为空", i + 1, true));
                    } else {
                        if (cell1.getCellType() == 1) {
                            productName = cell1.getStringCellValue();
                        } else {
                            errorList.add(new ActivityProductUploadError("商品名数据类型有误，应改为文本型", i + 1));
                        }
                    }

                    // 日常商品单价
                    Double formalPrice = 0D;
                    Cell cell2 = row.getCell(2);
                    if (null == cell2 || cell2.getCellType() == 3) {
                        validCount++;
                        errorList.add(new ActivityProductUploadError("日常商品单价为空", i + 1));
                    } else {
                        if (cell2.getCellType() == 0) {
                            formalPrice = cell2.getNumericCellValue();
                        } else {
                            errorList.add(new ActivityProductUploadError("日常商品单价数据类型有误，应改为数值型", i + 1));
                        }
                    }

                    // "商品ID", "商品名称", "日常商品单价（元/件）", "活动商品单价（元/件）", "店铺活动机制", "满件打折（折）", "满元减钱门槛（元）", "满元减钱面额（元）", "立减金额（元）"
                    // 活动商品单价
                    Double activityPrice = 0D;
                    Cell cell3 = row.getCell(3);
                    if (null == cell3 || cell3.getCellType() == 3) {
                        validCount++;
                        errorList.add(new ActivityProductUploadError("活动商品单价为空", i + 1));
                    } else {
                        if (cell3.getCellType() == 0) {
                            activityPrice = cell3.getNumericCellValue();
                        } else {
                            errorList.add(new ActivityProductUploadError("活动商品单价数据类型有误，应改为数值型", i + 1));
                        }
                    }

                    // 店铺活动机制
                    String groupId = null;
                    Cell cell4 = row.getCell(4);
                    if (null == cell4 || cell4.getCellType() == 3) {
                        validCount++;
                        errorList.add(new ActivityProductUploadError("店铺活动机制为空", i + 1));
                    } else {
                        if (cell4.getCellType() == 1) {
                            String groupName = Optional.of(row.getCell(4)).map(Cell::getStringCellValue).get();
                            switch (groupName) {
                                case "满件打折":
                                    groupId = "9";
                                    break;
                                case "满元减钱":
                                    groupId = "10";
                                    break;
                                case "特价秒杀":
                                    groupId = "11";
                                    break;
                                case "预售付尾立减":
                                    groupId = "12";
                                    break;
                                case "无店铺活动":
                                    groupId = "13";
                                    break;
                                default:
                                    break;
                            }
                            if (StringUtils.isEmpty(groupId)) {
                                errorList.add(new ActivityProductUploadError("店铺活动机制值与模板给定的值不一致", i + 1));
                            }
                        } else {
                            errorList.add(new ActivityProductUploadError("店铺活动机制数据类型有误，应改为文本型", i + 1));
                        }
                    }
                    double discountSize = 0D;
                    double discountThreadhold = 0D;
                    double discountDeno = 0D;
                    double discountAmount = 0D;
                    switch (groupId) {
                        case "9":
                            // 满件打折
                            Cell cell5 = row.getCell(5);
                            if (null == cell5 || cell5.getCellType() == 3) {
                                validCount++;
                                errorList.add(new ActivityProductUploadError("满件打折为空", i + 1));
                            } else {
                                if (cell5.getCellType() == 0) {
                                    discountSize = cell5.getNumericCellValue();
                                } else {
                                    errorList.add(new ActivityProductUploadError("满件打折数据类型有误，应改为数值型", i + 1));
                                }
                            }
                            break;
                        case "10":
                            // 满元减钱
                            Cell cell6 = row.getCell(6);
                            if (null == cell6 || cell6.getCellType() == 3) {
                                validCount++;
                                errorList.add(new ActivityProductUploadError("满元减钱门槛为空", i + 1));
                            } else {
                                if (cell6.getCellType() == 0) {
                                    discountThreadhold = cell6.getNumericCellValue();
                                } else {
                                    errorList.add(new ActivityProductUploadError("满元减钱门槛数据类型有误，应改为数值型", i + 1));
                                }
                            }

                            Cell cell7 = row.getCell(7);
                            if (null == cell7 || cell7.getCellType() == 3) {
                                validCount++;
                                errorList.add(new ActivityProductUploadError("满元减钱面额为空", i + 1));
                            } else {
                                if (cell7.getCellType() == 0) {
                                    discountDeno = cell7.getNumericCellValue();
                                } else {
                                    errorList.add(new ActivityProductUploadError("满元减钱面额数据类型有误，应改为数值型", i + 1));
                                }
                            }
                            break;
                        case "11":
                        case "12":
                            // 特价秒杀 + 预售付尾立减
                            Cell cell8 = row.getCell(8);
                            if (null == cell8 || cell8.getCellType() == 3) {
                                validCount++;
                                errorList.add(new ActivityProductUploadError("立减金额为空", i + 1));
                            } else {
                                if (cell8.getCellType() == 0) {
                                    discountAmount = cell8.getNumericCellValue();
                                } else {
                                    errorList.add(new ActivityProductUploadError("立减金额数据类型有误，应改为数值型", i + 1));
                                }
                            }
                            break;
                        default:
                            break;
                    }
                    if (validCount == 6) {
                        errorList = errorList.subList(0, errorList.size() - 6);
                    }
                    ActivityProduct activityProduct = new ActivityProduct();
                    activityProduct.setHeadId(Long.valueOf(headId));
                    activityProduct.setGroupId(groupId);
                    activityProduct.setProductName(productName);
                    activityProduct.setActivityPrice(new BigDecimal(activityPrice).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    activityProduct.setFormalPrice(new BigDecimal(formalPrice).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    activityProduct.setDiscountSize(new BigDecimal(discountSize).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    activityProduct.setDiscountThreadhold(new BigDecimal(discountThreadhold).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    activityProduct.setDiscountDeno(new BigDecimal(discountDeno).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    activityProduct.setDiscountAmount(new BigDecimal(discountAmount).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    activityProduct.setProductId(productId);
                    activityProduct.setActivityStage(stage);
                    activityProduct.setActivityType(activityType);
                    if (activityProduct.productValid()) {
                        activityProduct.setProductUrl(shortUrlService.genProdShortUrlByProdId(productId, "S"));
                        activityProduct = calculateProductMinPrice(activityProduct);
                        productList.add(activityProduct);
                    }
                }
                if (productList.size() != 0) {
                    saveUploadProductData(headId, productList, uploadMethod, repeatProduct, stage, activityType);
                } else {
                    errorList.add(new ActivityProductUploadError("校验通过的记录条数为0"));
                }
            }
        } catch (IOException e) {
            log.error("上传excel失败", e);
        }
        List<ActivityProductUploadError> result = errorList.stream().filter(x -> !x.isIgnore()).collect(Collectors.toList());
        if (result.size() > 0) {
            Map<String, List<ActivityProductUploadError>> collect = result.stream().collect(Collectors.groupingBy(ActivityProductUploadError::getErrorDesc));
            collect.entrySet().stream().forEach(x -> {
                long rows = x.getValue().stream().map(ActivityProductUploadError::getErrorRows).count();
                Integer first = x.getValue().stream().map(ActivityProductUploadError::getFirstErrorRow).min(Integer::compare).get();
                dataList.add(new ActivityProductUploadError(x.getKey(), first, Long.valueOf(rows).intValue()));
            });
        }
        return dataList.stream().sorted(Comparator.comparing(ActivityProductUploadError::getFirstErrorRow)).collect(Collectors.toList());
    }


    /**
     * 验证上传商品信息
     *
     * @param headId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void validProductInfo(String headId, String stage) {
        activityProductMapper.updateAllValidInfo(headId, stage);
        activityProductMapper.updateValidInfo(headId, stage);
    }

    @Override
    public int getCountByHeadId(String headId) {
        return activityProductMapper.getCountByHeadId(headId);
    }

    /**
     * 判断当前
     * @param headId
     * @param stage
     * @return
     */
    @Override
    public int validProduct(String headId, String stage) {
        ActivityHead activityHead = activityHeadMapper.findById(Long.valueOf(headId));
        if(stage.equalsIgnoreCase("preheat")) {
            if(activityHead.getPreheatNotifyStatus().equalsIgnoreCase("edit")) {
                return activityProductMapper.validProduct(headId, stage);
            }else {
                return activityProductMapper.validProductOfDuring(headId, stage);
            }
        }
        if(stage.equalsIgnoreCase("formal")) {
            if(activityHead.getFormalNotifyStatus().equalsIgnoreCase("edit")) {
                return activityProductMapper.validProduct(headId, stage);
            }else {
                return activityProductMapper.validProductOfDuring(headId, stage);
            }
        }
        return -1;
    }

    @Override
    public List<String> getGroupIds(Long headId) {
        return activityProductMapper.getGroupIds(headId);
    }

    @Override
    public boolean checkProductId(String headId, String activityType, String activityStage, String productId) {
        return activityProductMapper.checkProductId(headId, activityType, activityStage, productId) == 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncNotifyProduct(String headId, String activityStage) {
        activityProductMapper.deleteDataOfDuring(headId, activityStage);
        activityProductMapper.syncNotifyProduct(headId, activityStage);
    }

    /**
     * 保存上传的数据
     */
    private boolean saveUploadProductData(String headId, List<ActivityProduct> productList, String uploadMethod, String repeatProduct, String stage, String activityType) {
        String uploadMethod0 = "0";
        String repeatProduct0 = "0";
        String repeatProduct1 = "1";
        String uploadMethod1 = "1";
        List<ActivityProduct> insertList = Lists.newArrayList();
        List<ActivityProduct> deleteList = Lists.newArrayList();
        List<String> oldProductList = activityProductMapper.getProductIdByHeadId(headId, stage, activityType);
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
            if (deleteList.size() > 0) {
                List<String> productIdList = deleteList.stream().map(ActivityProduct::getProductId).collect(Collectors.toList());
                activityProductMapper.deleteDataList(headId, productIdList);
            }
            if (insertList.size() > 0) {
                activityProductMapper.saveActivityProductList(insertList);
            }
        } catch (Exception e) {
            log.error("上传商品数据发生DB错误:", e);
            return false;
        }
        return true;
    }

    /**
     * 上传的商品数据中含有重复数据的去重
     *
     * @param dataList
     * @param override
     * @return
     */
    private List<ActivityProduct> removeRepeat(List<ActivityProduct> dataList, boolean override) {
        List<ActivityProduct> newList = dataList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ActivityProduct::getProductId))), ArrayList::new));
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

    private static String getCellTypeName(int typeCode) {
        if (typeCode == 0) {
            return "数值型";
        } else if (typeCode == 1) {
            return "文本型";
        } else if (typeCode == 2) {
            return "公式";
        } else if (typeCode == 3) {
            return "空单元格，但有样式";
        } else if (typeCode == 4) {
            return "布尔类型";
        } else {
            return "错误类型";
        }
    }


}
