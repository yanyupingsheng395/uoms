package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.dao.DictMapper;
import com.linksteady.common.domain.Dict;
import com.linksteady.common.domain.enums.ConfigEnum;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.dao.CouponMapper;
import com.linksteady.operate.dao.DailyConfigMapper;
import com.linksteady.operate.dao.QywxDailyConfigMapper;
import com.linksteady.operate.dao.QywxDailyCouponMapper;
import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.domain.DailyGroupTemplate;
import com.linksteady.operate.exception.OptimisticLockException;
import com.linksteady.operate.service.QywxDailyConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QywxDailyConfigServiceImpl implements QywxDailyConfigService {

    @Autowired
    private QywxDailyConfigMapper dailyConfigMapper;

    @Autowired
    private ConfigService configService;

    @Autowired
    private QywxDailyCouponMapper couponMapper;

    /**
     * 获取用户组列表
     * @return
     */
    @Override
    public List<DailyGroupTemplate> getUserGroupList() {
        return dailyConfigMapper.getUserGroupList();
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

        //验证优惠券是否在有效期
        String whereInfo = " and t4.VALID_END::date < current_date";
        dailyConfigMapper.updateCheckFlagAndRemark(whereInfo, "补贴有效期已过期");

        //验证是否存在未配置优惠券的情况
        dailyConfigMapper.updateCheckFlagNotConfigCoupon("尚未为群组配置补贴");

        //验证是否配置了文案
        whereInfo = " and t1.sms_code is null";
        dailyConfigMapper.updateCheckFlagAndRemark(whereInfo, "尚未为群组配置文案");

        String active = configService.getValueByName(ConfigEnum.pathActiveList.getKeyCode());
        int result = 0;
        if (StringUtils.isNotEmpty(active)) {
            List<String> activeList = Arrays.asList(active.split(","));
            if (activeList.size() > 0) {
                //所有未通过检查的组的数量
                result = dailyConfigMapper.validCheckedUserGroup(Arrays.asList(active.split(",")));
            }
        } else {
            throw new RuntimeException("活跃度数据表配置有误！");
        }
        return result > 0;
    }

    /**
     * 校验每日任务：
     * 是否配置了优惠券；
     * 优惠券是否在有效期；
     * 是否配置了文案（校验文本）
     * @return
     */
    @Override
    public Map<String,Object> validUserGroupForQywx() {
        Map<String, Object> result = Maps.newHashMap();
        int couponCnt = couponMapper.getValidCoupon();
        int groupCouponCnt =  dailyConfigMapper.validGroupCoupon();
        int groupContextCnt =  dailyConfigMapper.validGroupContext();
        if(couponCnt == 0) {
            result.put("flag", "未通过");
            result.put("desc", "当前没有有效的补贴");
        }else if(groupCouponCnt > 0) {
            result.put("flag", "未通过");
            result.put("desc", "部分群组尚未配置补贴");
        } else if(groupContextCnt > 0) {
            result.put("flag", "未通过");
            result.put("desc", "部分群组尚未配置文案");
        }else {
            result.put("flag", "通过");
            result.put("desc", "");
        }
        return result;
    }

    @Override
    public void updateWxMsgId(String lifeCycle, String pathActive, Long qywxId) {
        dailyConfigMapper.updateWxMsgId(lifeCycle, pathActive, qywxId);
    }

    @Override
    public Map<String, Object> getConfigInfoByGroup(String userValue, String lifeCycle, String pathActive, String tarType) {
        Map<String, Object> data = dailyConfigMapper.findMsgInfo(userValue, lifeCycle, pathActive, tarType);
        // 获取补贴信息
        List<CouponInfo> couponInfos = couponMapper.getCouponListByGroup(userValue, lifeCycle, pathActive, tarType);
        if (data == null) {
            data = Maps.newHashMap();
        }
        data.put("couponInfos", couponInfos);
        return data;
    }

    /**
     * 先验证有无合法的券，再根据折扣等级给群组安券
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoSetGroupCoupon() throws OptimisticLockException{
            //查找当前校验通过的优惠券的条数
            int count1 = couponMapper.getValidCoupon();
            if (count1 == 0) {
                throw new OptimisticLockException("无有效的补贴，请先配置补贴!");
            }
            // 更新discountLevel字段
            couponMapper.updateDiscountLevel();
            // 清空群组和券的关系表
            couponMapper.deleteAllCouponGroupData();
            // 根据discountLevel字段匹配到用户分组上
            couponMapper.resetCouponGroupData();
    }

    @Override
    public List<Map<String, String>> getGroupDescription() {
        List<Map<String, String>> result = Lists.newArrayList();
        Map<String, String> tmp1 = Maps.newHashMap();
        tmp1.put("colName", "在成长类目所处生命周期阶段");
        tmp1.put("colValue", "新手期");
        tmp1.put("colDesc", "在所购买类目的忠诚度极低，流失概率大；");
        tmp1.put("colAdvice", "用一定补贴降低复购门槛尽快进入成长期；");
        Map<String, String> tmp2 = Maps.newHashMap();
        tmp2.put("colName", "在成长类目所处生命周期阶段");
        tmp2.put("colValue", "成长期");
        tmp2.put("colDesc", "在所购买类目开始快速建立忠诚度；");
        tmp2.put("colAdvice", "逐步减少补贴让用户持续复购进入成熟期；");
        Map<String, String> tmp3 = Maps.newHashMap();
        tmp3.put("colName", "在成长类目所处生命周期阶段");
        tmp3.put("colValue", "成熟期");
        tmp3.put("colDesc", "在所购买类目忠诚度较高，复购率较高；");
        tmp3.put("colAdvice", "原商品无需补贴、也可通过补贴提升件单价；");
        Map<String, String> tmp4 = Maps.newHashMap();
        tmp4.put("colName", "完成下一次购买前的活跃度状态");
        tmp4.put("colValue", "活跃状态");
        tmp4.put("colDesc", "当前购买间隔状态，用户再次购买概率较高；");
        tmp4.put("colAdvice", "用一定补贴尽可能引导用户完成购买；");
        Map<String, String> tmp5 = Maps.newHashMap();
        tmp5.put("colName", "完成下一次购买前的活跃度状态");
        tmp5.put("colValue", "留存状态");
        tmp5.put("colDesc", "防止用户流失的最佳、最后合理时机；");
        tmp5.put("colAdvice", "需大力补贴防止用户进入流失状态；");
        Map<String, String> tmp6 = Maps.newHashMap();
        tmp6.put("colName", "完成下一次购买前的活跃度状态");
        tmp6.put("colValue", "流失状态");
        tmp6.put("colDesc", "挽回用户的最后有效时间点；");
        tmp6.put("colAdvice", "通常挽回概率较低，量力而行；");
        result.add(tmp1);
        result.add(tmp2);
        result.add(tmp3);
        result.add(tmp4);
        result.add(tmp5);
        result.add(tmp6);
        return result;
    }

    @Override
    public void setSmsCode(Long groupId, String smsCode) {
        dailyConfigMapper.setSmsCode(groupId, smsCode);
    }

    @Override
    public void resetOpFlag() {
        dailyConfigMapper.resetOpFlag();
    }
}
