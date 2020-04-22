package com.linksteady.wxofficial.service;

import com.linksteady.wxofficial.entity.bo.UserTagBo;
import com.linksteady.wxofficial.entity.vo.WxOfficialUserVo;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/4/21
 */
public interface WxOfficialUserService {

    int getDataCount();

    List<WxOfficialUserVo> getDataList(int limit, int offset);

    void updateTagIds(List<UserTagBo> dataList);

    void updateRemark(String userId, String remark);

    WxOfficialUserVo getUserById(String userId);
}
