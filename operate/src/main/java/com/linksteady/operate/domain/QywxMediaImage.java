package com.linksteady.operate.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QywxMediaImage {

    private Long imgId;

    private String imgUrl;

    private LocalDateTime insertDt;

    private String insertBy;

    private String imgTitle;
}
