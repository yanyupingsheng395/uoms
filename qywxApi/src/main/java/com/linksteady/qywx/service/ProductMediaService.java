package com.linksteady.qywx.service;

public interface ProductMediaService {

    /**
     * 根据商品ID获取商品的图片
     * @param ebpProductId
     */
    byte[] getProductImageByte(long ebpProductId);
}
