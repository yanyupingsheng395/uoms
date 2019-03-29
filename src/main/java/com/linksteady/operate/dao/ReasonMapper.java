package com.linksteady.operate.dao;
import com.linksteady.operate.domain.KeyPointMonth;
import com.linksteady.operate.domain.KeyPointYear;
import com.linksteady.operate.domain.Reason;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface ReasonMapper {

    List<Map<String,Object>> getReasonList(@Param("startRow") int startRow,@Param("endRow") int endRow);

    int getReasonTotalCount();

    void saveReasonData(Reason reasonDo);

    int getReasonPrimaryKey();


}
