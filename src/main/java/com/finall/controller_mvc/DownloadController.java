package com.finall.controller_mvc;

import com.finall.controller_mvc.filter.LoginRequiredController;
import com.finall.model.request.DownloadFromFormRequestDTO;
import com.finall.service.FileService;
import com.finall.utils.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.finall.constant.CommonConstant.FileConstant.*;

@Controller
@Slf4j
@RequestMapping(path = "/download")
@AllArgsConstructor
public class DownloadController implements LoginRequiredController {

    private final FileService fileService;
    private final Validator validator;

    @GetMapping(path = "/{generated-link}")
    public ResponseEntity<Resource> downloadFileWithGeneratedLink(
            @PathVariable(value = "generated-link") String generatedLink,
            HttpServletRequest request) throws IOException {
        if (isLoggedIn()) {
            Resource resource = fileService.downloadFileByGeneratedLink(generatedLink);
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
        }
        return null;
    }

    @PostMapping(path = "/from-form")
    public ResponseEntity<Resource> downloadFileFromForm(
            @ModelAttribute DownloadFromFormRequestDTO requestDTO,
            HttpServletRequest request) throws IOException {
        if (isLoggedIn()) {
            Resource resource = fileService.downloadFileByGeneratedLink(requestDTO.getGeneratedLink());
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
        }
        return null;
    }

    @Override
    public boolean isLoggedIn() {
        return validator.isLoggedIn();
    }

}
