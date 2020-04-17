package com.linksteady.wxofficial.controller;

import com.google.common.collect.Lists;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.wxofficial.entity.ImageText;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/4/15
 */
@RestController
@RequestMapping("/material")
public class MaterialController {

    @RequestMapping("/getDataList")
    public ResponseBo getDataList(QueryRequest queryRequest) {
        int limit = queryRequest.getLimit();
        int offset = queryRequest.getOffset();
        List<ImageText> dataList = Lists.newArrayList();
        dataList.add(new ImageText("1", "2"));
        dataList.add(new ImageText("1", "2"));
        dataList.add(new ImageText("1", "2"));
        return ResponseBo.okOverPaging(null, 3, dataList);
    }
}
