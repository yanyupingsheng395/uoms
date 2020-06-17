package com.linksteady.operate.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.service.ShortUrlService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hxcao
 * @date 2020/3/27
 */
@RestController
@RequestMapping("/url")
public class ShortUrlController {

    @Autowired
    private ShortUrlService shortUrlService;

    /**
     *
     * @param type 转换类型：0 商品id,1 长链
     * @return
     */
    @GetMapping("/getShortUrl")
    public ResponseBo getShortUrl(@RequestParam String data, @RequestParam String type) {
        String shortUrl = "";
        if(type.equalsIgnoreCase("0")) {
            shortUrl = shortUrlService.genProdShortUrlByProdId(data, "M");
        }
        if(type.equalsIgnoreCase("1")) {
            shortUrl = shortUrlService.genProdShortUrl(data, "M");
        }
        return ResponseBo.okWithData(null, shortUrl);
    }

    /**
     * 长链转短链
     * @param url 长链
     * @return
     */
    @GetMapping("/convertShortUrl")
    public ResponseBo convertShortUrl(@RequestParam String url) {
        String shortUrl = "";
        if(StringUtils.isNotEmpty(url)) {
            shortUrl = shortUrlService.genShortUrlDirect(url, "M");
        }
        return ResponseBo.okWithData(null, shortUrl);
    }
}
