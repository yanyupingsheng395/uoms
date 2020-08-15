package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.linksteady.common.bo.UserBo;
import com.linksteady.common.domain.User;
import com.linksteady.common.util.FileUtils;
import com.linksteady.operate.dao.ManualDetailMapper;
import com.linksteady.operate.dao.ManualHeaderMapper;
import com.linksteady.operate.dao.PushLargeListMapper;
import com.linksteady.operate.domain.ManualDetail;
import com.linksteady.operate.domain.ManualHeader;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.exception.OptimisticLockException;
import com.linksteady.operate.service.ManualPushService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2019/12/25
 */
@Service
public class ManualPushServiceImpl implements ManualPushService {

    @Autowired
    private ManualHeaderMapper manualHeaderMapper;

    @Autowired
    private ManualDetailMapper manualDetailMapper;

    @Autowired
    private PushLargeListMapper pushLargeListMapper;

    @Override
    public int getHeaderListCount(String scheduleDate) {
        return manualHeaderMapper.getHeaderListCount(scheduleDate);
    }

    @Override
    public List<ManualHeader> getHeaderListData(int limit, int offset, String scheduleDate) {
        return manualHeaderMapper.getHeaderListData(limit,offset, scheduleDate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pushMessage(Long headId, String pushType) throws Exception{
        int count=manualHeaderMapper.updateStatusToPlaning(headId);
        if(count==0)
        {
            throw new OptimisticLockException();
        }
        // 立刻发送，更新当前schedule_date
        if (pushType.equalsIgnoreCase("1")) {
            ManualHeader manualHeader = new ManualHeader();
            manualHeader.setScheduleDate(new Date());
            manualHeader.setHeadId(headId);
            manualHeaderMapper.updateScheduleDate(manualHeader);
        }
        // 写入large表
        pushLargeListMapper.insertLargeDataByManual(headId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveManualData(String smsContent, MultipartFile file, String sendType, String pushDate) throws IOException, LinkSteadyException {
        // 保存header
        ManualHeader manualHeader = new ManualHeader();
        manualHeader.setFileName(file.getOriginalFilename());
        manualHeader.setInsertBy(((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername());
        manualHeader.setInsertDt(new Date());
        manualHeader.setPushType(sendType);
        manualHeader.setStatus("0");
        manualHeader.setSmsContent(smsContent);

        if (sendType.equalsIgnoreCase("0")) {
            manualHeader.setScheduleDate(
                    Date.from(LocalDateTime.parse(pushDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).atZone(ZoneId.systemDefault()).toInstant())
            );
        } else {
            manualHeader.setScheduleDate(new Date());
        }
        // 解析file
        List<String> mobiles = Lists.newArrayList();
        BufferedReader bufferedReader;
        try {
            File tmpFile = FileUtils.multipartFileToFile(file);
            if(tmpFile == null) {
                throw new RuntimeException("上传的文件为空！");
            }
            bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream(), Objects.requireNonNull(FileUtils.getCharSet(tmpFile))));
            FileUtils.deleteTempFile(tmpFile);
        }catch (Exception e) {
            throw new LinkSteadyException("文件解析异常！");
        }
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            mobiles.add(line);
        }
        bufferedReader.close();
        final long validCount = mobiles.stream().filter(m -> (m.length() == 11) && Pattern.matches("\\d+", m)).count();
        long totalCount = mobiles.size();
        manualHeader.setAllNum(totalCount);
        manualHeader.setValidNum(validCount);
        manualHeader.setUnvalidNum(totalCount - validCount);
        manualHeaderMapper.saveHeader(manualHeader);

        // 保存detail
        long headId = manualHeader.getHeadId();
        List<ManualDetail> manualDetails = Lists.newArrayList();
        mobiles.stream().filter(m -> (m.length() == 11) && Pattern.matches("\\d+", m)).forEach(x -> {
            ManualDetail manualDetail = new ManualDetail();
            manualDetail.setHeadId(headId);
            manualDetail.setPhoneNum(x);
            manualDetail.setPushStatus("P");
            manualDetails.add(manualDetail);
        });

        // 分批存储
        for (int limit = 1000, skip = 0; skip < manualDetails.size(); skip = skip + limit) {
            manualDetailMapper.saveDetailList(manualDetails.stream().skip(skip).limit(limit).collect(Collectors.toList()));
        }
    }

    @Override
    public Map<String, Object> getPushInfo(Long headId) {
        return manualHeaderMapper.getPushInfo(headId);
    }

    @Override
    public String getHeadStatus(Long headId) {
        return manualHeaderMapper.getHeadStatus(headId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteData(Long headId) {
        manualHeaderMapper.deleteData(headId);
        manualDetailMapper.deleteData(headId);
    }
}
