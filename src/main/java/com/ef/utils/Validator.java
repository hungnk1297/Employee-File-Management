package com.ef.utils;

import com.ef.entity.Employee;
import com.ef.entity.EmployeeFile;
import com.ef.exception.CustomException;
import com.ef.repository.EmployeeRepository;
import com.ef.repository.FileRepository;
import com.ef.repository.FileSharingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static com.ef.constant.CommonConstant.EntityNameConstant.EMPLOYEE;
import static com.ef.constant.CommonConstant.EntityNameConstant.FILE;
import static com.ef.constant.CommonConstant.FieldNameConstant.*;
import static com.ef.constant.CommonConstant.TOKEN_SIGNATURE;

@AllArgsConstructor
@Slf4j
@Component
public class Validator {

    private final EmployeeRepository employeeRepository;
    private final FileRepository fileRepository;
    private final FileSharingRepository fileSharingRepository;

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

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(token);
            String resolved = new String(decodedBytes);

            String[] splitted = resolved.split(TOKEN_SIGNATURE);

            if (splitted.length != 0) {
                authorized = (splitted[0] != null && employee.getUsername().equals(splitted[0]))
                        && (splitted[1] != null && employee.getPassword().equals(splitted[1]));
            }

            if (authorized) return true;
            else throw new CustomException(ExceptionGenerator.unAuthorize());
        } catch (Exception e) {
            log.error("Error when decoding Employee's token", e);
            throw new CustomException(ExceptionGenerator.unAuthorize());
        }
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

    // Read file permission check
    public boolean authorizedReadPermission(Long employeeID, Long fileID) {
        Employee employee = employeeRepository.getByEmployeeIDAndDeletedIsFalse(employeeID);
        if (employee == null)
            throw new CustomException(ExceptionGenerator.notFound(EMPLOYEE, EMPLOYEE_ID, employeeID));

        EmployeeFile file = fileRepository.getByFileIDAndDeletedIsFalse(fileID);
        if (file == null)
            throw new CustomException(ExceptionGenerator.notFound(FILE, FILE_ID, fileID));

        //  Check if the file is belong to the employee
        if (employee.getFiles() != null && !CollectionUtils.isEmpty(employee.getFiles())
                && employee.getFiles().stream()
                .filter(employeeFile -> !employeeFile.isDeleted())
                .map(EmployeeFile::getFileID)
                .anyMatch(fileID::equals))
            return true;

        // Check if the file is being share to the employee
        return fileSharingRepository.isSharingToEmployee(employeeID, fileID);
    }

    //TODO
}
