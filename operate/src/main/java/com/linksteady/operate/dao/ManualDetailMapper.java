package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ManualDetail;

import java.util.List;

/**
 * @author hxcao
 * @date 2019/12/25
 */
public interface ManualDetailMapper {

    void saveDetailList(List<ManualDetail> manualDetails);
}
