package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ManualDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hxcao
 * @date 2019/12/25
 */
public interface ManualDetailMapper {

    void saveDetailList(List<ManualDetail> manualDetails);

    void deleteData(@Param("headId") String headId);
}
