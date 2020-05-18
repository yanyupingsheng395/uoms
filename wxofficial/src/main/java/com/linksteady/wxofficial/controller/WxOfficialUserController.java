package com.linksteady.wxofficial.controller;

import com.alibaba.fastjson.JSON;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.wxofficial.common.wechat.service.OperateService;
import com.linksteady.wxofficial.config.WxProperties;
import com.linksteady.wxofficial.entity.bo.UserTagBo;
import com.linksteady.wxofficial.entity.vo.WxOfficialUserVo;
import com.linksteady.wxofficial.entity.vo.WxTagVo;
import com.linksteady.wxofficial.service.WxOfficialUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2020/4/20
 */
@RestController
@RequestMapping("/wxOfficialUser")
@Slf4j
public class WxOfficialUserController {

    @Autowired
    private WxOfficialUserService wxOfficialUserService;

    @Autowired
    private OperateService operateService;

    @Autowired
    private WxProperties wxProperties;

    @GetMapping("/getDataListPage")
    public ResponseBo getDataListPage(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        int count = wxOfficialUserService.getDataCount();
        List<WxOfficialUserVo> dataList = wxOfficialUserService.getDataList(limit, offset);
        List<WxTagVo> tagList = getTagList();
        Map<Long, String> tagMap = tagList.stream().collect(Collectors.toMap(WxTagVo::getId, WxTagVo::getName));
        dataList.stream().filter(v -> StringUtils.isNotEmpty(v.getTagidList())).forEach(x -> {
            List<Long> tagIds = Arrays.asList(x.getTagidList().split(",")).stream().map(Long::valueOf).collect(Collectors.toList());
            List<String> tagNames = tagIds.stream().map(tagMap::get).collect(Collectors.toList());
            x.setTagNames(StringUtils.join(tagNames, ","));
        });
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    private List<WxTagVo> getTagList() {
        String url = wxProperties.getServiceDomain() + wxProperties.getTagListUrl();
        String result = operateService.getDataList(url);
        return JSON.parseArray(JSON.parseObject(result).getString("msg"), WxTagVo.class);
    }

    /**
     * 给用户打标签
     *
     * @return
     */
    @PostMapping("/updateTagIds")
    public ResponseBo updateTagIds(String data) {
        List<UserTagBo> dataList = JSON.parseArray(data, UserTagBo.class);
        wxOfficialUserService.updateTagIds(dataList);
        return ResponseBo.ok();
    }

    /**
     * 给用户打标签
     *
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
