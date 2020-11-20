package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.QywxWelcomeProduct;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/9/8
 */
public interface WelcomeProductMapper {
    int getTableDataCount();

    List<QywxWelcomeProduct> getTableDataList(Integer limit, Integer offset);

    void saveData(QywxWelcomeProduct qywxWelcomeProduct);

    List<QywxWelcomeProduct> getDataById(String productId);

    void updateData(QywxWelcomeProduct qywxWelcomeProduct);

    void deleteProductById(String productId);
}
