package com.linksteady.operate.dao;

import com.linksteady.operate.domain.BlackInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/3/31
 */
public interface BlackMapper {

    List<BlackInfo> getDataList(String phone, int limit, int offset);

    int getCount(@Param("phone") String phone);

    void deleteByPhone(@Param("phoneList") List<String> phoneList);

    void insertData(BlackInfo blackInfo);

    int checkPhone(String phone);
}
