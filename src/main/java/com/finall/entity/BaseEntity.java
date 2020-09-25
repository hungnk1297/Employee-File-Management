package com.finall.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class BaseEntity implements Serializable {

    @Column(name = "DELETED")
    private boolean deleted;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn = LocalDate.now().atStartOfDay();

    @Column(name = "LAST_MODIFIED")
    private LocalDateTime lastModified;
}
