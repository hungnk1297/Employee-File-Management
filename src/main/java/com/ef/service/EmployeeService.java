package com.ef.service;

import com.ef.model.request.EmployeeCreateRequestDTO;
import com.ef.model.response.EmployeeResponseDTO;

public interface EmployeeService {

    EmployeeResponseDTO createEmployee(EmployeeCreateRequestDTO createRequestDTO);

    EmployeeResponseDTO getEmployeeToken(EmployeeCreateRequestDTO requestDTO);

    String deleteEmployee(Long employeeID);

    EmployeeResponseDTO validateLogin(EmployeeCreateRequestDTO requestDTO);

    void clearSession();
}
