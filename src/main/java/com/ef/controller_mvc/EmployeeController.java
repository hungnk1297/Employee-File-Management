package com.ef.controller_mvc;

import com.ef.controller_mvc.filter.LoginRequiredController;
import com.ef.model.request.EmployeeCreateRequestDTO;
import com.ef.model.response.EmployeeResponseDTO;
import com.ef.model.response.FileResponseDTO;
import com.ef.service.EmployeeService;
import com.ef.service.FileService;
import com.ef.utils.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

import java.util.List;

import static com.ef.constant.CommonConstant.TemplateConstant.*;
import static com.ef.constant.CommonConstant.AttributeConstant.*;

@Controller
@Slf4j
@RequestMapping(path = "/employee")
@AllArgsConstructor
public class EmployeeController implements LoginRequiredController {

    private final EmployeeService employeeService;
    private final FileService fileService;
    private final Validator validator;

    @GetMapping
    public String routingEmployee(Model model) {
        return loggedIn() ? HOME : INDEX;
    }

    // Register
    @GetMapping(path = "/register")
    public String getRegister(Model model) {
        model.addAttribute("registerDTO", new EmployeeCreateRequestDTO());
        return REGISTER;
    }

    @PostMapping(path = "/register")
    public String createEmployee(
            Model model, @ModelAttribute("registerDTO") EmployeeCreateRequestDTO employeeCreateRequestDTO) {
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
        if (loggedIn())
            return HOME;
        return LOGIN;
    }

    //  Log in
    @GetMapping(path = "/logout")
    public String getLogOut() {
        employeeService.clearSession();
        return INDEX;
    }

    @PostMapping(path = "/login")
    public String login(Model model, @ModelAttribute("employeeCreateRequestDTO") EmployeeCreateRequestDTO requestDTO) {
        if (loggedIn())
            return HOME;
        else {
            EmployeeResponseDTO loginEmployee = employeeService.validateLogin(requestDTO);
            if (loginEmployee != null) {
                HttpSession session = Validator.getSession();
                session.setAttribute(EMPLOYEE_LOGGED_IN, loginEmployee.getUsername());
                session.setAttribute(EMPLOYEE_ID, loginEmployee.getEmployeeID());
                //  Get all Files
                List<FileResponseDTO> authorizedFiles = fileService.getAllFilesOfEmployee(loginEmployee.getEmployeeID());
                model.addAttribute(AUTHORIZED_FILES, authorizedFiles);
                return HOME;
            } else {
                return LOGIN;
            }
        }
    }

    @Override
    public boolean loggedIn() {
        return validator.isLoggedIn();
    }
}
