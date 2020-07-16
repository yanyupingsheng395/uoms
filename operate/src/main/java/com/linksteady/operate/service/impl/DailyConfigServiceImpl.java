package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.dao.DictMapper;
import com.linksteady.common.domain.Dict;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.dao.CouponMapper;
import com.linksteady.operate.dao.DailyConfigMapper;
import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.domain.DailyGroupTemplate;
import com.linksteady.operate.domain.enums.ConfigEnum;
import com.linksteady.operate.exception.OptimisticLockException;
import com.linksteady.operate.service.DailyConfigService;
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

    /**
     * 获取用户组列表
     * @return
     */
    @Override
    public List<DailyGroupTemplate> getUserGroupList() {
        String active = configService.getValueByName(ConfigEnum.pathActiveList.getKeyCode());
        List<String> activeList = null;
        if (StringUtils.isNotEmpty(active)) {
            activeList = Arrays.asList(active.split(","));
        }
        return dailyConfigMapper.getUserGroupList(activeList);
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
     * 验证
     * @return
     */
    @Override
    public boolean validUserGroupForQywx() {
        //todo 后续待补充
        return false;
    }

    @Override
    public void updateWxMsgId(Long groupId, Long qywxId) {
        dailyConfigMapper.updateWxMsgId(groupId, qywxId);
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
    public List<Map<String, String>> getGroupDescription(String userValue, String lifecycle, String pathActive) {
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

    @Override
    public void setSmsCode(Long groupId, String smsCode) {
        dailyConfigMapper.setSmsCode(groupId, smsCode);
    }

    @Override
    public void resetOpFlag() {
        dailyConfigMapper.resetOpFlag();
    }
}
