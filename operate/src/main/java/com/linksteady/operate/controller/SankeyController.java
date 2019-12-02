package com.linksteady.operate.controller;

import com.alibaba.fastjson.JSONObject;
import com.linksteady.operate.service.SankeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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

    @RequestMapping("/getSunIdList")
    public List<String> getSunIdList(String id) {
        return sankeyService.getSunIdList(id);
    }

    public static void main(String[] args) {
        String nodes = "{\"nodes\":[\n" +
                "          {\"name\":\"Agricultural 'waste'\"},\n" +
                "          {\"name\":\"Bio-conversion\"},\n" +
                "          {\"name\":\"Liquid\"},\n" +
                "          {\"name\":\"Losses\"},\n" +
                "          {\"name\":\"Solid\"},\n" +
                "          {\"name\":\"Gas\"},\n" +
                "          {\"name\":\"Biofuel imports\"},\n" +
                "          {\"name\":\"Biomass imports\"},\n" +
                "          {\"name\":\"Coal imports\"},\n" +
                "          {\"name\":\"Coal\"},\n" +
                "          {\"name\":\"Coal reserves\"},\n" +
                "          {\"name\":\"District heating\"},\n" +
                "          {\"name\":\"Industry\"},\n" +
                "          {\"name\":\"Heating and cooling - commercial\"},\n" +
                "          {\"name\":\"Heating and cooling - homes\"},\n" +
                "          {\"name\":\"Electricity grid\"},\n" +
                "          {\"name\":\"Over generation / exports\"},\n" +
                "          {\"name\":\"H2 conversion\"},\n" +
                "          {\"name\":\"Road transport\"},\n" +
                "          {\"name\":\"Agriculture\"},\n" +
                "          {\"name\":\"Rail transport\"},\n" +
                "          {\"name\":\"Lighting & appliances - commercial\"},\n" +
                "          {\"name\":\"Lighting & appliances - homes\"},\n" +
                "          {\"name\":\"Gas imports\"},\n" +
                "          {\"name\":\"Ngas\"},\n" +
                "          {\"name\":\"Gas reserves\"},\n" +
                "          {\"name\":\"Thermal generation\"},\n" +
                "          {\"name\":\"Geothermal\"},\n" +
                "          {\"name\":\"H2\"},\n" +
                "          {\"name\":\"Hydro\"},\n" +
                "          {\"name\":\"International shipping\"},\n" +
                "          {\"name\":\"Domestic aviation\"},\n" +
                "          {\"name\":\"International aviation\"},\n" +
                "          {\"name\":\"National navigation\"},\n" +
                "          {\"name\":\"Marine algae\"},\n" +
                "          {\"name\":\"Nuclear\"},\n" +
                "          {\"name\":\"Oil imports\"},\n" +
                "          {\"name\":\"Oil\"},\n" +
                "          {\"name\":\"Oil reserves\"},\n" +
                "          {\"name\":\"Other waste\"},\n" +
                "          {\"name\":\"Pumped heat\"},\n" +
                "          {\"name\":\"Solar PV\"},\n" +
                "          {\"name\":\"Solar Thermal\"},\n" +
                "          {\"name\":\"Solar\"},\n" +
                "          {\"name\":\"Tidal\"},\n" +
                "          {\"name\":\"UK land based bioenergy\"},\n" +
                "          {\"name\":\"Wave\"},\n" +
                "          {\"name\":\"Wind\"}\n" +
                "        ],\n" +
                "        \"links\":[\n" +
                "          {\"source\":0,\"target\":1,\"value\":124.729},\n" +
                "          {\"source\":1,\"target\":2,\"value\":0.597},\n" +
                "          {\"source\":1,\"target\":3,\"value\":26.862},\n" +
                "          {\"source\":1,\"target\":4,\"value\":280.322},\n" +
                "          {\"source\":1,\"target\":5,\"value\":81.144},\n" +
                "          {\"source\":6,\"target\":2,\"value\":35},\n" +
                "          {\"source\":7,\"target\":4,\"value\":35},\n" +
                "          {\"source\":8,\"target\":9,\"value\":11.606},\n" +
                "          {\"source\":10,\"target\":9,\"value\":63.965},\n" +
                "          {\"source\":9,\"target\":4,\"value\":75.571},\n" +
                "          {\"source\":11,\"target\":12,\"value\":10.639},\n" +
                "          {\"source\":11,\"target\":13,\"value\":22.505},\n" +
                "          {\"source\":11,\"target\":14,\"value\":46.184},\n" +
                "          {\"source\":15,\"target\":16,\"value\":104.453},\n" +
                "          {\"source\":15,\"target\":14,\"value\":113.726},\n" +
                "          {\"source\":15,\"target\":17,\"value\":27.14},\n" +
                "          {\"source\":15,\"target\":12,\"value\":342.165},\n" +
                "          {\"source\":15,\"target\":18,\"value\":37.797},\n" +
                "          {\"source\":15,\"target\":19,\"value\":4.412},\n" +
                "          {\"source\":15,\"target\":13,\"value\":40.858},\n" +
                "          {\"source\":15,\"target\":3,\"value\":56.691},\n" +
                "          {\"source\":15,\"target\":20,\"value\":7.863},\n" +
                "          {\"source\":15,\"target\":21,\"value\":90.008},\n" +
                "          {\"source\":15,\"target\":22,\"value\":93.494},\n" +
                "          {\"source\":23,\"target\":24,\"value\":40.719},\n" +
                "          {\"source\":25,\"target\":24,\"value\":82.233},\n" +
                "          {\"source\":5,\"target\":13,\"value\":0.129},\n" +
                "          {\"source\":5,\"target\":3,\"value\":1.401},\n" +
                "          {\"source\":5,\"target\":26,\"value\":151.891},\n" +
                "          {\"source\":5,\"target\":19,\"value\":2.096},\n" +
                "          {\"source\":5,\"target\":12,\"value\":48.58},\n" +
                "          {\"source\":27,\"target\":15,\"value\":7.013},\n" +
                "          {\"source\":17,\"target\":28,\"value\":20.897},\n" +
                "          {\"source\":17,\"target\":3,\"value\":6.242},\n" +
                "          {\"source\":28,\"target\":18,\"value\":20.897},\n" +
                "          {\"source\":29,\"target\":15,\"value\":6.995},\n" +
                "          {\"source\":2,\"target\":12,\"value\":121.066},\n" +
                "          {\"source\":2,\"target\":30,\"value\":128.69},\n" +
                "          {\"source\":2,\"target\":18,\"value\":135.835},\n" +
                "          {\"source\":2,\"target\":31,\"value\":14.458},\n" +
                "          {\"source\":2,\"target\":32,\"value\":206.267},\n" +
                "          {\"source\":2,\"target\":19,\"value\":3.64},\n" +
                "          {\"source\":2,\"target\":33,\"value\":33.218},\n" +
                "          {\"source\":2,\"target\":20,\"value\":4.413},\n" +
                "          {\"source\":34,\"target\":1,\"value\":4.375},\n" +
                "          {\"source\":24,\"target\":5,\"value\":122.952},\n" +
                "          {\"source\":35,\"target\":26,\"value\":839.978},\n" +
                "          {\"source\":36,\"target\":37,\"value\":504.287},\n" +
                "          {\"source\":38,\"target\":37,\"value\":107.703},\n" +
                "          {\"source\":37,\"target\":2,\"value\":611.99},\n" +
                "          {\"source\":39,\"target\":4,\"value\":56.587},\n" +
                "          {\"source\":39,\"target\":1,\"value\":77.81},\n" +
                "          {\"source\":40,\"target\":14,\"value\":193.026},\n" +
                "          {\"source\":40,\"target\":13,\"value\":70.672},\n" +
                "          {\"source\":41,\"target\":15,\"value\":59.901},\n" +
                "          {\"source\":42,\"target\":14,\"value\":19.263},\n" +
                "          {\"source\":43,\"target\":42,\"value\":19.263},\n" +
                "          {\"source\":43,\"target\":41,\"value\":59.901},\n" +
                "          {\"source\":4,\"target\":19,\"value\":0.882},\n" +
                "          {\"source\":4,\"target\":26,\"value\":400.12},\n" +
                "          {\"source\":4,\"target\":12,\"value\":46.477},\n" +
                "          {\"source\":26,\"target\":15,\"value\":525.531},\n" +
                "          {\"source\":26,\"target\":3,\"value\":787.129},\n" +
                "          {\"source\":26,\"target\":11,\"value\":79.329},\n" +
                "          {\"source\":44,\"target\":15,\"value\":9.452},\n" +
                "          {\"source\":45,\"target\":1,\"value\":182.01},\n" +
                "          {\"source\":46,\"target\":15,\"value\":19.013},\n" +
                "          {\"source\":47,\"target\":15,\"value\":289.366}\n" +
                "        ]}";

        JSONObject parse = JSONObject.parseObject(nodes);
        System.out.println(parse.getString("nodes"));
    }
}
