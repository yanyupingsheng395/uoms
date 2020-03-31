package com.linksteady.operate.dao;

import com.linksteady.operate.domain.BlackInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/3/31
 */
public interface BlackMapper {

    List<BlackInfo> getDataList(String phone, int start, int end);

    int getCount(@Param("phone") String phone);

    void deleteByPhone(@Param("phone") String phone);

    void insertData(BlackInfo blackInfo);
}
