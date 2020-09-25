package com.finall.controller_mvc;

import com.finall.controller_mvc.filter.FileAuthorRequiredController;
import com.finall.controller_mvc.filter.FileReadPermissionRequiredController;
import com.finall.controller_mvc.filter.LoginRequiredController;
import com.finall.exception.CustomException;
import com.finall.model.response.FileResponseDTO;
import com.finall.service.FileService;
import com.finall.utils.ExceptionGenerator;
import com.finall.utils.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

import static com.finall.constant.CommonConstant.AttributeConstant.*;
import static com.finall.constant.CommonConstant.TemplateConstant.HOME;
import static com.finall.constant.CommonConstant.TemplateConstant.REDIRECT;

@Controller
@Slf4j
@RequestMapping(path = "/employee/{employeeID}/file")
@AllArgsConstructor
public class FileController implements LoginRequiredController, FileAuthorRequiredController, FileReadPermissionRequiredController {

    private final FileService fileService;
    private final Validator validator;

    @PostMapping
    public String uploadFile(Model model,
                             @PathVariable(value = "employeeID") Long employeeID,
                             @RequestPart MultipartFile[] files) {
        HttpSession session = Validator.getSession();
        Long employeeIDFromSession = (Long) session.getAttribute(EMPLOYEE_ID);
        if (employeeIDFromSession != null && employeeIDFromSession.equals(employeeID)) {
            fileService.uploadFile(employeeID, files);
            List<FileResponseDTO> authorizedFiles = fileService.getAllFilesOfEmployee(employeeID);
            model.addAttribute(AUTHORIZED_FILES, authorizedFiles);
            model.addAttribute(ERROR_UPLOAD_FILE, null);
        } else
            model.addAttribute(ERROR_UPLOAD_FILE, true);
        return REDIRECT + HOME;
    }

    @GetMapping(path = "/generate")
    public String generateDownloadLink(@PathVariable(value = "employeeID") Long employeeID,
                                       @RequestParam(value = "fileID") Long fileID) {
        if (isLoggedIn() && isFileAuthor(employeeID, fileID)) {
            fileService.generateDownLoadLink(fileID);
            return REDIRECT + HOME;
        } else
            throw new CustomException(ExceptionGenerator.notOwnerOfTheFile());
    }

    //  Addon
    @Override
    public boolean isFileAuthor(Long employeeID, Long fileID) {
        return validator.isOwnerOfFile(employeeID, fileID);
    }

    @Override
    public boolean isFileReadable(Long employeeID, Long fileID) {
        return validator.authorizedReadPermission(employeeID, fileID);
    }

    @Override
    public boolean isLoggedIn() {
        return validator.isLoggedIn();
    }
}
