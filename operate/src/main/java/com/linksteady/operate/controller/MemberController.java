package com.linksteady.operate.controller;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.MemberDetail;
import com.linksteady.operate.domain.MemberHead;
import com.linksteady.operate.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-09-23
 */
@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    /**
     * 成长任务列表
     * @param request
     * @return
     */
    @RequestMapping("/getHeadListPage")
    public ResponseBo getHeadListPage(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String date = request.getParam().get("memberDate");
        List<MemberHead> dataList = memberService.getHeadListPage(start, end, date);
        int count = memberService.getHeadListCount(date);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 获取detail的分页数据
     * @param request
     * @return
     */
    @RequestMapping("/getDetailListPage")
    public ResponseBo getDetailListPage(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String userValue = request.getParam().get("userValue");
        String pathActive = request.getParam().get("pathActive");
        String brandDeep = request.getParam().get("brandDeep");
        String joinRate = request.getParam().get("joinRate");
        String headId = request.getParam().get("headId");
        List<MemberDetail> memberDetailList = memberService.getDetailListPage(headId, start, end, userValue, pathActive, brandDeep, joinRate);
        int count = memberService.getDetailListCount(headId, userValue, pathActive, brandDeep, joinRate);
        return ResponseBo.okOverPaging(null, count, memberDetailList);
    }

    /**
     * 获取编辑页的头部提示信息
     * @param headId
     * @return
     */
    @GetMapping("/getTipInfo")
    public ResponseBo getTipInfo(String headId) {
        return ResponseBo.okWithData(null, memberService.getTipInfo(headId));
    }

    /**
     * 获取满减信息
     * @return
     */
    @GetMapping("/getReduceInfo")
    public ResponseBo getReduceInfo(String headId, String reduceFlag) {
        return ResponseBo.okWithData(null, memberService.getReduceInfo(headId, reduceFlag));
    }
}
