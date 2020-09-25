package com.finall.asm;

import com.finall.entity.Employee;
import com.finall.repository.EmployeeRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@DataJpaTest
public class EmployeeRepositoryTest {
    //    MethodName_StateUnderTest_ExpectedBehavior

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void test_GetEmployeeByUserName_Success() {
        String testingUsername = "testingUsername";
        Employee testingEmployee = Employee.builder()
                .username(testingUsername)
                .password("abc")
                .build();
        testingEmployee.setDeleted(false);
        entityManager.persist(testingEmployee);
        entityManager.flush();

        Employee foundInRepo = employeeRepository.getByUsernameAndDeletedIsFalse(testingUsername);

        Assert.assertEquals(testingUsername, foundInRepo.getUsername());
    }

    @Test
    public void test_GetEmployeeByUserName_Fail() {
        String testingUsername = "testingUsername";
        Employee testingEmployee = Employee.builder()
                .username(testingUsername)
                .password("abc")
                .build();
        testingEmployee.setDeleted(false);
        entityManager.persist(testingEmployee);
        entityManager.flush();

        Employee foundInRepo = employeeRepository.getByUsernameAndDeletedIsFalse(testingUsername);

        Assert.assertNotEquals("anotherString", foundInRepo.getUsername());
    }

    @Test
    public void test_CheckDuplicateUserName_True() {
        String testingUsername = "testingUsername";
        Employee testingEmployee = Employee.builder()
                .username(testingUsername)
                .password("abc")
                .build();
        testingEmployee.setDeleted(false);
        entityManager.persist(testingEmployee);
        entityManager.flush();

        boolean duplicateCheck = employeeRepository.isDuplicateUsername(testingUsername);

        Assert.assertEquals(duplicateCheck, true);
    }

    @Test
    public void test_CheckDuplicateUserName_False() {
        String testingUsername = "testingUsername";
        Employee testingEmployee = Employee.builder()
                .username(testingUsername)
                .password("abc")
                .build();
        testingEmployee.setDeleted(true);
        entityManager.persist(testingEmployee);
        entityManager.flush();

        boolean duplicateCheck = employeeRepository.isDuplicateUsername(testingUsername);

        Assert.assertEquals(duplicateCheck, false);
    }

    @Test
    public void test_FindEmployeesByIdSet_Success() {
        Employee emp1 = Employee.builder().username("testingUsername1").password("abc").build();
        Employee emp2 = Employee.builder().username("testingUsername2").password("abc").build();
        Employee emp3 = Employee.builder().username("testingUsername3").password("abc").build();

        List<Employee> employeeList = Stream.of(emp1, emp2, emp3).collect(Collectors.toList());
        entityManager.persist(emp1);
        entityManager.persist(emp2);
        entityManager.persist(emp3);
        entityManager.flush();

        List<Employee> foundList = employeeRepository
                .findAllByEmployeeIDInAndDeletedIsFalse(Stream.of(emp1.getEmployeeID(), emp2.getEmployeeID(), emp3.getEmployeeID())
                        .collect(Collectors.toSet()));

        Assert.assertEquals(employeeList.size(), foundList.size());
    }
}
