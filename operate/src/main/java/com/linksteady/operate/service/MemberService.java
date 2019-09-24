package com.linksteady.operate.service;

import com.linksteady.operate.domain.MemberDetail;
import com.linksteady.operate.domain.MemberHead;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-09-23
 */
public interface MemberService {

    /**
     * 根据条件获取list的count
     * @param date
     * @return
     */
    int getHeadListCount(String date);

    /**
     * 获取head的分页数据
     * @param start
     * @param end
     * @param date
     * @return
     */
    List<MemberHead> getHeadListPage(int start, int end, String date);

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
    List<MemberDetail> getDetailListPage(String headId, int start, int end, String userValue, String pathActive, String brandDeep, String joinRate);

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
     * 获取编辑页的头部提示信息
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
