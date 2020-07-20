package com.ef.entity;

import com.ef.constant.EmployeeType;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Table(name = "AS1_EMPLOYEE")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMPLOYEE_ID", unique = true, nullable = false)
    private Long employeeID;

    @Column(name = "USERNAME", unique = true, nullable = false)
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "EMPLOYEE_TYPE", columnDefinition = "char(1 BYTE) default 'E'")
    private EmployeeType employeeType;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee", cascade = CascadeType.ALL)
    private Set<EmployeeFile> files;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee", cascade = CascadeType.ALL)
    private Set<FileSharing> fileSharings;
}
