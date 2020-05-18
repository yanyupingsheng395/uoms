package com.linksteady.wxofficial.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.wxofficial.common.exception.LinkSteadyException;
import com.linksteady.wxofficial.config.WxProperties;
import com.linksteady.wxofficial.dao.WxOfficialUserMapper;
import com.linksteady.wxofficial.entity.bo.UserTagBo;
import com.linksteady.wxofficial.entity.bo.WxMpUserBo;
import com.linksteady.wxofficial.entity.vo.WxOfficialUserVo;
import com.linksteady.wxofficial.service.WxOfficialUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/4/21
 */
@Slf4j
@Service
public class WxOfficialUserServiceImpl implements WxOfficialUserService {

    @Autowired
    private WxProperties wxProperties;

    @Autowired
    private WxOfficialUserMapper wxOfficialUserMapper;

    @Override
    public int getDataCount() {
        return wxOfficialUserMapper.getDataCount();
    }

    @Override
    public List<WxOfficialUserVo> getDataList(int limit, int offset) {
        return wxOfficialUserMapper.getDataList(limit, offset);
    }

    @Override
    public void updateTagIds(List<UserTagBo> dataList) {
        wxOfficialUserMapper.updateTagIds(dataList);
    }

    @Override
    public void updateRemark(String userId, String remark) {
        wxOfficialUserMapper.updateRemark(userId, remark);
    }

    @Override
    public WxOfficialUserVo getUserById(String userId) {
        return wxOfficialUserMapper.selectByPrimaryKey(userId);
    }

    /**
     * 同步公众号用户
     * @param data
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void setSyncUserList(String data) throws Exception {
        JSONObject object = JSON.parseObject(data);
        String appId = object.getString("appId");
        if (StringUtils.isNotEmpty(appId) && wxProperties.getAppId().equalsIgnoreCase(appId)) {
            List<WxOfficialUserVo> userList = JSONObject.parseArray(object.getString("data"), WxOfficialUserVo.class);
            userList.stream().filter(x->x.getTagIds().length > 0).forEach(v->{
                v.setTagidList(StringUtils.join(v.getTagIds(), ","));
            });
            log.info("获取到到同步用户数据条数为" + userList.size());
            if(!userList.isEmpty()) {
                wxOfficialUserMapper.syncDataList(userList);
            }
        } else {
            throw new LinkSteadyException("appId不匹配，请检查");
        }
    }
}
