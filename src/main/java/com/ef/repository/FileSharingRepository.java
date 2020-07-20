package com.ef.repository;

import com.ef.entity.FileSharing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileSharingRepository extends JpaRepository<FileSharing, Long> {
}
