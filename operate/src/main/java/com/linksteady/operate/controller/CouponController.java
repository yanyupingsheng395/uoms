package com.linksteady.operate.controller;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.service.impl.CouponServiceImpl;
import com.linksteady.operate.service.impl.ShortUrlServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
/**
 * 短信相关的controller
 *
 * @author huang
 */
@RestController
@RequestMapping("/coupon")
@Slf4j
public class CouponController extends BaseController {

    @Autowired
    CouponServiceImpl couponService;

    @Autowired
    ShortUrlServiceImpl shortUrlService;

    /**
     * 获取券list
     *
     * @param
     * @return
     */
    @RequestMapping("/couponList")
    public ResponseBo couponList(Integer limit, Integer offset) {
        couponService.validCoupon();
        List<CouponInfo> result = couponService.getList(limit, offset);
        int totalCount = couponService.getTotalCount();
        return ResponseBo.okOverPaging("", totalCount, result);
    }

    /**
     * 获取某个groupId下的couponIds
     *
     * @param groupId
     * @return
     */
    @RequestMapping("/getCouponIdsByGroupId")
    public ResponseBo getCouponIdsByGroupId(String groupId) {
        List<Integer> ids = couponService.getCouponIdsByGroupId(groupId);
        return ResponseBo.okWithData(null, ids);
    }


    @RequestMapping("/save")
    public ResponseBo save(CouponInfo couponInfo) {
        couponInfo.setCouponSource("1");
        couponInfo = getCheckInfo(couponInfo);
        couponService.save(couponInfo);
        return ResponseBo.ok();
    }

    /**
     * 对优惠券的验证
     * 1.券链接是否配置
     * 2.适用人群是否配置
     * 3.券设置了失效
     * @param couponInfo
     * @return
     */
    private CouponInfo getCheckInfo(CouponInfo couponInfo) {
        // 默认为验证通过
        couponInfo.setCheckFlag("1");
        String couponUrl = couponInfo.getCouponUrl();
        // 链接为空
        if(StringUtils.isEmpty(couponUrl)) {
            couponInfo.setCheckFlag("0");
            couponInfo.setCheckComments("补贴链接没有配置");
        }
        // 券的有效状态
        String validStatus = couponInfo.getValidStatus();
        if(StringUtils.isNotEmpty(validStatus) && validStatus.equalsIgnoreCase("N")) {
            couponInfo.setCheckFlag("0");
            couponInfo.setCheckComments("补贴已失效");
        }
        return couponInfo;
    }

    @RequestMapping("/update")
    public ResponseBo update(CouponInfo couponInfo) {
        //对券进行校验
        couponInfo = getCheckInfo(couponInfo);
        //保存
        couponService.update(couponInfo);
        //保存完后对所有优惠券做个校验

        return ResponseBo.ok();
    }

    /**
     * 根据couponId获取记录
     *
     * @param couponId
     * @return
     */
    @RequestMapping("/getByCouponId")
    public ResponseBo getByCouponId(String couponId) {
        CouponInfo couponInfo = couponService.getByCouponId(couponId);
        return ResponseBo.okWithData(null, couponInfo);
    }

    /**
     * 获取短链
     * @return
     */
    @RequestMapping("/getShortUrl")
    public ResponseBo getShortUrl(String url) {
        String shortUrl;
        if (StringUtils.isNotEmpty(url)) {
            shortUrl = shortUrlService.genConponShortUrl(url,"S");
            if("error".equalsIgnoreCase(shortUrl)) {
                return ResponseBo.error("生成短链错误！");
            }
        }else {
            return ResponseBo.error("长链地址不合法！");
        }
        return ResponseBo.okWithData(null, shortUrl);
    }

    /**
     * 删除组-券关系
     * @param
     * @return
     */
    @RequestMapping("/deleteCoupon")
    public ResponseBo deleteCoupon(@RequestParam("couponId") String couponId) {
        String msg = "";
        if(StringUtils.isNotEmpty(couponId)) {
            List<String> ids = Arrays.asList(couponId.split(","));
            //已被引用的券IDs
            List<String> couponIds = couponService.isCouponUsed(ids);
            //未被引用的券IDs
            List<String> others = ids.stream().filter(x->!couponIds.contains(x)).collect(Collectors.toList());
            if(others.size() != 0) {
                //判断是否被历史引用过，如果是则删除，否则打上失效标记
                couponService.deleteCoupon(others);
            }

            //有可被删除的券
            if(others.size()>0)
            {
                if(couponIds.size()>0) {

                    return ResponseBo.ok("部分删除成功！"+couponIds.size()+"条记录被引用，无法删除。");
                }else {
                    return ResponseBo.ok("删除成功！");
                }
            }else
            {
                //未被引用的券为空
                return ResponseBo.error(couponIds.size()+"条记录被引用，无法删除！");
            }

        }else {
            return ResponseBo.error();
        }

    }

    @RequestMapping("/deleteCouponGroup")
    public ResponseBo deleteCouponGroup(@RequestParam("groupId") String groupId) {
        couponService.deleteCouponGroup(groupId);
        return ResponseBo.ok();
    }

    /**
     * 智能补贴弹出面板-保存
     * @return
     */
    @GetMapping("/getCalculatedCoupon")
    public ResponseBo getCalculatedCoupon(@RequestParam String coupon) {
        List<CouponInfo> dataList = JSONObject.parseArray(coupon, CouponInfo.class);
        couponService.getCalculatedCoupon(dataList);
        return ResponseBo.ok();
    }

    /**
     * 验证券名称是否相同
     * @return
     */
    @RequestMapping("/checkCouponName")
    public boolean checkCouponName(@RequestParam("couponDisplayName") String couponDisplayName, @RequestParam("operate") String operate) {
        if("update".equalsIgnoreCase(operate)) {
            return true;
        }else {
            return couponService.checkCouponName(couponDisplayName) == 0;
        }
    }

    /**
     * 验证券信息
     * @return
     */
    @RequestMapping("/validCoupon")
    public ResponseBo validCoupon() {
        couponService.validCoupon();
        return ResponseBo.ok();
    }

    /**
     * 获取智能券
     * @return
     */
    @GetMapping("/getIntelCouponList")
    public List<CouponInfo> getIntelCouponList() {
        return couponService.getIntelCouponList();
    }

}