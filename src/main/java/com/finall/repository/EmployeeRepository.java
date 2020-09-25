package com.finall.repository;

import com.finall.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {


    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Employee e WHERE e.deleted = false AND e.username = :username ")
    boolean isDuplicateUsername(@Param("username") String username);

    Employee getByUsernameAndDeletedIsFalse(String username);

    Employee getByEmployeeIDAndDeletedIsFalse(Long employeeID);

    List<Employee> findAllByEmployeeIDInAndDeletedIsFalse(Set<Long> employeeIDs);
}
