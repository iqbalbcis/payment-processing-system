package com.main.audit;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

import static com.main.constants.CommonConstant.UTC_TIME_ZONE;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    @Column(name = "created_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @JsonFormat(timezone = UTC_TIME_ZONE)
    protected Date creationDate;

    @Column(name = "last_modified_date")
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = UTC_TIME_ZONE)
    protected Date lastModifiedDate;

    @CreatedBy
    @Column(name="created_by")
    protected String createdBy;

    @LastModifiedBy
    @Column(name="modified_by")
    protected String modifiedBy;

}
