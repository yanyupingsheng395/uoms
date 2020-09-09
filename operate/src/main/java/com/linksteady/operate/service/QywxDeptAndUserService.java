package com.linksteady.operate.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/8/27
 */
public interface QywxDeptAndUserService {

    void uploadData(MultipartFile file, String userName);

    List<Map<String, Object>> getUserTableData(Integer limit, Integer offset);

    List<Map<String, Object>> getDeptTableData(Integer limit, Integer offset);

    int getUserTableCount();

    int getDeptTableCount();
}
