package com.ef.repository;

import com.ef.entity.FileSharing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileSharingRepository extends JpaRepository<FileSharing, Long> {

    @Query("SELECT CASE WHEN COUNT(fs) > 0 THEN TRUE ELSE FALSE END " +
            "FROM FileSharing fs " +
            "WHERE fs.deleted = false " +
            "   AND fs.employee.employeeID = :employeeID " +
            "   AND fs.employeeFile.fileID = :fileID")
    boolean isSharingToEmployee(@Param("employeeID") Long employeeID,
                                @Param("fileID") Long fileID);
}
