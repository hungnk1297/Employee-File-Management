package com.ef.service.implement;

import com.ef.constant.EmployeeType;
import com.ef.entity.Employee;
import com.ef.exception.CustomException;
import com.ef.model.request.EmployeeCreateRequestDTO;
import com.ef.model.response.EmployeeResponseDTO;
import com.ef.repository.EmployeeRepository;
import com.ef.service.EmployeeService;
import com.ef.service.FileService;
import com.ef.service.FileSharingService;
import com.ef.utils.ExceptionGenerator;
import com.ef.utils.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

import static com.ef.constant.CommonConstant.EntityNameConstant.EMPLOYEE;
import static com.ef.constant.CommonConstant.FieldNameConstant.EMPLOYEE_ID;
import static com.ef.constant.CommonConstant.TOKEN_SIGNATURE;

@AllArgsConstructor
@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final Validator validator;

    private final FileService fileService;
    private final FileSharingService fileSharingService;

    @Transactional
    @Override
    public EmployeeResponseDTO createEmployee(EmployeeCreateRequestDTO createRequestDTO) {

        if (validator.isDuplicateUsername(createRequestDTO.getUsername()))
            throw new CustomException(ExceptionGenerator.duplicateUsername(createRequestDTO.getUsername()));

        Employee employee = Employee.builder()
                .username(createRequestDTO.getUsername())
                .password(validator.generateHashPassword(createRequestDTO.getUsername(), createRequestDTO.getPassword()))
                .employeeType(createRequestDTO.getEmployeeType() != null ? createRequestDTO.getEmployeeType() : EmployeeType.EMPLOYEE)
                .build();

        employeeRepository.save(employee);

        return toEmployeeResponseDTO(employee);
    }

    @Override
    public EmployeeResponseDTO getEmployeeToken(EmployeeCreateRequestDTO requestDTO) {
        Employee employee = employeeRepository.getByUsernameAndDeletedIsFalse(requestDTO.getUsername());
        if (employee == null)
            throw new CustomException(ExceptionGenerator.invalidLogin());

        if (!employee.getPassword().equals(validator.generateHashPassword(requestDTO.getUsername(), requestDTO.getPassword())))
            throw new CustomException(ExceptionGenerator.invalidLogin());

        return toEmployeeResponseDTO(employee);
    }

    @Transactional
    @Override
    public String deleteEmployee(Long employeeID) {
        Employee employee = employeeRepository.getByEmployeeIDAndDeletedIsFalse(employeeID);
        if (employee == null)
            throw new CustomException(ExceptionGenerator.notFound(EMPLOYEE, EMPLOYEE_ID, employeeID));

        fileSharingService.stopSharingFileWhenDeleteEmployee(employeeID);
        fileService.deleteAllFileOfEmployee(employeeID);
        employee.setDeleted(true);
        employeeRepository.save(employee);
        return String.format("User %s was successfully deleted.", employee.getUsername());

    }

    private EmployeeResponseDTO toEmployeeResponseDTO(Employee employee) {
        EmployeeResponseDTO responseDTO = EmployeeResponseDTO.builder()
                .employeeID(employee.getEmployeeID())
                .username(employee.getUsername())
                .employeeType(employee.getEmployeeType().getValue())
                .employeeToken(generateEmployeeToken(employee))
                .build();

        responseDTO.setCreatedOn(employee.getCreatedOn());
        responseDTO.setDeleted(employee.isDeleted());
        responseDTO.setLastModified(employee.getLastModified());
        return responseDTO;
    }

    private String generateEmployeeToken(Employee employee) {
        String token = new StringBuilder()
                .append(employee.getUsername())
                .append(TOKEN_SIGNATURE)
                .append(employee.getPassword())
                .append(TOKEN_SIGNATURE)
                .append(employee.getCreatedOn())
                .toString();
        return Base64.getEncoder().encodeToString(token.getBytes());
    }


}
