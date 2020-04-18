package com.linksteady.wxofficial.entity.bo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.List;

/**
 * @author hxcao
 * @date 2020/4/17
 */
@Data
public class MaterialBo {
    private String code;
    private String data;
    private String itemCount;
    private List<Article> articles;
    private List<Item> items;

    @Data
    public static class Item {
        private String mediaId;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
        private String updateTime;
        private String name;
        private String url;
    }

    @Data
    public static class Article {
        private String mediaId;
        private String thumbMediaId;
        private String thumbUrl;
        private String author;
        private String title;
        private String content;
        private String digest;
        private String showCoverPic;
        private String url;
        private String needOpenComment;
        private String onlyFansCanComment;
    }
}







