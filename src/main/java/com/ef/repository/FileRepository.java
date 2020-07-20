package com.ef.repository;

import com.ef.entity.EmployeeFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<EmployeeFile, Long> {
}
