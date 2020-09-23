package com.ef.controller_mvc;

import com.ef.controller_mvc.filter.FileAuthorRequiredController;
import com.ef.controller_mvc.filter.FileReadPermissionRequiredController;
import com.ef.controller_mvc.filter.LoginRequiredController;
import com.ef.exception.CustomException;
import com.ef.model.response.FileResponseDTO;
import com.ef.service.FileService;
import com.ef.utils.ExceptionGenerator;
import com.ef.utils.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static com.ef.constant.CommonConstant.AttributeConstant.*;
import static com.ef.constant.CommonConstant.FileConstant.*;
import static com.ef.constant.CommonConstant.TemplateConstant.HOME;

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

        return HOME;
    }

    @GetMapping(path = "/download")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable(value = "employeeID") Long employeeID,
            @RequestParam(value = "fileID") Long fileID,
            HttpServletRequest request) throws IOException {
        if (loggedIn() && isFileReadable(employeeID, fileID)) {
            Resource resource = fileService.downloadFileByFileID(fileID);
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (Exception e) {
                log.error("Could not get the content type!");
            }
            contentType = contentType != null ? contentType : DEFAULT_CONTENT_TYPE;

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + resource.getFilename() + KEY_SLASH)
                    .body(resource);
        } else
            throw new CustomException(ExceptionGenerator.noReadPermission(fileID));
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
    public boolean loggedIn() {
        return validator.isLoggedIn();
    }
}
