package com.linksteady.wxofficial.service.impl;

import com.linksteady.wxofficial.dao.WxUserFollowMapper;
import com.linksteady.wxofficial.entity.po.WxFollowReply;
import com.linksteady.wxofficial.service.WxUserFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author hxcao
 * @date 2020/4/24
 */
@Service
public class WxUserFollowServiceImpl implements WxUserFollowService {

    @Autowired
    private WxUserFollowMapper wxUserFollowMapper;

    @Override
    public void saveData(WxFollowReply wxFollowReply) {
        wxUserFollowMapper.saveData(wxFollowReply);
    }

    @Override
    public List<WxFollowReply> getDataList() {
        return wxUserFollowMapper.getDataList();
    }

    @Override
    public WxFollowReply getDataById(String id) throws UnsupportedEncodingException {
        WxFollowReply wxFollowReply = wxUserFollowMapper.getDataById(id);
        byte[] content = wxFollowReply.getContent();
        if(null != content && content.length > 0) {
            wxFollowReply.setContentStr(new String(content, "utf-8"));
        }
        return wxFollowReply;
    }

    @Override
    public void deleteById(String id) {
        wxUserFollowMapper.deleteById(id);
    }

    @Override
    public void updateData(WxFollowReply wxFollowReply) {
        wxUserFollowMapper.updateData(wxFollowReply);
    }
}
