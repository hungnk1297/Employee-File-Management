package com.finall.controller_mvc;

import com.finall.controller_mvc.filter.LoginRequiredController;
import com.finall.model.request.DownloadFromFormRequestDTO;
import com.finall.model.response.FileResponseDTO;
import com.finall.service.FileService;
import com.finall.utils.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

import static com.finall.constant.CommonConstant.AttributeConstant.*;
import static com.finall.constant.CommonConstant.TemplateConstant.*;

@Controller
@Slf4j
@RequestMapping(path = "/")
@AllArgsConstructor
public class HomeController implements LoginRequiredController {

    private final Validator validator;
    private final FileService fileService;

    @GetMapping("/")
    public String index() {
        if (isLoggedIn()) {
            return REDIRECT + HOME;
        } else
            return INDEX;
    }

    @GetMapping("/home")
    public String home(Model model) {
        if (isLoggedIn()) {
            HttpSession session = Validator.getSession();
            Long employeeIDFromSession = (Long) session.getAttribute(EMPLOYEE_ID);
            //  Get all Files
            List<FileResponseDTO> authorizedFiles = fileService.getAllFilesOfEmployee(employeeIDFromSession);
            model.addAttribute(AUTHORIZED_FILES, authorizedFiles);
            //  Form download
            model.addAttribute(FORM_DOWNLOAD, new DownloadFromFormRequestDTO());
            return HOME;
        } else
            return INDEX;
    }

    @Override
    public boolean isLoggedIn() {
        return validator.isLoggedIn();
    }
}
