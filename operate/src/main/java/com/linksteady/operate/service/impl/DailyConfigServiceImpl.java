package com.linksteady.operate.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.dao.DailyConfigMapper;
import com.linksteady.operate.service.DailyConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class DailyConfigServiceImpl implements DailyConfigService {

    @Autowired
    private DailyConfigMapper dailyConfigMapper;

    @Autowired
    private ConfigService configService;


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
        String activeCode =configService.getValueByName("op.daily.pathactive.list");

        Map<String, String> pathActiveMap =configService.selectDictByTypeCode("PATH_ACTIVE");
        Map<String, String> userValueMap =configService.selectDictByTypeCode("USER_VALUE");
        Map<String, String> lifeCycleMap =configService.selectDictByTypeCode("LIFECYCLE");

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
                activeDesc="";
                activePolicy="";
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
        dailyConfigMapper.updateCheckFlagAndRemark(whereInfo, "文案含补贴，补贴信息不能为空");

        // 不含券：券名称不为空
        whereInfo = " and t1.IS_COUPON = 0 and t4.COUPON_DISPLAY_NAME is not null";
        dailyConfigMapper.updateCheckFlagAndRemark(whereInfo, "文案不含补贴，补贴信息不能出现");
        // 验证券的有效期
        whereInfo = " and to_number(to_char(t4.VALID_END, 'YYYYMMDD')) < to_number(to_char(sysdate, 'YYYYMMDD'))";
        dailyConfigMapper.updateCheckFlagAndRemark(whereInfo, "补贴有效期已过期");

        // 短信：不为空
        whereInfo = " and t2.SMS_CONTENT IS NULL";
        dailyConfigMapper.updateCheckFlagAndRemark(whereInfo, "尚未为群组配置文案");

        String active =configService.getValueByName("op.daily.pathactive.list");
        int result = 0;
        if(StringUtils.isNotEmpty(active)) {
            List<String> activeList = Arrays.asList(active.split(","));
            if(activeList.size() > 0) {
                result = dailyConfigMapper.validCheckedUserGroup(Arrays.asList(active.split(",")));
            }
        }else {
            throw new RuntimeException("活跃度数据表配置有误！");
        }
        return result > 0;
    }

    @Override
    public void deleteSmsGroup(String groupId) {
        dailyConfigMapper.deleteSmsGroup(Arrays.asList(groupId.split(",")));
    }
}
