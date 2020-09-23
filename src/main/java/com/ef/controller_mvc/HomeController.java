package com.ef.controller_mvc;

import com.ef.controller_mvc.filter.LoginRequiredController;
import com.ef.utils.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.ef.constant.CommonConstant.TemplateConstant.HOME;
import static com.ef.constant.CommonConstant.TemplateConstant.INDEX;

@Controller
@Slf4j
@RequestMapping(path = "/")
@AllArgsConstructor
public class HomeController implements LoginRequiredController {

    private final Validator validator;

    @GetMapping("/")
    public String index(Model model) {
        return loggedIn() ? HOME : INDEX;
    }

    @Override
    public boolean loggedIn() {
        return validator.isLoggedIn();
    }
}
