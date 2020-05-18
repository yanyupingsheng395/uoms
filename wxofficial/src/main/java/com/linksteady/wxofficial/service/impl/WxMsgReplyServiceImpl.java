package com.linksteady.wxofficial.service.impl;

import com.linksteady.wxofficial.dao.WxMsgReplyMapper;
import com.linksteady.wxofficial.entity.po.WxFollowReply;
import com.linksteady.wxofficial.entity.po.WxMsgReply;
import com.linksteady.wxofficial.service.WxMsgReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author hxcao
 * @date 2020/4/24
 */
@Service
public class WxMsgReplyServiceImpl implements WxMsgReplyService {

    @Autowired
    private WxMsgReplyMapper wxMsgReplyMapper;

    @Override
    public void saveData(WxMsgReply wxMsgReply) {
        wxMsgReplyMapper.saveData(wxMsgReply);
    }

    @Override
    public List<WxMsgReply> getDataList() {
        return wxMsgReplyMapper.getDataList();
    }

    @Override
    public WxMsgReply getDataById(String id) throws UnsupportedEncodingException {
        WxMsgReply wxMsgReply = wxMsgReplyMapper.getDataById(id);
        byte[] content = wxMsgReply.getContent();
        if(null != content && content.length > 0) {
            wxMsgReply.setContentStr(new String(content, "utf-8"));
        }
        return wxMsgReply;
    }

    @Override
    public void deleteById(String id) {
        wxMsgReplyMapper.deleteById(id);
    }

    @Override
    public void updateData(WxMsgReply wxMsgReply) {
        wxMsgReplyMapper.updateData(wxMsgReply);
    }
}
