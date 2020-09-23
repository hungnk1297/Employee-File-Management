package com.ef.controller_mvc;

import com.ef.controller_mvc.filter.LoginRequiredController;
import com.ef.model.request.EmployeeCreateRequestDTO;
import com.ef.model.response.EmployeeResponseDTO;
import com.ef.service.EmployeeService;
import com.ef.utils.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.ef.constant.CommonConstant.TemplateConstant.*;

@Controller
@Slf4j
@RequestMapping(path = "/employee")
@AllArgsConstructor
public class EmployeeController implements LoginRequiredController {

    private final EmployeeService employeeService;
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
            return LOGIN;
        } else {
            return REGISTER;
        }
    }

    //  Log in
    @GetMapping(path = "/login")
    public String getLogin() {
        return LOGIN;
    }

    @PostMapping(path = "/login")
    public String login(Model model, @ModelAttribute("employeeCreateRequestDTO") EmployeeCreateRequestDTO employeeCreateRequestDTO) {
        if (loggedIn())
            return HOME;
        else {
            if (employeeService.validateLogin(employeeCreateRequestDTO)) {
                return HOME;
            }
            else {
                return LOGIN;
            }
        }
    }

    @Override
    public boolean loggedIn() {
        return validator.isLoggedIn();
    }
}
