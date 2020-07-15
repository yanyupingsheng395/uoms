package com.linksteady.operate.controller;

import com.google.common.collect.Lists;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.QywxContactWay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
@Controller
public class QywxContactWayController {

    private static  List<QywxContactWay> dataList= Lists.newArrayList();

    static{
        dataList.add(
                new QywxContactWay(1L,"https://wework.qpic.cn/wwpic/704265_ITkRKdZOSM2NOTN_1594712420/0",
                        "1","这是备注","线下门店专用1","wake","1")
        );
        dataList.add(
                new QywxContactWay(2L,"https://wework.qpic.cn/wwpic/704265_ITkRKdZOSM2NOTN_1594712420/0",
                        "1","这是备注","线下门店专用2","HuangKun","1")
        );
        dataList.add(
                new QywxContactWay(3L,"https://wework.qpic.cn/wwpic/845767_PP6-s1wxRCuNOOD_1594713419/0",
                        "2","这是备注","线下门店专用3","HuangKun,wake,brandonz","1")
        );
        dataList.add(
                new QywxContactWay(4L,"https://wework.qpic.cn/wwpic/845767_PP6-s1wxRCuNOOD_1594713419/0",
                        "1","这是备注","线下门店专用4","HuangKun","1")
        );
        dataList.add(
                new QywxContactWay(5L,"https://wework.qpic.cn/wwpic/704265_ITkRKdZOSM2NOTN_1594712420/0",
                        "1","这是备注","线下门店专用5","HuangKun","1")
        );
    }

    /**
     * 获取所有的活码列表
     *
     * @return
     */
    @GetMapping("/contactWay/getContactWayData")
    @ResponseBody
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
    @GetMapping("/contactWay/getList")
    @ResponseBody
    public ResponseBo getHeadList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        String qstate = request.getParam().get("qstate");
        String qremark=request.getParam().get("qremark");
        int count =QywxContactWayController.dataList.size();
        Collections.sort(dataList);
        return ResponseBo.okOverPaging("", count,dataList);
    }



    @RequestMapping("/contactWay/getContactWayById")
    @ResponseBody
    public ResponseBo getContactWayById(String contactWayId) {
        Optional<QywxContactWay> qywxContactWay=dataList.stream().filter(p->p.getContactWayId()==Long.parseLong(contactWayId)).findFirst();
        return ResponseBo.okWithData(null, qywxContactWay.get());
    }

    /**
     * 更新
     */
    @RequestMapping("/contactWay/update")
    @ResponseBody
    public ResponseBo update(QywxContactWay qywxContactWay) {
        Long  id=qywxContactWay.getContactWayId();
        Optional<QywxContactWay> optionalQywxContactWay=dataList.stream().filter(p->p.getContactWayId().equals(id)).findFirst();

        if(optionalQywxContactWay.isPresent())
        {
            optionalQywxContactWay.get().setCreateDt(new Date());
            optionalQywxContactWay.get().setState(qywxContactWay.getState());
            optionalQywxContactWay.get().setRemark(qywxContactWay.getRemark());
            optionalQywxContactWay.get().setUsersList(qywxContactWay.getUsersList());

            if(qywxContactWay.getUsersList().indexOf(",")!=-1)
            {
                optionalQywxContactWay.get().setType("2");
            }else
            {
                optionalQywxContactWay.get().setType("1");
            }
        }

        return ResponseBo.ok();
    }

    /**
     * 新增
     */
    @RequestMapping("/contactWay/save")
    @ResponseBody
    public ResponseBo save(QywxContactWay qywxContactWay) {

        int id=dataList.size()+1;
        qywxContactWay.setContactWayId((long)id);
        if(qywxContactWay.getUsersList().indexOf(",")!=-1)
        {
            qywxContactWay.setType("2");
        }else
        {
            qywxContactWay.setType("1");
        }

        qywxContactWay.setQrCode("https://wework.qpic.cn/wwpic/704265_ITkRKdZOSM2NOTN_1594712420/0");

        qywxContactWay.setCreateDt(new Date());

        //默认生成一个短链
        dataList.add(qywxContactWay);
        return ResponseBo.ok();
    }


    /**
     * 更新渠道活码对应的短链接
     * @param contactWayId
     * @param shortUrl
     * @return
     */
    @RequestMapping("/contactWay/updateShortUrl")
    @ResponseBody
    public ResponseBo updateShortUrl(Long contactWayId,String shortUrl) {
        Optional<QywxContactWay> optionalQywxContactWay=dataList.stream().filter(p->p.getContactWayId().equals(contactWayId)).findFirst();
        if(optionalQywxContactWay.isPresent())
        {
            optionalQywxContactWay.get().setShortUrl(shortUrl);
            optionalQywxContactWay.get().setCreateDt(new Date());
        }
        return ResponseBo.ok();
    }

    @RequestMapping("/test/qrCode")
    public String mappingToQrCode()
    {
        return "operate/contactWay/qrCode";
    }
}
