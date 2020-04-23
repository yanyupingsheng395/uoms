package com.linksteady.wxofficial.dao;

import com.linksteady.wxofficial.entity.bo.UserTagBo;
import com.linksteady.wxofficial.entity.bo.WxMpUserBo;
import com.linksteady.wxofficial.entity.vo.WxOfficialUserVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/4/21
 */
public interface WxOfficialUserMapper {

    int deleteByPrimaryKey(String id);

    int insert(WxOfficialUserVo record);

    int insertSelective(WxOfficialUserVo record);

    WxOfficialUserVo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(WxOfficialUserVo record);

    int updateByPrimaryKey(WxOfficialUserVo record);

    int getDataCount();

    List<WxOfficialUserVo> getDataList(int limit, int offset);

    void updateTagIds(@Param("dataList") List<UserTagBo> dataList);

    void updateRemark(String userId, String remark);

    void syncDataList(@Param("userList") List<WxOfficialUserVo> userList);
}
