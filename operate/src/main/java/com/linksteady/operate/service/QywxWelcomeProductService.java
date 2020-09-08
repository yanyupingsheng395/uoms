package com.linksteady.operate.service;

import com.linksteady.operate.domain.QywxWelcomeProduct;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/9/8
 */
public interface QywxWelcomeProductService {
    int getTableDataCount();

    List<QywxWelcomeProduct> getTableDataList(Integer limit, Integer offset);

    void saveData(QywxWelcomeProduct qywxWelcomeProduct);

    QywxWelcomeProduct getDataById(String productId);

    void updateData(QywxWelcomeProduct qywxWelcomeProduct);

    void deleteProductById(String productId);
}
