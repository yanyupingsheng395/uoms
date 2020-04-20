package com.linksteady.wxofficial.common.wechat.entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hxcao
 * @date 2020/4/17
 */
@Data
public class MaterialInfo {

    private String title;

    private String introduction;

    private String mediaType;

    private String base64Code;

    private String fileName;
}
