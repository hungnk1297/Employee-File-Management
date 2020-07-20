package com.ef.utils;

import com.ef.constant.CommonConstant;
import com.ef.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

@Slf4j
@Component
public class FileUtil {

    private FileUtil() {
    }

    public static Path storeFile(Path uploadPath, Long employeeID, MultipartFile file) {

        makeDirectoryByEmployeeID(uploadPath, employeeID);

        //  Append employeeID as a sub folder when saving
        uploadPath = uploadPath.toAbsolutePath();
        uploadPath = uploadPath.resolve(employeeID.toString());

        //  Generate unique file name
        String appendedEpochFilename = appendEpochTimeToFileName(file.getOriginalFilename());

        uploadPath = uploadPath.resolve(appendedEpochFilename);

        try {
            //  Copy file to the upload path created
            Files.copy(file.getInputStream(), uploadPath.toAbsolutePath());
            return uploadPath;
        } catch (IOException e) {
            log.error("Error when uploading file {} to employee {}", file.getOriginalFilename(), employeeID, e);
            throw new CustomException(ExceptionGenerator.uploadFailed());
        }
    }

    public static String appendEpochTimeToFileName(String originalFileName) {
        return getBaseName(originalFileName) + "_" + Instant.now().toEpochMilli() + getExtension(originalFileName);
    }

    public static String getBaseName(String originalFileName) {
        int dotPosition = originalFileName.lastIndexOf(".");
        if (dotPosition > 0 && dotPosition < originalFileName.length() - 1) {
            return originalFileName.substring(0, dotPosition);
        }
        return CommonConstant.EMPTY_STRING;
    }

    public static String getExtension(String originalFileName) {
        int dotPosition = originalFileName.lastIndexOf(".");
        if (dotPosition > 0 && dotPosition < originalFileName.length() - 1) {
            return originalFileName.substring(dotPosition);
        }
        return CommonConstant.EMPTY_STRING;
    }

    public static void makeDirectoryByEmployeeID(Path uploadPath, Long employeeID) {
        File directory = new File(uploadPath.resolve(employeeID.toString()).toAbsolutePath().toString());

        if (!directory.exists()) {
            boolean success = directory.mkdirs();
            if (!success)
                log.error("Error when creating employee directory file for employee {}", employeeID);
        }
    }

}
