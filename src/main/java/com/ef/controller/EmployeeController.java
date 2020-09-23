package com.ef.controller;

import com.ef.model.request.EmployeeCreateRequestDTO;
import com.ef.model.response.EmployeeResponseDTO;
import com.ef.service.EmployeeService;
import com.ef.utils.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

//@RestController
@Slf4j
@RequestMapping(path = "/employee")
@AllArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final Validator validator;

    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> createEmployee(
            @RequestBody @Valid EmployeeCreateRequestDTO employeeCreateRequestDTO) {
        return new ResponseEntity<>(employeeService.createEmployee(employeeCreateRequestDTO), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{employeeID}")
    public ResponseEntity<String> deleteEmployee(
            @PathVariable(name = "employeeID") Long employeeID,
            @RequestHeader(name = "token") String token) {

        if (validator.authorizedEmployeeToken(token, employeeID))
            return new ResponseEntity<>(employeeService.deleteEmployee(employeeID), HttpStatus.OK);

        return ResponseEntity.badRequest().build();
    }

    @PostMapping(value = "/token")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeToken(
            @RequestBody @Valid EmployeeCreateRequestDTO employeeCreateRequestDTO) {
        return new ResponseEntity<>(employeeService.getEmployeeToken(employeeCreateRequestDTO), HttpStatus.OK);
    }
}
