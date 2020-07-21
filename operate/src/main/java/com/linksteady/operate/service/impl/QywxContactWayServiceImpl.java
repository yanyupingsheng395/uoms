package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.dao.QywxContactWayMapper;
import com.linksteady.operate.domain.QywxContactWay;
import com.linksteady.operate.domain.enums.ConfigEnum;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.QywxContactWayService;
import com.linksteady.operate.service.ShortUrlService;
import com.linksteady.operate.util.OkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * @author huang
 * @date 2020/7/17
 * 渠道活码的服务类
 */
@Service
@Slf4j
public class QywxContactWayServiceImpl implements QywxContactWayService {

    /**
     * 增加渠道活码接口地址
     */
    private static final String ADD_CONTACT_WAY="/api/addContactWay";
    /**
     * 更新渠道活码的接口地址
     */
    private static final String UPDATE_CONTACT_WAY="/api/updateContactWay";
    /**
     * 删除渠道活码的接口地址
     */
    private static final String DELETE_CONTACT_WAY="/api/deleteContactWay";
    /**
     * 获取渠道活码的详细信息
     */
    private static final String GET_CONTACT_WAY="/api/getContactWay";

    /**
     * 渠道活码展示页的详细地址
     */
    private static final String CONTACT_DETAIL_URL="/images/qrcode";


    @Autowired
    ConfigService configService;

    @Autowired
    QywxContactWayMapper qywxContactWayMapper;

    @Autowired
    ShortUrlService shortUrlService;

    @Override
    @Transactional
    public void saveContactWay(QywxContactWay qywxContactWay) throws Exception{
        //保存渠道活码
        qywxContactWayMapper.saveContactWay(qywxContactWay);
        Long contactWayId=qywxContactWay.getContactWayId();
        String url = configService.getValueByName(ConfigEnum.qywxDomainUrl.getKeyCode()) + ADD_CONTACT_WAY;
        String param = JSON.toJSONString(qywxContactWay);
        //构造数据，请求企业微信端 生成渠道码
        String result = OkHttpUtil.postRequestByJson(url, param);
        log.debug("请求获取渠道活码的url:{},参数:{},返回的结果{}", url, param, result);

        JSONObject jsonObject = JSON.parseObject(result);
        if (null != jsonObject && "0".equalsIgnoreCase(jsonObject.getString("errcode"))) {
            String configId = jsonObject.getString("config_id");
            qywxContactWay.setConfigId(configId);

            JSONObject contactDetail=getContactWayByConfigId(qywxContactWay.getConfigId());
            //更新configId获取一次二维码的地址
            String qrCode = contactDetail.getString("qr_code");;
            qywxContactWay.setQrCode(qrCode);

            //获取当前应用的域名
            String currentDomain=qywxContactWayMapper.getCurrentDomain();
            if(StringUtils.isEmpty(currentDomain))
            {
                throw new LinkSteadyException("新增渠道活码失败,无法生成短链！");
            }
            //获取渠道活码对应的二维码链接
            String shortUrl =shortUrlService.genShortUrlDirect(currentDomain+CONTACT_DETAIL_URL+"/"+configId,"S");
            qywxContactWay.setShortUrl(shortUrl);
            qywxContactWayMapper.updateContactWayFullInfo(contactWayId,configId,qrCode,shortUrl,qywxContactWay.getUpdateBy());
        }else
        {
            throw new LinkSteadyException("新增渠道活码失败！");
        }
    }

    @Override
    @Transactional
    public void updateContractWay(QywxContactWay qywxContactWay) throws Exception{
        //更新数据库
        qywxContactWayMapper.updateContractWay(qywxContactWay);
        String url = configService.getValueByName(ConfigEnum.qywxDomainUrl.getKeyCode()) + UPDATE_CONTACT_WAY;
        String param = JSON.toJSONString(qywxContactWay);

        //构造数据，请求企业微信端 生成渠道码
        String result = OkHttpUtil.postRequestByJson(url, param);
        log.debug("请求更新渠道活码的url:{},参数:{},返回的结果{}", url, param, result);

        JSONObject jsonObject = JSON.parseObject(result);
        if (null != jsonObject && "0".equalsIgnoreCase(jsonObject.getString("errcode"))) {
            //获取二维码
            JSONObject contactDetail=getContactWayByConfigId(qywxContactWay.getConfigId());
            String qrCode = contactDetail.getString("qr_code");

            qywxContactWayMapper.updateContactWayQrCode(qywxContactWay.getContactWayId(),qrCode,qywxContactWay.getUpdateBy());
        }else
        {
            throw new LinkSteadyException("更新失败！");
        }


    }

    @Override
    public int getContactWayCount(String qstate, String qremark) {
        return qywxContactWayMapper.getContactWayCount(qstate,qremark);
    }

    @Override
    public List<QywxContactWay> getContactWayList() {
        return qywxContactWayMapper.getAllContactWayList();
    }

    @Override
    public List<QywxContactWay> getContactWayList(int limit,int offset,String qstate, String qremark) {
        return qywxContactWayMapper.getContactWayList(limit,offset,qstate,qremark);
    }

    @Override
    public QywxContactWay getContactWayById(Long contactWayId) {
        return qywxContactWayMapper.getContactWayById(contactWayId);
    }

    /**
     * 根据configId从企业微信获取渠道活码的详细信息
     * @param configId
     * @return
     */
    @Override
    public JSONObject getContactWayByConfigId(String configId) {
        String url = configService.getValueByName(ConfigEnum.qywxDomainUrl.getKeyCode()) + GET_CONTACT_WAY;
        String param = JSON.toJSONString(configId);

        //构造数据，请求企业微信端 生成渠道码
        String result = OkHttpUtil.postRequestByJson(url, param);
        log.debug("请求获取渠道活码详细信息的url:{},参数:{},返回的结果{}", url, param, result);

        JSONObject jsonObject = JSON.parseObject(result);
        if (null != jsonObject && "0".equalsIgnoreCase(jsonObject.getString("errcode")))
        {
            return jsonObject.getJSONObject("contact_way");
        }else
        {
            return new JSONObject();
        }
    }

    @Override
    public void updateShortUrl(Long contactWayId, String shortUrl,String updateBy) {
        qywxContactWayMapper.updateShortUrl(contactWayId,shortUrl,updateBy);
    }

    @Override
    @Transactional
    public void deleteContactWay(String configId) throws Exception{
        //发送到企业微信端进行删除
        String url = configService.getValueByName(ConfigEnum.qywxDomainUrl.getKeyCode()) + DELETE_CONTACT_WAY;
        String param = JSON.toJSONString(configId);

        //构造数据，请求企业微信端 生成渠道码
        String result = OkHttpUtil.postRequestByJson(url, param);
        log.debug("请求删除渠道活码的url:{},参数:{},返回的结果{}", url, param, result);

        JSONObject jsonObject = JSON.parseObject(result);
        if (null != jsonObject && "0".equalsIgnoreCase(jsonObject.getString("errcode")))
        {
            //从数据库进行删除
            qywxContactWayMapper.deleteContactWay(configId);
        }else
        {
            throw new LinkSteadyException("删除失败！");
        }
    }

    @Override
    public String getQrcodeByConfigId(String configId) {
        return qywxContactWayMapper.getQrcodeByConfigId(configId);
    }
}
