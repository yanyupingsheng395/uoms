package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.MemberMapper;
import com.linksteady.operate.domain.MemberDetail;
import com.linksteady.operate.domain.MemberHead;
import com.linksteady.operate.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-09-23
 */
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public int getHeadListCount(String date) {
        return memberMapper.getHeadListCount(date);
    }

    @Override
    public List<MemberHead> getHeadListPage(int start, int end, String date) {
        return memberMapper.getHeadListPage(start, end, date) ;
    }

    @Override
    public List<MemberDetail> getDetailListPage(String headId, int start, int end, String userValue, String pathActive, String brandDeep, String joinRate) {
        return memberMapper.getDetailListPage(headId, start, end, userValue, pathActive, brandDeep, joinRate);
    }

    @Override
    public int getDetailListCount(String headId, String userValue, String pathActive, String brandDeep, String joinRate) {
        return memberMapper.getDetailListCount(headId, userValue, pathActive, brandDeep, joinRate);
    }

    @Override
    public Map<String, Object> getTipInfo(String headId) {
        return memberMapper.getTipInfo(headId);
    }

    @Override
    public List<MemberDetail> getReduceInfo(String headId, String reduceFlag) {
        return memberMapper.getReduceInfo(headId, reduceFlag);
    }
}
