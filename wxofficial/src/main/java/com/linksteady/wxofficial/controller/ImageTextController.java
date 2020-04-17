package com.linksteady.wxofficial.controller;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.wxofficial.entity.ImageText;
import com.linksteady.wxofficial.service.ImageTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * @author hxcao
 * @date 2020/4/15
 */
@RestController
@RequestMapping("/imageText")
public class ImageTextController {

    @Autowired
    private ImageTextService imageTextService;

    @RequestMapping("/getDataList")
    public ResponseBo getDataList(QueryRequest queryRequest) {
        int limit = queryRequest.getLimit();
        int offset = queryRequest.getOffset();
        int count = imageTextService.getCount();
        List<ImageText> dataList = imageTextService.getDataListPage(limit, offset);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 添加图文消息
     * @return
     */
    @RequestMapping("/addImageText")
    public ResponseBo addImageText(ImageText imageText) {
        imageTextService.addImageText(imageText);
        return ResponseBo.ok("保存成功！");
    }

    @RequestMapping("/getImageText")
    public ResponseBo getImageText(@RequestParam("id") int id) {
        return ResponseBo.okWithData(null, imageTextService.getImageText(id));
    }

    @RequestMapping("/deleteImageText")
    public ResponseBo deleteImageText(@RequestParam String ids) {
        imageTextService.deleteImageText(ids);
        return ResponseBo.ok();
    }

    @RequestMapping("/updateImageText")
    public ResponseBo updateImageText(ImageText imageText) {
        imageTextService.updateImageText(imageText);
        return ResponseBo.ok();
    }
}
