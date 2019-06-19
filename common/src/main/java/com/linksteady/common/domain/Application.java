package com.linksteady.common.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by hxcao on 2019-05-05
 */
@Data
@Table(name = "T_APPLICATION")
public class Application {

    @Id
    @Column(name = "APPLICATION_ID")
    private String applicationId;

    @Column(name = "APPLICATION_NAME")
    private String applicationName;

    @Column(name = "DOMAIN")
    private String domain;

    @Column(name = "REMARK")
    private String remark;
}
