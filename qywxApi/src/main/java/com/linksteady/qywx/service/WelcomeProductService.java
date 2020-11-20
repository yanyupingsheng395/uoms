package com.linksteady.qywx.service;


import com.linksteady.qywx.domain.QywxWelcomeProduct;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/9/8
 */
public interface WelcomeProductService {
    int getTableDataCount();

    List<QywxWelcomeProduct> getTableDataList(Integer limit, Integer offset);

    void saveData(QywxWelcomeProduct qywxWelcomeProduct);

    QywxWelcomeProduct getDataById(String productId);

    void updateData(QywxWelcomeProduct qywxWelcomeProduct);

    void deleteProductById(String productId);
}
