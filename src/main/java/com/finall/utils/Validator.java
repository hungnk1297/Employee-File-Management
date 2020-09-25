package com.finall.utils;

import com.finall.constant.CommonConstant;
import com.finall.entity.Employee;
import com.finall.entity.EmployeeFile;
import com.finall.exception.CustomException;
import com.finall.repository.EmployeeRepository;
import com.finall.repository.FileRepository;
import com.finall.repository.FileSharingRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static com.finall.constant.CommonConstant.EntityNameConstant.EMPLOYEE;
import static com.finall.constant.CommonConstant.EntityNameConstant.FILE;
import static com.finall.constant.CommonConstant.FieldNameConstant.*;
import static com.finall.constant.CommonConstant.TOKEN_SIGNATURE;


@Slf4j
@Component
@AllArgsConstructor(onConstructor_ = {@Autowired})
@NoArgsConstructor
public class Validator {

    private EmployeeRepository employeeRepository;
    private FileRepository fileRepository;
    private FileSharingRepository fileSharingRepository;

    //  Validate duplicate username when creating
    public boolean isDuplicateUsername(String username) {
        return employeeRepository.isDuplicateUsername(username);
    }

    //  Generate hash password
    public static String generateHashPassword(String username, String password) {
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

    //  Share file permission check
    public boolean isOwnerOfFile(Long employeeID, Long fileID) {
        Employee employee = employeeRepository.getByEmployeeIDAndDeletedIsFalse(employeeID);
        if (employee == null)
            throw new CustomException(ExceptionGenerator.notFound(EMPLOYEE, EMPLOYEE_ID, employeeID));

        EmployeeFile file = fileRepository.getByFileIDAndDeletedIsFalse(fileID);
        if (file == null)
            throw new CustomException(ExceptionGenerator.notFound(FILE, FILE_ID, fileID));

        if (employeeID.equals(file.getEmployee().getEmployeeID()))
            return true;

        throw new CustomException(ExceptionGenerator.notOwnerOfTheFile());
    }

    public boolean isLoggedIn() {
        HttpSession session = Validator.getSession();
        return session.getAttribute(CommonConstant.AttributeConstant.EMPLOYEE_LOGGED_IN) != null;
    }

    public static HttpSession getSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true); // true == allow create
    }

}
