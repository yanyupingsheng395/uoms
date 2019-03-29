package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.service.ReasonService;
import com.linksteady.operate.vo.ReasonVO;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 原因探究相关的controller
 */
@RestController
@RequestMapping("/reason")
public class ReasonController  extends BaseController {

    @Autowired
    ReasonService reasonService;

    /**
     * 获取原因探究列表
     * @param request  包装分页参数的对象
     * @return
     */
    @RequestMapping("/list")
    public ResponseBo list(@RequestBody  QueryRequest request) {
        List<Map<String,Object>> result=reasonService.getReasonList((request.getPageNum()-1)*request.getPageSize()+1, request.getPageNum()*request.getPageSize());

        int totalCount= reasonService.getReasonTotalCount();
        return  ResponseBo.okOverPaging("",totalCount,result);
    }

    /**
     * 获取原因探究列表
     * @param reasonVO 原因探究主表
     * @return
     */
    @RequestMapping("/submitAnalysis")
    public ResponseBo submitAnalysis(@RequestBody ReasonVO reasonVO) {

        String curuser=SecurityUtils.getSubject().getPrincipal().toString();

        int primaryKey=reasonService.getReasonPrimaryKey();

        //写入主记录 同时获取到主键ID
        reasonService.saveReasonData(reasonVO,curuser,primaryKey);

        // 写入选择的维度信息 UO_REASON_DETAIL
       // reasonService.saveReasonDetail(primaryKey,reasonVO.getDims(),reasonVO.getDimValues());

        //UO_REASON_TEMPLATE
        //reasonService.saveReasonTemplate(primaryKey,reasonVO.getTemplates());

        //原因KPI列表

        System.out.println(reasonVO);
        return  ResponseBo.ok(12345);
    }




}
