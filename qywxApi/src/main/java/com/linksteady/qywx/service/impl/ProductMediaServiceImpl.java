package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.dao.ProductMediaMapper;
import com.linksteady.qywx.service.ProductMediaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

@Service
@Slf4j
public class ProductMediaServiceImpl implements ProductMediaService {

    @Autowired
    ProductMediaMapper productMediaMapper;

    @Override
    public byte[] getProductImageByte(long ebpProductId) {
        String prodImageUrl=productMediaMapper.getProductMediaUrl(ebpProductId);

        if(!StringUtils.isEmpty(prodImageUrl))
        {
            ByteArrayOutputStream baos = null;
            try{
                URL u = new URL(prodImageUrl);
                BufferedImage image = ImageIO.read(u);
                baos = new ByteArrayOutputStream();
                ImageIO.write( image, "jpg", baos);
                baos.flush();
                return baos.toByteArray();
            }catch (Exception e){
                log.error("通过url，将图片转字节数组错误，请检查！");
            }finally{
                if(baos != null){
                    try {
                        baos.close();
                    } catch (IOException e) {
                        log.error("关闭流失败！");
                    }
                }
            }
            return new byte[0];
        }else
        {
            return new byte[0];
        }

    }
}
