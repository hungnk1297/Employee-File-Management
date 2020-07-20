package com.ef.repository;

import com.ef.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {


    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Employee e WHERE e.deleted = false AND e.username = :username ")
    boolean isDuplicateUsername(@Param("username") String username);

    Employee getByUsernameAndDeletedIsFalse(String username);

    Employee getByEmployeeIDAndDeletedIsFalse(Long employeeID);
}
