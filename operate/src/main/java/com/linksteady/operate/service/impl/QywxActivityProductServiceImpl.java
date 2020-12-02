package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.operate.dao.ActivityHeadMapper;
import com.linksteady.operate.dao.ActivityProductMapper;
import com.linksteady.operate.dao.QywxActivityHeadMapper;
import com.linksteady.operate.dao.QywxActivityProductMapper;
import com.linksteady.operate.domain.ActivityCoupon;
import com.linksteady.operate.domain.ActivityProduct;
import com.linksteady.operate.domain.ActivityProductUploadError;
import com.linksteady.operate.service.ActivityProductService;
import com.linksteady.operate.service.QywxActivityProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.record.PageBreakRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hxcao
 * @date 2019-09-07
 */
@Slf4j
@Service
public class QywxActivityProductServiceImpl implements QywxActivityProductService {

    @Autowired
    private ShortUrlServiceImpl shortUrlService;

    @Autowired
    private QywxActivityProductMapper activityProductMapper;

    @Autowired
    private QywxActivityHeadMapper activityHeadMapper;

    @Override
    public int getCount(String headId, String productId, String productName, String groupId,String activityType) {
        return activityProductMapper.getCount(headId, productId, productName, groupId,activityType);
    }

    @Override
    public List<ActivityProduct> getActivityProductListPage(int limit, int offset, String headId, String productId, String productName, String groupId,String activityType) {
        return activityProductMapper.getActivityProductListPage(limit, offset, headId, productId, productName, groupId,activityType);
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
    private ActivityProduct calculateProductMinPrice(ActivityProduct activityProduct){
        //商品的活动价
        double activityPrice = activityProduct.getActivityPrice() == null ? 0 : activityProduct.getActivityPrice();
        //获取活动的信息
        Long headId = activityProduct.getHeadId();
        List<ActivityCoupon> couponList = activityHeadMapper.getActivityCouponList(headId);
        //判断是否设置单品券,设置单品券信息
        String spCouponFlag = activityProduct.getSpCouponFlag();
        Double spCouponThreshold = activityProduct.getSpCouponThreshold();
        Double spCouponDenom = activityProduct.getSpCouponDenom();
        String groupId="0";
        double lowestPrice=0;//最低价
        double spCouponPrice = 0;//单品券免减金额
        double shopExemptionAmount=0;//店铺券下免减金额
        double spExemptionAmount=0;//单品应享受的免减金额

        //有单品券
        if(StringUtils.isNotEmpty(spCouponFlag) && spCouponFlag.equalsIgnoreCase("1")){
            // 活动价小于单品券门槛，免减金额=商品活动价*（券免减金额/券门槛）
            if(activityPrice<spCouponThreshold){
                spCouponPrice=activityPrice*(spCouponDenom/spCouponThreshold);
            }else {
                // 活动价大于单品券门槛   免减金额=单品券免减金额
                spCouponPrice=spCouponDenom;
            }
        }else{
            //a.各单品优惠应享受的免减金额（各机制对应各商品）
            groupId = activityProduct.getGroupId();
            double discountAmount = activityProduct.getDiscountAmount() == null ? 0 : activityProduct.getDiscountAmount();
            double discountSize = activityProduct.getDiscountSize() == null ? 0 : activityProduct.getDiscountSize();
            discountSize = discountSize < 1 ? discountSize : (discountSize > 10 ? (discountSize / 100) : (discountSize / 10));
            switch (groupId) {
                case "9"://满件打折：免减金额=商品活动价*（1-几几折）
                    spExemptionAmount = activityPrice * (1 - discountSize);
                    break;
                case "14"://立减特价：免减金额为立减金额
                    spExemptionAmount = discountAmount;
                    break;
                case "13"://仅店铺券：免减金额为0；
                    spExemptionAmount = 0;
                default:
                    break;
            }

            //b.店铺券下的满减金额（针对所有商品）
            List<ActivityCoupon> shopCouponList = couponList.stream().filter(x -> x.getCouponType().equalsIgnoreCase("S")).collect(Collectors.toList());//获取活动设置的优惠券信息
            ActivityCoupon tmp = shopCouponList.get(0);
            double couponDenom = Double.parseDouble(tmp.getCouponDenom());//最低优惠券券的免减金额
            double couponThreshold =  Double.parseDouble(tmp.getCouponThreshold());//最低店铺优惠券门槛
            if(activityPrice<couponThreshold){
                //免减金额=商品活动价*（门槛最低的券的免减金额/门槛最低的券门槛）
                shopExemptionAmount=activityPrice*(couponDenom/couponThreshold);
            }else{
                //商品活动价大于最低门槛时，找距离商品活动价最近的且小于商品活动价的店铺券门槛，
                for (int i = shopCouponList.size()-1; i >-1; i--) {
                    if(Double.parseDouble(shopCouponList.get(i).getCouponThreshold())<activityPrice){
                        couponDenom=Double.parseDouble(shopCouponList.get(i).getCouponDenom());
                        break;
                    }
                }
                shopExemptionAmount=couponDenom;
            }
        }

        //单品券为有时，最低价=活动价-单品券免减金额
        if(StringUtils.isNotEmpty(spCouponFlag) && spCouponFlag.equalsIgnoreCase("1")){
            lowestPrice=(activityPrice-spCouponPrice)>0?(activityPrice-spCouponPrice):0;
        }else{
            //单品券为无时，最低价=活动价-单品享受的免减金额-店铺券下的满减金额
            lowestPrice=(activityPrice-spExemptionAmount-shopExemptionAmount)>0?(activityPrice-spExemptionAmount-shopExemptionAmount):0;
        }
        //设置活动通知体现最低单价
        activityProduct.setMinPrice(Math.round(lowestPrice));

        //设置利益体现点
        if(StringUtils.isNotEmpty(spCouponFlag) && spCouponFlag.equalsIgnoreCase("1")){
            activityProduct.setGroupId("14");
            activityProduct.setActivityProfit(Math.ceil(spCouponPrice));
        }else{
            if("9".equals(groupId)){
                //满减打折
                activityProduct.setActivityProfit(activityProduct.getDiscountSize());
            }else if("14".equals(groupId)){//立减特价
                activityProduct.setActivityProfit(Math.ceil(spExemptionAmount));
            }else{//仅店铺券
                activityProduct.setActivityProfit(Math.ceil(shopExemptionAmount));
            }
        }
        return  activityProduct;
    }


    /**
     * 计算商品利益点（旧版，已废弃）
     * @param activityProduct
     * @return
     */
    private ActivityProduct calculateProductMinPrice1(ActivityProduct activityProduct) {
        Long headId = activityProduct.getHeadId();
        List<ActivityCoupon> couponList = activityHeadMapper.getActivityCouponList(headId);

        double preferAmount1 = 0;
        double preferAmount2 = 0;
        double preferAmount3_1 = 0;
        double preferAmount3_2 = 0;
        double activityPrice = activityProduct.getActivityPrice() == null ? 0 : activityProduct.getActivityPrice();
        double discountAmount = activityProduct.getDiscountAmount() == null ? 0 : activityProduct.getDiscountAmount();
        // 计算商品优惠
        String groupId = activityProduct.getGroupId();
        double discountSize = activityProduct.getDiscountSize() == null ? 0 : activityProduct.getDiscountSize();
        discountSize = discountSize < 1 ? discountSize : (discountSize > 10 ? (discountSize / 100) : (discountSize / 10));
        switch (groupId) {
            case "9":
                preferAmount1 = activityPrice * (1 - discountSize);
                break;
            case "14":
                preferAmount1 = discountAmount;
                break;
            case "13":
                preferAmount1 = 0;
            default:
                break;
        }

        String spCouponFlag = activityProduct.getSpCouponFlag();
        Double spCouponThreshold = activityProduct.getSpCouponThreshold();
        Double spCouponDenom = activityProduct.getSpCouponDenom();
        // 店铺优惠:叠加
        List<ActivityCoupon> shopCouponList = couponList.stream().filter(x -> x.getCouponType().equalsIgnoreCase("S")).collect(Collectors.toList());//获取活动设置的优惠券信息
        long shopAddFlagY = shopCouponList.stream().filter(x -> x.getAddFlag().equalsIgnoreCase("1")).count();//addflag  是否叠加
        long shopAddFlagN = shopCouponList.stream().filter(x -> x.getAddFlag().equalsIgnoreCase("0")).count();
        if (shopAddFlagY > 0 && shopAddFlagN > 0) {
            throw new IllegalArgumentException("店铺优惠不能同时存在叠加券和非叠加券！");
        }
        // 店铺优惠:不叠加
        if (shopAddFlagN > 0) {
            // 有单品券
            if (StringUtils.isNotEmpty(spCouponFlag) && spCouponFlag.equalsIgnoreCase("1")) {
                if (activityPrice >= spCouponThreshold) {
                    preferAmount2 = spCouponDenom;
                } else {
                    preferAmount2 = spCouponDenom / Math.ceil(spCouponThreshold / activityPrice);
                }
            } else {
                // 没有单品
                // 如果是满件打折，活动价*N
                double finalActivityPrice;
                Integer discountCnt = activityProduct.getDiscountCnt();
                if (groupId.equalsIgnoreCase("9")) {
                    finalActivityPrice = activityPrice * discountCnt;
                } else {
                    finalActivityPrice = activityPrice;
                }

                Map<String, ActivityCoupon> tmpThreshold = Maps.newHashMap();
                couponList.stream().filter(x -> x.getCouponType().equalsIgnoreCase("S"))
                        .sorted(Comparator.comparingDouble(v -> Double.parseDouble(v.getCouponThreshold())))
                        .forEach(v -> {
                            if (finalActivityPrice >= Double.parseDouble(v.getCouponThreshold())) {
                                tmpThreshold.put("tmp", v);
                            }
                        });
                ActivityCoupon tmp = tmpThreshold.get("tmp");
                if (tmp == null) {
                    tmp = couponList.stream().filter(x -> x.getCouponType().equalsIgnoreCase("S"))
                            .sorted(Comparator.comparingDouble(v -> Double.parseDouble(v.getCouponThreshold()))).findFirst().orElse(null);
                    preferAmount2 = activityPrice * (Double.parseDouble(tmp.getCouponDenom()) / Double.parseDouble(tmp.getCouponThreshold()));
                } else {
                    preferAmount2 = Double.parseDouble(tmp.getCouponDenom());
                    if (groupId.equalsIgnoreCase("9")) {
                        preferAmount2 = Math.round(preferAmount2 / discountCnt);
                    }
                }
            }
        }

        if (shopAddFlagY > 0) {
            if (shopAddFlagY > 1) {
                throw new IllegalArgumentException("店铺优惠只能有一个叠加券！");
            } else {
                // 有单品券
                if (StringUtils.isNotEmpty(spCouponFlag) && spCouponFlag.equalsIgnoreCase("1")) {
                    if (activityPrice >= spCouponThreshold) {
                        preferAmount2 = spCouponDenom;
                    } else {
                        preferAmount2 = spCouponDenom / Math.ceil(spCouponThreshold / activityPrice);
                    }
                } else {
                    double finalActivityPrice;
                    Integer discountCnt = activityProduct.getDiscountCnt();
                    if (groupId.equalsIgnoreCase("9")) {
                        finalActivityPrice = activityPrice * discountCnt;
                    } else {
                        finalActivityPrice = activityPrice;
                    }

                    ActivityCoupon tmp = shopCouponList.get(0);
                    double n1 = Double.parseDouble(tmp.getCouponDenom()) / Double.parseDouble(tmp.getCouponThreshold());//优惠券面额/优惠券门槛
                    double n2 = finalActivityPrice / Double.parseDouble(tmp.getCouponThreshold());//活动商品价格/优惠券门槛； 大于1是活动价高于代金券门槛   否则反之
                    if (n2 < 1) {//如果否，则免减金额=商品活动价*（券免减金额/券门槛）
                        preferAmount2 = finalActivityPrice * n1;
                    } else {
                       // 如果是，则免减金额=该门槛下需减去的金额
                        preferAmount2=Double.parseDouble(tmp.getCouponDenom());
//                        preferAmount2 = Math.floor(n2) * Double.parseDouble(tmp.getCouponDenom());
//                        if (groupId.equalsIgnoreCase("9")) {
//                            preferAmount2 = preferAmount2 / discountCnt;
//                        }
                    }
                }
            }
        }
        // 平台优惠
        List<ActivityCoupon> platCouponList = couponList.stream().filter(x -> x.getCouponType().equalsIgnoreCase("P")).collect(Collectors.toList());
        long platAddFlagY = platCouponList.stream().filter(x -> x.getAddFlag().equalsIgnoreCase("1")).count();
        long platAddFlagN = platCouponList.stream().filter(x -> x.getAddFlag().equalsIgnoreCase("0")).count();
        // 非叠加券
        if (platAddFlagN > 0) {
            double finalActivityPrice;
            Integer discountCnt = activityProduct.getDiscountCnt();
            if (groupId.equalsIgnoreCase("9")) {
                finalActivityPrice = activityPrice * discountCnt;
            } else {
                finalActivityPrice = activityPrice;
            }

            Map<String, ActivityCoupon> tmpThreshold = Maps.newHashMap();
            platCouponList.stream().filter(x -> x.getAddFlag().equalsIgnoreCase("0"))
                    .sorted(Comparator.comparingDouble(v -> Double.parseDouble(v.getCouponThreshold())))
                    .forEach(v -> {
                        if (finalActivityPrice >= Double.parseDouble(v.getCouponThreshold())) {
                            System.out.println(v);
                            tmpThreshold.put("tmp", v);
                        }
                    });
            ActivityCoupon tmp = tmpThreshold.get("tmp");

            if (tmp == null) {
                preferAmount3_1 = 0;
            } else {
                preferAmount3_1 = Double.parseDouble(tmp.getCouponDenom());
                if (groupId.equalsIgnoreCase("9")) {
                    preferAmount3_1 = preferAmount3_1 / discountCnt;
                }
            }
        }

        // 叠加券
        if (platAddFlagY > 0) {
            if (platAddFlagY > 1) {
                throw new IllegalArgumentException("平台优惠只能有一个叠加券！");
            } else {
                double finalActivityPrice;
                Integer discountCnt = activityProduct.getDiscountCnt();
                if (groupId.equalsIgnoreCase("9")) {
                    finalActivityPrice = activityPrice * discountCnt;
                } else {
                    finalActivityPrice = activityPrice;
                }

                ActivityCoupon tmp = platCouponList.get(0);
                double n1 = Double.parseDouble(tmp.getCouponDenom()) / Double.parseDouble(tmp.getCouponThreshold());
                double n2 = finalActivityPrice / Double.parseDouble(tmp.getCouponThreshold());
                if (n2 < 1) {
                    preferAmount3_2 = 0;
                } else {
                    preferAmount3_2 = Math.floor(n2) * Double.parseDouble(tmp.getCouponDenom());
                    if (groupId.equalsIgnoreCase("9")) {
                        preferAmount3_2 = preferAmount3_2 / discountCnt;
                    }
                }
            }
        }
        double activityProfit = 0D;
        double totalDiscount = preferAmount1 + preferAmount2 + preferAmount3_1 + preferAmount3_2;
        activityPrice = (activityPrice - totalDiscount) < 0 ? 0 : (activityPrice - totalDiscount);
        activityProduct.setMinPrice(Math.round(activityPrice));
        switch (groupId) {
            case "9":
                activityProfit = activityProduct.getDiscountSize();
                activityProduct.setActivityProfit(activityProfit);
                break;
            case "13":
                activityProfit = preferAmount2 + preferAmount3_1 + preferAmount3_2;
                activityProduct.setActivityProfit(Math.ceil(activityProfit));
                break;
            case "14":
                activityProfit = preferAmount1 + preferAmount2 + preferAmount3_1 + preferAmount3_2;
                activityProduct.setActivityProfit(Math.ceil(activityProfit));
                break;
            default:
                break;
        }
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
     * 删除商品，删除完更新head表的数据状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
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
                                                        String repeatProduct, String activityType) throws Exception {
        String xlsSuffix = ".xls";
        String xlsxSuffix = ".xlsx";
        // 表头
        List<String> headers = Arrays.asList(
                "商品ID", "商品名称", "日常商品单价\n（元/件）", "报名活动单价\n（元/件）", "店铺活动机制", "满件打折件数\n（件）", "满件打折力度\n（折）", "立减特价金额（元）", "单品券", "单品券门槛\n（元）", "单品券面额\n（元）", "最低价", "利益点"
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
                    Row row = sheet.getRow(i);
                    //获取第一行的的数据
                    if (null == row) {
                        errorList.add(new ActivityProductUploadError("行为空", i + 1));
                        continue;
                    }
                    boolean blankRow = false;
                    for (int j = 0; j < headers.size(); j++) {
                        Cell tmp = row.getCell(j);
                        if(tmp != null && tmp.getCellType() != 3) {
                            blankRow = true;
                        }
                    }
                    if (!blankRow) {
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
                        errorList.add(new ActivityProductUploadError("商品ID为空", i + 1));
                    } else {
                        if (cell0.getCellType() == 1) {
                            productId = cell0.getStringCellValue();
                        } else if (cell0.getCellType() == 0) {
                            productId = new BigDecimal(cell0.getNumericCellValue()).toString();
                        } else {
                            errorList.add(new ActivityProductUploadError("商品ID数据类型有误，应改为文本型或数值型", i + 1));
                        }
                    }

                    // 商品名称
                    String productName = "";
                    Cell cell1 = row.getCell(1);
                    if (null == cell1 || cell1.getCellType() == 3) {
                        errorList.add(new ActivityProductUploadError("商品名称为空", i + 1, true));
                    } else {
                        if (cell1.getCellType() == 1) {
                            productName = cell1.getStringCellValue();
                        } else {
                            errorList.add(new ActivityProductUploadError("商品名数据类型有误，应改为文本型", i + 1));
                        }
                    }
                    // 日常商品单价
                    double formalPrice = 0D;
                    Cell cell2 = row.getCell(2);
                    if (null == cell2 || cell2.getCellType() == 3) {
                        errorList.add(new ActivityProductUploadError("日常商品单价为空", i + 1));
                    } else {
                        if (cell2.getCellType() == 0) {
                            formalPrice = cell2.getNumericCellValue();
                        } else if (cell2.getCellType() == 1) {
                            formalPrice = new BigDecimal(cell2.getStringCellValue()).doubleValue();
                        } else {
                            errorList.add(new ActivityProductUploadError("日常商品单价数据类型有误，应改为数值型", i + 1));
                        }
                    }

                    // 报名活动单价
                    Double activityPrice = 0D;
                    Cell cell3 = row.getCell(3);
                    if (null == cell3 || cell3.getCellType() == 3) {
                        errorList.add(new ActivityProductUploadError("报名活动单价为空", i + 1));
                    } else {
                        if (cell3.getCellType() == 0) {
                            activityPrice = cell3.getNumericCellValue();
                        } else if (cell3.getCellType() == 1) {
                            activityPrice = new BigDecimal(cell3.getStringCellValue()).doubleValue();
                        } else {
                            errorList.add(new ActivityProductUploadError("报名活动单价数据类型有误，应改为数值型", i + 1));
                        }
                    }

                    // 店铺活动机制
                    String groupId = null;
                    Cell cell4 = row.getCell(4);
                    if (null == cell4 || cell4.getCellType() == 3) {
                        errorList.add(new ActivityProductUploadError("店铺活动机制为空", i + 1));
                    } else {
                        if (cell4.getCellType() == 1) {
                            String groupName = Optional.of(row.getCell(4)).map(Cell::getStringCellValue).get();
                            switch (groupName) {
                                case "满件打折":
                                    groupId = "9";
                                    break;
                                case "立减特价":
                                    groupId = "14";
                                    break;
                                case "仅店铺券":
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
                    // 折扣件数
                    double discountCnt = 0D;
                    // 折扣力度
                    double discountSize = 0D;
                    // 折扣金额
                    double discountAmount = 0D;
                    switch (Objects.requireNonNull(groupId)) {
                        case "9":
                            // 满件打折
                            Cell cell5 = row.getCell(5);
                            if (null == cell5 || cell5.getCellType() == 3) {
                                errorList.add(new ActivityProductUploadError("满件打折件数为空", i + 1));
                            } else {
                                if (cell5.getCellType() == 0) {
                                    discountCnt = cell5.getNumericCellValue();
                                } else {
                                    errorList.add(new ActivityProductUploadError("满件打折件数数据类型有误，应改为数值型", i + 1));
                                }
                            }

                            Cell cell6 = row.getCell(6);
                            if (null == cell6 || cell6.getCellType() == 3) {
                                errorList.add(new ActivityProductUploadError("满件打折力度为空", i + 1));
                            } else {
                                if (cell6.getCellType() == 1) {
                                    discountSize = Double.parseDouble(cell6.getStringCellValue());
                                } else if (cell0.getCellType() == 0) {
                                    discountSize = cell6.getNumericCellValue();
                                } else {
                                    errorList.add(new ActivityProductUploadError("满件打折力度数据类型有误，应改为数值型", i + 1));
                                }
                            }
                            break;
                        case "14":
                            // 满元减钱
                            Cell cell7 = row.getCell(7);
                            if (null == cell7 || cell7.getCellType() == 3) {
                                errorList.add(new ActivityProductUploadError("立减特价金额为空", i + 1));
                            } else {
                                if (cell7.getCellType() == 0) {
                                    discountAmount = cell7.getNumericCellValue();
                                } else {
                                    errorList.add(new ActivityProductUploadError("立减特价金额数据类型有误，应改为数值型", i + 1));
                                }
                            }
                            break;
                        case "13":
                            break;
                        default:
                            throw new IllegalArgumentException("活动机制未匹配到组ID");
                    }

                    String singleProduct = "";
                    Cell cell8 = row.getCell(8);
                    if (null == cell8 || cell8.getCellType() == 3) {
                        // do nothing
                    } else {
                        if (cell8.getCellType() == 1) {
                            singleProduct = cell8.getStringCellValue();
                        } else {
                            errorList.add(new ActivityProductUploadError("单品券数据类型有误，应改为文本型", i + 1));
                        }
                    }

                    if (StringUtils.isNotEmpty(singleProduct)) {
                        singleProduct = singleProduct.equals("有") ? "1" : (singleProduct.equals("无") ? "0" : null);
                        if (singleProduct == null) {
                            errorList.add(new ActivityProductUploadError("单品券值与限定值不匹配", i + 1));
                        }
                    }
                    double singleProductThreadHold = 0;
                    Cell cell9 = row.getCell(9);
                    if (null == cell9 || cell9.getCellType() == 3) {
                        if (StringUtils.isNotEmpty(singleProduct)) {
                            errorList.add(new ActivityProductUploadError("单品券门槛为空", i + 1));
                        }
                    } else {
                        if (cell9.getCellType() == 0) {
                            singleProductThreadHold = cell9.getNumericCellValue();
                        } else if (cell9.getCellType() == 1) {
                            singleProductThreadHold = new BigDecimal(cell9.getStringCellValue()).doubleValue();
                        } else {
                            errorList.add(new ActivityProductUploadError("单品券门槛类型有误，应改为数值型", i + 1));
                        }
                    }

                    double singleProductDeno = 0;
                    Cell cell10 = row.getCell(10);
                    if (null == cell10 || cell10.getCellType() == 3) {
                        if (StringUtils.isNotEmpty(singleProduct)) {
                            errorList.add(new ActivityProductUploadError("单品券面额为空", i + 1));
                        }
                    } else {
                        if (cell10.getCellType() == 0) {
                            singleProductDeno = cell10.getNumericCellValue();
                        } else if (cell10.getCellType() == 1) {
                            singleProductDeno = new BigDecimal(cell10.getStringCellValue()).doubleValue();
                        } else {
                            errorList.add(new ActivityProductUploadError("单品券面额类型有误，应改为数值型", i + 1));
                        }
                    }

                    ActivityProduct activityProduct = new ActivityProduct();
                    activityProduct.setHeadId(Long.valueOf(headId));
                    activityProduct.setGroupId(groupId);
                    activityProduct.setProductName(productName);
                    activityProduct.setActivityPrice(new BigDecimal(activityPrice).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    activityProduct.setFormalPrice(new BigDecimal(formalPrice).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    activityProduct.setDiscountSize(new BigDecimal(discountSize).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    activityProduct.setDiscountCnt(new BigDecimal(discountCnt).setScale(2, RoundingMode.HALF_UP).intValue());
                    activityProduct.setDiscountAmount(new BigDecimal(discountAmount).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    activityProduct.setProductId(productId);
                    activityProduct.setActivityType(activityType);
                    if (StringUtils.isNotEmpty(singleProduct)) {
                        activityProduct.setSpCouponFlag(singleProduct);
                        activityProduct.setSpCouponThreshold((new BigDecimal(singleProductThreadHold).setScale(2, RoundingMode.HALF_UP).doubleValue()));
                        activityProduct.setSpCouponDenom((new BigDecimal(singleProductDeno).setScale(2, RoundingMode.HALF_UP).doubleValue()));
                    }else {
                        activityProduct.setSpCouponFlag("0");
                    }
                    activityProduct = calculateProductMinPrice(activityProduct);
                    //如果错误集合中有数据，就不会往真正的数据集合存，而是直接遍历数据，分析以后有可能出现的错误数据格式
                    if(errorList.size()>0){
                        continue;
                    }
                    productList.add(activityProduct);
                }
                //只有数据集合中有数据，并且错误集合中没数据，才能进行数据保存
                if (productList.size() != 0&&errorList.size()<=0) {
                    saveUploadProductData(headId, productList, uploadMethod, repeatProduct,activityType);
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
        List<ActivityProductUploadError> errors = dataList.stream().sorted(Comparator.comparing(ActivityProductUploadError::getFirstErrorRow)).collect(Collectors.toList());
        if (errors.size() > 6) {
            errors = errors.subList(0, 6);
        }
        return errors;
    }


    /**
     * 验证上传商品信息
     *
     * @param headId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void validProductInfo(String headId) {
        activityProductMapper.updateAllValidInfo(headId);
        activityProductMapper.updateValidInfo(headId);
      //  activityProductMapper.updateValidRepeatSkuInfo(headId);
    }

    @Override
    public int getCountByHeadId(String headId) {
        return activityProductMapper.getCountByHeadId(headId);
    }

    /**
     * 判断当前
     *
     * @param headId
     * @return
     */
    @Override
    public int validProduct(String headId) {
        return activityProductMapper.validProduct(headId);
    }

    @Override
    public List<String> getGroupIds(Long headId) {
        return activityProductMapper.getGroupIds(headId);
    }

    @Override
    public boolean checkProductId(String headId, String productId) {
        return activityProductMapper.checkProductId(headId, productId) == 0;
    }

    @Override
    public List<String> getNotValidProductCount(Long headId) {
        return activityProductMapper.getNotValidProduct(headId);
    }

    /**
     * 保存上传的数据
     */
    private boolean saveUploadProductData(String headId, List<ActivityProduct> productList, String uploadMethod, String repeatProduct,String activityType) {
        String uploadMethod0 = "0";
        String repeatProduct0 = "0";
        String repeatProduct1 = "1";
        String uploadMethod1 = "1";
        List<ActivityProduct> insertList = Lists.newArrayList();
        List<ActivityProduct> deleteList = Lists.newArrayList();
        List<String> oldProductList = activityProductMapper.getProductIdByHeadId(headId,activityType);
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