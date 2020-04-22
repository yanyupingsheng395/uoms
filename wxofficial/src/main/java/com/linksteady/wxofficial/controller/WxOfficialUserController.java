package com.linksteady.wxofficial.controller;
import com.alibaba.fastjson.JSON;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.wxofficial.entity.bo.UserTagBo;
import com.linksteady.wxofficial.entity.vo.WxOfficialUserVo;
import com.linksteady.wxofficial.service.WxOfficialUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/4/20
 */
@RestController
@RequestMapping("/wxOfficialUser")
public class WxOfficialUserController {

    @Autowired
    private WxOfficialUserService wxOfficialUserService;

    @GetMapping("/getDataListPage")
    public ResponseBo getDataListPage(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        int count = wxOfficialUserService.getDataCount();
        List<WxOfficialUserVo> dataList = wxOfficialUserService.getDataList(limit, offset);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 给用户打标签
     * @return
     */
    @PostMapping("/updateTagIds")
    public ResponseBo updateTagIds(String data)  {
        List<UserTagBo> dataList = JSON.parseArray(data, UserTagBo.class);
        wxOfficialUserService.updateTagIds(dataList);
        return ResponseBo.ok();
    }

    /**
     * 给用户打标签
     * @return
     */
    @PostMapping("/updateRemark")
    public ResponseBo updateRemark(String userId, String remark) {
        wxOfficialUserService.updateRemark(userId, remark);
        return ResponseBo.ok();
    }

    @GetMapping("/getUserById")
    public ResponseBo getUserById(String userId) {
        return ResponseBo.okWithData(null, wxOfficialUserService.getUserById(userId));
    }
}
