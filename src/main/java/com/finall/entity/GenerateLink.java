package com.finall.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "FINAL_GENERATE_LINK")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateLink extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GENERATE_LINK_ID", unique = true, nullable = false)
    private Long generateLinkID;

    @ManyToOne
    @JoinColumn(name = "FILE_ID", nullable = false)
    private EmployeeFile employeeFile;

    @Column(name = "LINK")
    private String link;

    @Column(name = "EXPIRE_TIME")
    private LocalDateTime expireTime;

}
