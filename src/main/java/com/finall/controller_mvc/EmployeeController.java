package com.finall.controller_mvc;

import com.finall.controller_mvc.filter.LoginRequiredController;
import com.finall.model.request.EmployeeCreateRequestDTO;
import com.finall.model.response.EmployeeResponseDTO;
import com.finall.service.EmployeeService;
import com.finall.service.FileService;
import com.finall.utils.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

import static com.finall.constant.CommonConstant.AttributeConstant.*;
import static com.finall.constant.CommonConstant.TemplateConstant.*;

@Controller
@Slf4j
@RequestMapping(path = "/employee")
@AllArgsConstructor
public class EmployeeController implements LoginRequiredController {

    private final EmployeeService employeeService;
    private final FileService fileService;
    private final Validator validator;

    @GetMapping
    public String routingEmployee() {
        if (isLoggedIn()) {
            return REDIRECT + HOME;
        } else
            return INDEX;
    }

    // Register
    @GetMapping(path = "/register")
    public String getRegister(Model model) {
        model.addAttribute("registerDTO", new EmployeeCreateRequestDTO());
        return REGISTER;
    }

    @PostMapping(path = "/register")
    public String createEmployee(Model model,
                                 @ModelAttribute("registerDTO") EmployeeCreateRequestDTO employeeCreateRequestDTO) {
        EmployeeResponseDTO employeeResponseDTO = employeeService.createEmployee(employeeCreateRequestDTO);
        if (employeeResponseDTO != null) {
            model.addAttribute(SUCCESS_REGISTER_EMPLOYEE, true);
            return LOGIN;
        } else {
            model.addAttribute(SUCCESS_REGISTER_EMPLOYEE, null);
            return REGISTER;
        }
    }

    //  Log in
    @GetMapping(path = "/login")
    public String getLogin() {
        if (isLoggedIn()) {
            return REDIRECT + HOME;
        }
        return LOGIN;
    }

    //  Log in
    @GetMapping(path = "/logout")
    public String getLogOut() {
        employeeService.clearSession();
        return INDEX;
    }

    @PostMapping(path = "/login")
    public String login(@ModelAttribute("employeeCreateRequestDTO") EmployeeCreateRequestDTO requestDTO) {
        if (isLoggedIn()) {
            return REDIRECT + HOME;
        } else {
            EmployeeResponseDTO loginEmployee = employeeService.validateLogin(requestDTO);
            if (loginEmployee != null) {
                HttpSession session = Validator.getSession();
                session.setAttribute(EMPLOYEE_LOGGED_IN, loginEmployee.getUsername());
                session.setAttribute(EMPLOYEE_ID, loginEmployee.getEmployeeID());
                return REDIRECT + HOME;
            } else {
                return LOGIN;
            }
        }
    }

    @Override
    public boolean isLoggedIn() {
        return validator.isLoggedIn();
    }
}
