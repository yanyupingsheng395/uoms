package com.linksteady.operate.controller;

import com.linksteady.operate.service.SankeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-12-02
 */
@RestController
@RequestMapping("/sankey")
public class SankeyController {

    @Autowired
    private SankeyService sankeyService;
    /**
     * 获取spu的桑基图
     * @return
     */
    @RequestMapping("/getSpuSnakey")
    public Map<String, Object> getSpuSnakey() {
        return sankeyService.getSpuList();
    }
}
