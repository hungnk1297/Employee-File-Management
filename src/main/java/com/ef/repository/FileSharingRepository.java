package com.ef.repository;

import com.ef.entity.FileSharing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FileSharingRepository extends JpaRepository<FileSharing, Long> {

    @Query("SELECT CASE WHEN COUNT(fs) > 0 THEN TRUE ELSE FALSE END " +
            "FROM FileSharing fs " +
            "WHERE fs.deleted = false " +
            "   AND fs.sharingEmployee.employeeID = :employeeID " +
            "   AND fs.employeeFile.fileID = :fileID")
    boolean isSharingToEmployee(@Param("employeeID") Long employeeID,
                                @Param("fileID") Long fileID);

    @Query("SELECT fs " +
            "FROM FileSharing fs WHERE fs.deleted = false " +
            "   AND fs.employeeFile.fileID = :fileID " +
            "   AND (COALESCE(:sharingEmployeeIDs) IS NULL OR fs.sharingEmployee.employeeID IN :sharingEmployeeIDs)")
    List<FileSharing> findAllByFileIDAndSharingEmployeeIDs(Long fileID, Set<Long> sharingEmployeeIDs);

    List<FileSharing> findAllBySharingEmployee_EmployeeIDAndDeletedIsFalse(Long sharingEmployeeID);

    @Query("SELECT fs " +
            "FROM FileSharing fs WHERE fs.deleted = false " +
            "AND fs.employeeFile.employee.employeeID = :employeeID")
    List<FileSharing> findAllSharingOfEmployee(Long employeeID);
}
