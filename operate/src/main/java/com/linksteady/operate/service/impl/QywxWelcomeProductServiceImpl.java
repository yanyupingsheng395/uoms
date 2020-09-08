package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.QywxWelcomeProductMapper;
import com.linksteady.operate.domain.QywxWelcomeProduct;
import com.linksteady.operate.service.QywxWelcomeProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/9/8
 */
@Service
public class QywxWelcomeProductServiceImpl implements QywxWelcomeProductService {

    @Autowired
    private QywxWelcomeProductMapper qywxWelcomeProductMapper;

    @Override
    public int getTableDataCount() {
        return qywxWelcomeProductMapper.getTableDataCount();
    }

    @Override
    public List<QywxWelcomeProduct> getTableDataList(Integer limit, Integer offset) {
        return qywxWelcomeProductMapper.getTableDataList(limit, offset);
    }

    @Override
    public void saveData(QywxWelcomeProduct qywxWelcomeProduct) {
        qywxWelcomeProductMapper.saveData(qywxWelcomeProduct);
    }

    @Override
    public QywxWelcomeProduct getDataById(String productId) {
        List<QywxWelcomeProduct> dataList = qywxWelcomeProductMapper.getDataById(productId);
        return dataList.size() > 0 ? dataList.get(0) : null;
    }

    @Override
    public void updateData(QywxWelcomeProduct qywxWelcomeProduct) {
        qywxWelcomeProductMapper.updateData(qywxWelcomeProduct);
    }

    @Override
    public void deleteProductById(String productId) {
        qywxWelcomeProductMapper.deleteProductById(productId);
    }
}
