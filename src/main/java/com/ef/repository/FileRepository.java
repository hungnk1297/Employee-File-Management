package com.ef.repository;

import com.ef.entity.EmployeeFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FileRepository extends JpaRepository<EmployeeFile, Long> {

    List<EmployeeFile> findAllByFileIDInAndDeletedIsFalse(Set<Long> fileIDs);

    EmployeeFile getByFileIDAndDeletedIsFalse(Long fileID);

    List<EmployeeFile> findAllByEmployee_EmployeeIDAndDeletedIsFalse(Long employeeID);
}
