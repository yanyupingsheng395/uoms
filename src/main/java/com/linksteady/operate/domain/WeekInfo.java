package com.linksteady.operate.domain;

import java.math.BigDecimal;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "W_WEEK")
public class WeekInfo {
    @Column(name = "YEAR")
    private BigDecimal year;

    @Column(name = "WEEK_WID")
    private BigDecimal weekWid;

    @Column(name = "WEEK_OF_YARE_NAME")
    private String weekOfYareName;

    @Column(name = "WEEK_BEGIN_WID")
    private BigDecimal weekBeginWid;

    @Column(name = "WEEK_END_WID")
    private BigDecimal weekEndWid;
}