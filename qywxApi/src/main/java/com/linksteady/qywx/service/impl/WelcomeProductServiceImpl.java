package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.dao.WelcomeProductMapper;
import com.linksteady.qywx.domain.QywxWelcomeProduct;
import com.linksteady.qywx.service.WelcomeProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/9/8
 */
@Service
public class WelcomeProductServiceImpl implements WelcomeProductService {

    @Autowired
    private WelcomeProductMapper welcomeProductMapper;

    @Override
    public int getTableDataCount() {
        return welcomeProductMapper.getTableDataCount();
    }

    @Override
    public List<QywxWelcomeProduct> getTableDataList(Integer limit, Integer offset) {
        return welcomeProductMapper.getTableDataList(limit, offset);
    }

    @Override
    public void saveData(QywxWelcomeProduct qywxWelcomeProduct) {
        welcomeProductMapper.saveData(qywxWelcomeProduct);
    }

    @Override
    public QywxWelcomeProduct getDataById(String productId) {
        List<QywxWelcomeProduct> dataList = welcomeProductMapper.getDataById(productId);
        return dataList.size() > 0 ? dataList.get(0) : null;
    }

    @Override
    public void updateData(QywxWelcomeProduct qywxWelcomeProduct) {
        welcomeProductMapper.updateData(qywxWelcomeProduct);
    }

    @Override
    public void deleteProductById(String productId) {
        welcomeProductMapper.deleteProductById(productId);
    }
}
