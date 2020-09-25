package com.finall.repository;

import com.finall.entity.GenerateLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GenerateLinkRepository extends JpaRepository<GenerateLink, Long> {

    @Query("SELECT gl FROM GenerateLink gl " +
            "WHERE gl.expireTime > :currentTime " +
            "AND gl.employeeFile.fileID = :fileID " +
            "ORDER BY gl.createdOn")
    List<GenerateLink> getUnExpireGeneratedLink(Long fileID, LocalDateTime currentTime);

    Boolean existsByLink(String link);

    GenerateLink getByLinkAndExpireTimeAfter(String generatedLink, LocalDateTime currentDateTime);
}
