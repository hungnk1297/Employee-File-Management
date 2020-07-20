package com.ef.utils;

import com.ef.entity.Employee;
import com.ef.exception.CustomException;
import com.ef.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static com.ef.constant.CommonConstant.EntityNameConstant.EMPLOYEE;
import static com.ef.constant.CommonConstant.FieldNameConstant.EMPLOYEE_ID;
import static com.ef.constant.CommonConstant.FieldNameConstant.USERNAME;
import static com.ef.constant.CommonConstant.TOKEN_SIGNATURE;

@AllArgsConstructor
@Slf4j
@Component
public class Validator {

    private final EmployeeRepository employeeRepository;

    //  Validate duplicate username when creating
    public boolean isDuplicateUsername(String username) {
        return employeeRepository.isDuplicateUsername(username);
    }

    //  Generate hash password
    public String generateHashPassword(String username, String password) {
        String appendedString = password.concat(username);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] digestBytes = md.digest(appendedString.getBytes());
            BigInteger no = new BigInteger(digestBytes);
            return no.toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    //  Authorize permission
    public boolean authorizedEmployeeToken(String token, Employee employee) {
        boolean authorized = false;
        byte[] decodedBytes = Base64.getDecoder().decode(token);
        String resolved = new String(decodedBytes);

        String[] splitted = resolved.split(TOKEN_SIGNATURE);

        if (splitted.length != 0) {
            authorized = (splitted[0] != null && employee.getUsername().equals(splitted[0]))
                    && (splitted[1] != null && employee.getPassword().equals(splitted[1]));
        }

        if (authorized) return true;
        else throw new CustomException(ExceptionGenerator.unAuthorize());
    }

    public boolean authorizedEmployeeToken(String token, Long employeeID) {
        Employee employee = employeeRepository.getByEmployeeIDAndDeletedIsFalse(employeeID);
        if (employee == null)
            throw new CustomException(ExceptionGenerator.notFound(EMPLOYEE, EMPLOYEE_ID, employeeID));

        return authorizedEmployeeToken(token, employee);
    }

    public boolean authorizedEmployeeToken(String token, String username) {
        Employee employee = employeeRepository.getByUsernameAndDeletedIsFalse(username);
        if (employee == null)
            throw new CustomException(ExceptionGenerator.notFound(EMPLOYEE, USERNAME, username));

        return authorizedEmployeeToken(token, employee);
    }
}
