package com.ef.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Table(name = "AS1_FILE")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FILE_ID", unique = true, nullable = false)
    private Long fileID;

    @Column(name = "NAME", nullable = false)
    private String fileName;

    @Column(name = "URL", nullable = false)
    private String url;

    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID", nullable = false)
    private Employee employee;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee", cascade = CascadeType.ALL)
    private Set<FileSharing> fileSharings;
}
