package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.service.OpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户运营相关的controller
 */
@RestController
@RequestMapping("/op")
public class OpController extends BaseController {

    @Autowired
    OpService opService;

    /**
     * 获取日运营的头表列表
     * @param
     * @return
     */
    @RequestMapping("/getOpDayList")
    public ResponseBo getOpDayList(@RequestBody  QueryRequest request) {
        String daywid=request.getParam().get("daywid");
        List<Map<String,Object>> result=opService.getOpDayList((request.getPageNum()-1)*request.getPageSize()+1, request.getPageNum()*request.getPageSize(),daywid);

        int totalCount= opService.getOpDayListCount(daywid);
        return  ResponseBo.okOverPaging("",totalCount,result);
    }

    /**
     * 获取日运营的用户数
     * @param
     * @return
     */
    @RequestMapping("/getOpDayHeadInfo")
    public ResponseBo getOpDayHeadInfo(@RequestParam  String daywid) {
        Integer userCount=0;
        if(StringUtils.isEmpty(daywid))
        {

            userCount=opService.getOpDayUserCountInfo(new SimpleDateFormat("yyyy-mm-dd").format(new Date()));
        }else
        {
            userCount=opService.getOpDayUserCountInfo(daywid);
        }

        return  ResponseBo.ok(null==userCount?0:userCount.intValue());
    }

    /**
     * 获取日运营的明细列表
     * @param
     * @return
     */
    @RequestMapping("/getOpDayDetailList")
    public ResponseBo getOpDayDetailList(@RequestBody  QueryRequest request) {
        String daywid=request.getParam().get("daywid");
        if(StringUtils.isEmpty(daywid))
        {
            daywid=new SimpleDateFormat("yyyy-mm-dd").format(new Date());
        }

        List<Map<String,Object>> result=opService.getOpDayDetailList((request.getPageNum()-1)*request.getPageSize()+1, request.getPageNum()*request.getPageSize(),daywid);

        int totalCount= opService.getOpDayDetailListCount(daywid);
        return  ResponseBo.okOverPaging("",totalCount,result);
    }

    /**
     * 获取日运营的所有明细列表
     * @param
     * @return
     */
    @RequestMapping("/getOpDayDetailAllList")
    public ResponseBo getOpDayDetailAllList(QueryRequest request) {
        int start = (request.getPageNum()-1)*request.getPageSize()+1;
        int end = request.getPageNum()*request.getPageSize();
        String daywid = request.getParam().get("daywid");
        String userActiv = request.getParam().get("userActiv");
        String userValue = request.getParam().get("userValue");
        String sortColumn = request.getSort();
        String sortOrder = request.getSortOrder();
        List<Map<String,Object>> result=opService.getOpDayDetailAllList(daywid, userActiv, userValue, start, end, sortColumn, sortOrder);
        int totalCount = opService.getOpDayListCount(daywid, userActiv, userValue);
        return ResponseBo.okOverPaging(null,totalCount, result);
    }

    /**
     * 获取到周期运营的头表
     * @param
     * @return
     */
    @RequestMapping("/periodHeaderList")
    public ResponseBo getPeriodHeaderList(@RequestBody  QueryRequest request) {
        List<Map<String,Object>> result=opService.getPeriodHeaderList((request.getPageNum()-1)*request.getPageSize()+1, request.getPageNum()*request.getPageSize(), request.getParam().get("taskName"));
        int totalCount= opService.getPeriodListCount(request.getParam().get("taskName"));
        return  ResponseBo.okOverPaging("",totalCount,result);
    }

    /**
     * 获取到周期运营的头表
     * @param
     * @return
     */
    @RequestMapping("/savePeriodHeaderInfo")
    public ResponseBo savePeriodHeaderInfo(@RequestParam String periodName,@RequestParam String startDt,@RequestParam String endDt) {
        opService.savePeriodHeaderInfo(periodName,startDt,endDt);
        return  ResponseBo.ok("success");
    }

    /**
     * 获取周期运营的用户列表
     * @param
     * @return
     */
    @RequestMapping("/getPeriodUserList")
    public ResponseBo getPeriodUserList(@RequestBody  QueryRequest request,@RequestParam String headerId) {

        List<Map<String,Object>> result=opService.getPeriodUserList((request.getPageNum()-1)*request.getPageSize()+1, request.getPageNum()*request.getPageSize(),headerId);

        int totalCount= opService.getPeriodUserListCount(headerId);
        return  ResponseBo.okOverPaging("",totalCount,result);
    }

    @RequestMapping("/getSpuStatis")
    public ResponseBo getSpuStatis(String touchDt) {
        List<Map<String, Object>> dataList = opService.getSpuStatis(touchDt);
        return ResponseBo.okWithData(null, dataList);
    }

    @RequestMapping("/getChartData")
    public ResponseBo getChartData(String touchDt, String type) {
        return ResponseBo.okWithData(null, opService.getChartData(touchDt, type));
    }

    @RequestMapping("/getPeriodSpuStatis")
    public ResponseBo getPeriodSpuStatis(String headerId) {
        List<Map<String, Object>> dataList = opService.getPeriodSpuStatis(headerId);
        return ResponseBo.okWithData(null, dataList);
    }

    @RequestMapping("/getPeriodChartData")
    public ResponseBo getPeriodChartData(String headerId, String type) {
        return ResponseBo.okWithData(null, opService.getPeriodChartData(headerId, type));
    }
}
