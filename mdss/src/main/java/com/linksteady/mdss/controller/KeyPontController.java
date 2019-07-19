package com.linksteady.mdss.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.mdss.service.KeyPointService;
import com.linksteady.mdss.vo.KeyPointMonthVO;
import com.linksteady.mdss.vo.KeyPointYearVO;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 关键指标完成情况相关的controller
 */
@RestController
@RequestMapping("/keyPoint")
public class KeyPontController  extends BaseController {

    @Autowired
    private KeyPointService keyPointService;
    @Autowired
    private DozerBeanMapper dozerBeanMapper;

    /**
     * 获取月的关键指标详情
     * @param month  月份ID
     * @return
     */
    @RequestMapping("/getMonthDetail")
    public ResponseBo getMonthDetail(@RequestParam("month") String month) {
        KeyPointMonthVO vo=dozerBeanMapper.map(keyPointService.getKeyPointMonthData(month), KeyPointMonthVO.class);

        return  ResponseBo.ok(vo);
    }

    /**
     * 月按天的GMV值和去年同期的对比
     * @param month  月份ID
     * @return
     */
    @RequestMapping("/getGMVByDay")
    public ResponseBo getGMVByDay(@RequestParam("month") String month) {
        List<Map<String,Object>> cur=keyPointService.getGMVByDay(month);

        //获取去年的月份
        String lastmonth=String.valueOf(Integer.parseInt(month.substring(0,4))-1)+month.substring(4,6);
        List<Map<String,Object>> last=keyPointService.getGMVByDay(lastmonth);

        Map<String,List<Map<String,Object>>> obj= Maps.newHashMap();
        obj.put("cur",cur);
        obj.put("pre",last);

        return ResponseBo.ok(obj);
    }

    /**
     * 获取年的关键指标详情
     * @param year  年ID
     * @return
     */
    @RequestMapping("/getYearDetail")
    @ResponseBody
    public ResponseBo getYearDetail(@RequestParam("year") String year) {
        KeyPointYearVO vo=dozerBeanMapper.map(keyPointService.getKeyPointYearData(year), KeyPointYearVO.class);
        return ResponseBo.ok(vo);
    }

    /**
     * 本年、去年按月的GMV的趋势
     * @param year  年ID
     * @return
     */
    @RequestMapping("/getGMVTrendByMonth")
    public ResponseBo getGMVTrendByMonth(@RequestParam("year") String year) {
        //获取当年个月的GVM
        List<Map<String,Object>> cur=keyPointService.getGMVTrendByMonth(year);
        //获取去年各月的GMV
        String preYear=String.valueOf(Integer.parseInt(year)-1);
        List<Map<String,Object>> pre=keyPointService.getGMVTrendByMonth(preYear);

        Map<String,List<Map<String,Object>>> obj= Maps.newHashMap();
        obj.put("cur",cur);
        obj.put("pre",pre);

        return ResponseBo.ok(obj);
    }

    /**
     * 本年各月的GMV、目标对比数据
     * @param year  年ID
     * @return
     */
    @RequestMapping("/getGMVCompareByMonth")
    public ResponseBo getGMVCompareByMonth(@RequestParam("year") String year) {
         return ResponseBo.ok(keyPointService.getGMVCompareByMonth(year));
    }

    /**
     * 本年各月的利润率的变化曲线
     * @param year  年ID
     * @return
     */
    @RequestMapping("/getProfitRateByMonth")
    public ResponseBo getProfitRateByMonth(@RequestParam("year") String year) {
        return ResponseBo.ok(keyPointService.getProfitRateByMonth(year));
    }

    /**
     * 本月是否维护了成本及利润率数据
     * @param month  月ID
     * @return
     */
    @RequestMapping("/isFixProfitByMonth")
    public ResponseBo isFixProfitByMonth(@RequestParam("month") String month) {
       //Y表示已维护 N表示未维护
        return ResponseBo.ok(keyPointService.isFixProfitByMonth(month));
    }

    /**
     * 年度是否维护了成本及利润率数据
     * @param year  年ID
     * @return
     */
    @RequestMapping("/isFixProfitByYear")
    public ResponseBo isFixProfitByYear(@RequestParam("year") String year) {
        //Y表示已维护 N表示未维护
        return ResponseBo.ok(keyPointService.isFixProfitByYear(year));
    }

    /**
     * 获取预警及合理化建议信息
     * @param periodtype  周期类型 可选值有month 和 year
     * @param periodvalue 周期值 为yearid或monthid，与periodtype一起使用
     * @return
     */
    @RequestMapping("/getKeypointHint")
    public ResponseBo getKeypointHint(@RequestParam("periodtype") String periodtype,@RequestParam("periodvalue") String periodvalue) {
        return ResponseBo.ok(keyPointService.getKeypointHint(periodtype,periodvalue));
    }


}
