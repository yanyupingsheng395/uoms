package com.linksteady.operate.dao;

import com.linksteady.operate.domain.MemberDetail;
import com.linksteady.operate.domain.MemberHead;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-09-23
 */
public interface MemberMapper {

    /**
     * 获取head分页数据
     * @param start
     * @param end
     * @param date
     * @return
     */
    List<MemberHead> getHeadListPage(int limit, int offset, @Param("memberDate") String memberDate);

    /**
     * 获取head分页数据的大小
     * @param date
     * @return
     */
    int getHeadListCount(@Param("memberDate") String memberDate);

    /**
     * 获取detail的分页数据
     * @param start
     * @param end
     * @param userValue
     * @param pathActive
     * @param brandDeep
     * @param joinRate
     * @return
     */
    List<MemberDetail> getDetailListPage(String headId, int limit, int offset, String userValue, String pathActive, String brandDeep, String joinRate);

    /**
     * 获取detail的分页数据
     * @param userValue
     * @param pathActive
     * @param brandDeep
     * @param joinRate
     * @return
     */
    int getDetailListCount(String headId, String userValue, String pathActive, String brandDeep, String joinRate);

    /**
     * 获取编辑页头部提示信息
     * @param headId
     * @return
     */
    Map<String, Object> getTipInfo(String headId);

    /**
     * 获取满减信息
     * @param headId
     * @param reduceFlag
     * @return
     */
    List<MemberDetail> getReduceInfo(String headId, String reduceFlag);
}
