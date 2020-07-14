package com.linksteady.operate.controller;

import com.google.common.collect.Lists;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.QywxContactWay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 外部联系方式
 * @author huang
 */
@Slf4j
@RestController
@RequestMapping("/contactWay")
public class QywxContactWayController {

    private static  List<QywxContactWay> dataList= Lists.newArrayList();

    static{
        dataList.add(
                new QywxContactWay(1L,"https://wework.qpic.cn/wwpic/704265_ITkRKdZOSM2NOTN_1594712420/0",
                        "1","这是备注","线下门店专用","wake","1")
        );
        dataList.add(
                new QywxContactWay(2L,"https://wework.qpic.cn/wwpic/704265_ITkRKdZOSM2NOTN_1594712420/0",
                        "1","这是备注","线下门店专用","HuangKun","1")
        );
        dataList.add(
                new QywxContactWay(3L,"https://wework.qpic.cn/wwpic/845767_PP6-s1wxRCuNOOD_1594713419/0",
                        "2","这是备注","线下门店专用","HuangKun,wake,brandonz","1")
        );
        dataList.add(
                new QywxContactWay(4L,"https://wework.qpic.cn/wwpic/845767_PP6-s1wxRCuNOOD_1594713419/0",
                        "1","这是备注","线下门店专用","HuangKun","1")
        );
        dataList.add(
                new QywxContactWay(5L,"https://wework.qpic.cn/wwpic/704265_ITkRKdZOSM2NOTN_1594712420/0",
                        "1","这是备注","线下门店专用","HuangKun","1")
        );
    }

    /**
     * 获取所有的活码列表
     *
     * @return
     */
    @GetMapping("/getContactWayData")
    public ResponseBo getHeadList() {
        Collections.sort(dataList);
        int count =dataList.size();
        return ResponseBo.okOverPaging("", count,dataList);
    }


    /**
     * 获取列表
     *
     * @param request
     * @return
     */
    @GetMapping("/getList")
    public ResponseBo getHeadList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        String qstate = request.getParam().get("qstate");
        String qremark=request.getParam().get("qremark");
        int count =QywxContactWayController.dataList.size();
        Collections.sort(dataList);
        return ResponseBo.okOverPaging("", count,dataList);
    }



    @RequestMapping("/getContactWayById")
    public ResponseBo getContactWayById(String contactWayId) {
        Optional<QywxContactWay> qywxContactWay=dataList.stream().filter(p->p.getId()==Long.parseLong(contactWayId)).findFirst();
        return ResponseBo.okWithData(null, qywxContactWay.get());
    }

    /**
     * 更新
     */
    @RequestMapping("/update")
    public ResponseBo update(QywxContactWay qywxContactWay) {
        Long  id=qywxContactWay.getId();
        Optional<QywxContactWay> optionalQywxContactWay=dataList.stream().filter(p->p.getId().equals(id)).findFirst();

        if(optionalQywxContactWay.isPresent())
        {
            optionalQywxContactWay.get().setCreateDt(new Date());
        }

        return ResponseBo.ok();
    }

    /**
     * 新增
     */
    @RequestMapping("/save")
    public ResponseBo save(QywxContactWay qywxContactWay) {

        int id=dataList.size()+1;
        qywxContactWay.setId((long)id);
        if(qywxContactWay.getUsersList().indexOf(",")!=-1)
        {
            qywxContactWay.setType("2");
        }else
        {
            qywxContactWay.setType("1");
        }

        qywxContactWay.setQrCode("https://wework.qpic.cn/wwpic/704265_ITkRKdZOSM2NOTN_1594712420/0");

        qywxContactWay.setCreateDt(new Date());
        dataList.add(qywxContactWay);
        return ResponseBo.ok();
    }
}
