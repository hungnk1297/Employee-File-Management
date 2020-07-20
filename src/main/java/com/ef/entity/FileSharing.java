package com.ef.entity;

import lombok.*;

import javax.persistence.*;

@Table(name = "AS1_SHARE_FILE")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileSharing extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SHARE_FILE_ID", unique = true, nullable = false)
    private Long shareFileID;

    @ManyToOne
    @JoinColumn(name = "SHARING_EMPLOYEE_ID", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "FILE_ID", nullable = false)
    private EmployeeFile employeeFile;
}
