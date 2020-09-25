package com.finall.service;

import com.finall.model.request.EmployeeCreateRequestDTO;
import com.finall.model.response.EmployeeResponseDTO;

public interface EmployeeService {

    EmployeeResponseDTO createEmployee(EmployeeCreateRequestDTO createRequestDTO); // TEST

    EmployeeResponseDTO getEmployeeToken(EmployeeCreateRequestDTO requestDTO);

    String deleteEmployee(Long employeeID);

    EmployeeResponseDTO validateLogin(EmployeeCreateRequestDTO requestDTO); // TEST

    void clearSession();
}
