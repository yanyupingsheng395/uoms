package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ShortUrlInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ShortUrlMapper {

    /**
     * 获取短链历史记录的列表
     * @param startRow
     * @param endRow
     * @return
     */
    List<Map<String,String>> getList(@Param("startRow") int startRow, @Param("endRow") int endRow);

    /**
     * 写入短链历史记录
     * @param shorUrlInfo
     */
    void insertShortUrl(ShortUrlInfo shorUrlInfo);

    /**
     * 根据长链获取短链
     * @param longUrl
     * @return
     */
    String selectShortUrlByLongUrl(@Param("longUrl") String longUrl);

    int selectCountByLongUrl(@Param("longUrl") String longUrl);

    /**
     * 更新validateDate
     */
    void updateShortUrlValidateDate(@Param("validateDate")Date validateDate);

    List<ShortUrlInfo> getDataList();
}