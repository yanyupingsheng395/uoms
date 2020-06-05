package com.linksteady.operate.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.dao.DictMapper;
import com.linksteady.common.domain.Dict;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.dao.CouponMapper;
import com.linksteady.operate.dao.DailyConfigMapper;
import com.linksteady.operate.dao.VmallCouponMapper;
import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.service.DailyConfigService;
import com.linksteady.operate.vo.GroupCouponVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class DailyConfigServiceImpl implements DailyConfigService {

    @Autowired
    private DailyConfigMapper dailyConfigMapper;

    @Autowired
    private ConfigService configService;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private DictMapper dictMapper;

    @Autowired
    private VmallCouponMapper vmallCouponMapper;


    /**
     * 理解用户
     *
     * @param userValue
     * @param pathActive
     * @param lifecycle
     * @return
     */
    @Override
    public Map<String, Object> usergroupdesc(String userValue, String pathActive, String lifecycle) {
        //根据活跃度，设置活跃度按钮
        String activeCode = configService.getValueByName("op.daily.pathactive.list");

        Map<String, String> pathActiveMap = configService.selectDictByTypeCode("PATH_ACTIVE");
        Map<String, String> userValueMap = configService.selectDictByTypeCode("USER_VALUE");
        Map<String, String> lifeCycleMap = configService.selectDictByTypeCode("LIFECYCLE");

        List<String> activeCodeList = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(activeCode);

        List<Map<String, String>> activeResult = Lists.newArrayList();
        Map<String, String> temp;
        for (String code : activeCodeList) {
            temp = Maps.newHashMap();
            temp.put("name", pathActiveMap.get(code));

            if (pathActive.equals(code)) {
                temp.put("flag", "1");
            } else {
                temp.put("flag", "0");
            }
            activeResult.add(temp);
        }

        List<Map<String, String>> userValueResult = Lists.newArrayList();
        //根据用户价值，设置用户价值按钮
        for (Map.Entry<String, String> entry : userValueMap.entrySet()) {
            temp = Maps.newHashMap();
            temp.put("name", entry.getValue());

            if (userValue.equals(entry.getKey())) {
                temp.put("flag", "1");
            } else {
                temp.put("flag", "0");
            }
            userValueResult.add(temp);
        }

        //根据生命周期，设置生命周期按钮
        List<Map<String, String>> lifecycleResult = Lists.newArrayList();
        for (Map.Entry<String, String> entry : lifeCycleMap.entrySet()) {
            temp = Maps.newHashMap();
            temp.put("name", entry.getValue());

            if (lifecycle.equals(entry.getKey())) {
                temp.put("flag", "1");
            } else {
                temp.put("flag", "0");
            }
            lifecycleResult.add(temp);
        }

        String activeDesc = "";
        String activePolicy = "";
        //设置活跃度业务理解 及 运营策略
        switch (pathActive) {
            case "UAC_01":
                activeDesc = "当前到达下一次购买类目的最早合理时间;";
                activePolicy = "处在引导提升购买频率有效时机;";
                break;
            case "UAC_02":
                activeDesc = "到达下一次购买类目成功率最高的时间点;";
                activePolicy = "处在借势培养用户购买最佳时机;";
                break;
            case "UAC_03":
                activeDesc = "经过当前时间没有购买，后续再购买较难;";
                activePolicy = "处在流失之前刺激购买最后时机;";
                break;
            case "UAC_04":
                activeDesc = "流失后，再购买概率相对较高的时间节点;";
                activePolicy = "处在流失后尝试挽回的可行时机;";
                break;
            case "UAC_05":
                activeDesc = "经过当前时间没有购买，后续不会再购买;";
                activePolicy = "处在沉睡之前唤醒用户可行时机;";
                break;
            default:
                activeDesc = "";
                activePolicy = "";
        }

        //设置用户价值 业务理解 及 运营策略
        String valueDesc = "";
        String valuePolicy = "";
        switch (userValue) {
            case "ULC_01":
                valueDesc = "在类目消费很多，未来购买力强，价格不敏感;";
                valuePolicy = "加强情感关怀，防止用户流失，通常无需补贴;";
                break;
            case "ULC_02":
                valueDesc = "在类目消费较多，未来购买力强，价格较敏感;";
                valuePolicy = "加强情感关怀，关注用户成长，补贴重点培养;";
                break;
            case "ULC_03":
                valueDesc = "在类目消费一般，购买力不确定，价格较敏感;";
                valuePolicy = "无需情感关怀，加强用户留存，补贴适度刺激;";
                break;
            case "ULC_04":
                valueDesc = "在类目消费较少，未来购买力弱，价格很敏感;";
                valuePolicy = "无需情感关怀，减少补贴投入;";
                break;
        }

        //设置生命周期价值 业务理解及运营策略
        String lifecyleDesc = "";
        String lifecyclePolicy = "";
        switch (lifecycle) {
            case "1":
                lifecyleDesc = "对类目没有形成忠诚度;";
                lifecyclePolicy = "降低门槛刺激复购;";
                break;
            case "0":
                lifecyleDesc = "对类目忠诚度开始提升;";
                lifecyclePolicy = "递减补贴培养多购;";
                break;
        }

        Map<String, Object> result = Maps.newHashMap();
        result.put("activeResult", activeResult);
        result.put("userValueResult", userValueResult);
        result.put("lifecycleResult", lifecycleResult);
        result.put("activeDesc", activeDesc);
        result.put("activePolicy", activePolicy);
        result.put("valueDesc", valueDesc);
        result.put("valuePolicy", valuePolicy);
        result.put("lifecyleDesc", lifecyleDesc);
        result.put("lifecyclePolicy", lifecyclePolicy);
        return result;
    }

    /**
     * 验证用户群组：先进行一遍验证，更新配置表的CHECK_FLAG 然后再返回校验的状态
     * 1. 含券：券名称为空
     * 2. 不含券：券名称不为空
     * 3. 短信：不为空
     * 4. 验证券有效期是否合法，系统不对失效的券进行更新，由用户自行更新
     * 5. 获取其他groupId, check_flag = 'Y'的设置CHECK_COMMENTS 为null
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean validUserGroup() {
        dailyConfigMapper.updateCheckFlagY();
        // 获取短信内容为空的情况
        // 含券：券名称为空
        String whereInfo = " and t1.IS_COUPON = 1 AND t4.COUPON_DISPLAY_NAME IS NULL";
//        dailyConfigMapper.updateCheckFlagAndRemark(whereInfo, "文案含补贴，补贴信息不能为空");

//        // 不含券：券名称不为空
//        whereInfo = " and t1.IS_COUPON = 0 and t4.COUPON_DISPLAY_NAME is not null";
//        dailyConfigMapper.updateCheckFlagAndRemark(whereInfo, "文案不含补贴，补贴信息不能出现");
        // 验证券的有效期

//        whereInfo = " and t4.VALID_END::timestamp < now()";
//        dailyConfigMapper.updateCheckFlagAndRemark(whereInfo, "补贴有效期已过期");

        // 短信：不为空
        whereInfo = " and t1.sms_code is null";
        dailyConfigMapper.updateCheckFlagAndRemark(whereInfo, "尚未为群组配置文案");

        String active = configService.getValueByName("op.daily.pathactive.list");
        int result = 0;
        if (StringUtils.isNotEmpty(active)) {
            List<String> activeList = Arrays.asList(active.split(","));
            if (activeList.size() > 0) {
                result = dailyConfigMapper.validCheckedUserGroup(Arrays.asList(active.split(",")));
            }
        } else {
            throw new RuntimeException("活跃度数据表配置有误！");
        }
        return result > 0;
    }

    @Override
    public void deleteSmsGroup(String groupId) {
        dailyConfigMapper.deleteSmsGroup(Arrays.asList(groupId.split(",")));
    }

    @Override
    public boolean validUserGroupForQywx() {
        //todo 后续待补充
        return false;
    }

    @Override
    public void updateWxMsgId(String groupId, String qywxId) {
        dailyConfigMapper.updateWxMsgId(groupId, qywxId);
    }

    @Override
    public Map<String, Object> getCurrentGroupData(String userValue, String lifeCycle, String pathActive, String tarType) {
        Map<String, Object> data = dailyConfigMapper.findMsgInfo(userValue, lifeCycle, pathActive, tarType);
        List<CouponInfo> couponInfos = couponMapper.getCouponListByGroup(userValue, lifeCycle, pathActive, tarType);
        if (data == null) {
            data = Maps.newHashMap();
        }
        data.put("couponList", couponInfos);
        return data;
    }

    /**
     * 先验证有无合法的券，再根据折扣等级给群组安券
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseBo resetGroupCoupon() {
        Lock lock = new ReentrantLock();
        lock.lock();
        try {
            int count1 = couponMapper.getValidCoupon();
            int count2 = vmallCouponMapper.getValidCoupon();
            if (count1 == 0 && count2 == 0) {
                return ResponseBo.error("无有效的优惠券，请先配置优惠券。");
            }
            // 更新discountLevel字段
            couponMapper.updateDiscountLevel();
            // 清空群组和券的关系表
            couponMapper.deleteAllCouponGroupData();
            // 根据discountLevel字段匹配到用户分组上
            couponMapper.resetCouponGroupData();

            // 更新discountLevel字段
            vmallCouponMapper.updateDiscountLevel();
            // 清空群组和券的关系表
            vmallCouponMapper.deleteAllCouponGroupData();
            // 根据discountLevel字段匹配到用户分组上
            vmallCouponMapper.resetCouponGroupData();
            if (count1 == 0) {
                return ResponseBo.warn("淘客券无有效的优惠券，请先配置优惠券。");
            }
            if (count2 == 0) {
                return ResponseBo.warn("小程序券无有效的优惠券，请先配置优惠券。");
            }
        } finally {
            lock.unlock();
        }
        return ResponseBo.ok();
    }

    @Override
    public List<Map<String, String>> getUserGroupValue(String userValue, String lifecycle, String pathActive) {
        List<Dict> userValueList = dictMapper.getDataListByTypeCode("USER_VALUE");
        Map<String, String> userValueMap = userValueList.stream().collect(Collectors.toMap(Dict::getCode, Dict::getValue));
        Map<String, String> userValueRemarkMap = userValueList.stream().collect(Collectors.toMap(Dict::getCode, Dict::getRemark));
        List<Dict> lifecycleList = dictMapper.getDataListByTypeCode("LIFECYCLE");
        Map<String, String> lifecycleMap = lifecycleList.stream().collect(Collectors.toMap(Dict::getCode, Dict::getValue));
        Map<String, String> lifecycleRemarkMap = lifecycleList.stream().collect(Collectors.toMap(Dict::getCode, Dict::getRemark));
        List<Dict> pathActiveList = dictMapper.getDataListByTypeCode("PATH_ACTIVE");
        Map<String, String> pathActiveMap = pathActiveList.stream().collect(Collectors.toMap(Dict::getCode, Dict::getValue));
        Map<String, String> pathActiveRemarkMap = pathActiveList.stream().filter(x->StringUtils.isNotEmpty(x.getRemark())).collect(Collectors.toMap(Dict::getCode, Dict::getRemark));
        List<Map<String, String>> result = Lists.newArrayList();
        Map<String, String> tmp1 = Maps.newHashMap();
        tmp1.put("colName", "用户对类目的价值/沉默成本");
        tmp1.put("colValue", userValueMap.get(userValue));
        tmp1.put("colDesc", userValueRemarkMap.get(userValue).split("\\|")[0]);
        tmp1.put("colAdvice", userValueRemarkMap.get(userValue).split("\\|")[1]);
        Map<String, String> tmp2 = Maps.newHashMap();
        tmp2.put("colName", "用户对类目的生命周期阶段");
        tmp2.put("colValue", lifecycleMap.get(lifecycle));
        tmp2.put("colDesc", lifecycleRemarkMap.get(lifecycle).split("\\|")[0]);
        tmp2.put("colAdvice", lifecycleRemarkMap.get(lifecycle).split("\\|")[1]);
        Map<String, String> tmp3 = Maps.newHashMap();
        tmp3.put("colName", "用户下一次转化的活跃度节点");
        tmp3.put("colValue", pathActiveMap.get(pathActive));
        tmp3.put("colDesc", pathActiveRemarkMap.get(pathActive).split("\\|")[0]);
        tmp3.put("colAdvice", pathActiveRemarkMap.get(pathActive).split("\\|")[1]);
        result.add(tmp1);
        result.add(tmp2);
        result.add(tmp3);
        return result;
    }

    public static void main(String[] args) {
        System.out.println("1|2".split("|")[0]);
    }
}
