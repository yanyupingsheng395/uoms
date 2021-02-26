package com.linksteady.qywx.dao;

public interface ProductMediaMapper {
    /**
     * 根据商品id查w_product_media中的prod_pic_url
     * @param ebpProductId
     * @return
     */
    String getProductMediaUrl(Long ebpProductId);
}
