package com.linksteady.wxofficial.service.impl;

import com.linksteady.wxofficial.dao.WxOfficialUserMapper;
import com.linksteady.wxofficial.entity.bo.UserTagBo;
import com.linksteady.wxofficial.entity.vo.WxOfficialUserVo;
import com.linksteady.wxofficial.service.WxOfficialUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author hxcao
 * @date 2020/4/21
 */
@Service
public class WxOfficialUserServiceImpl implements WxOfficialUserService {

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
}
