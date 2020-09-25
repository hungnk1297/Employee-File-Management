package com.finall.utils;

import com.finall.constant.CommonConstant;
import com.finall.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.finall.constant.CommonConstant.EntityNameConstant.FILE;
import static com.finall.constant.CommonConstant.FieldNameConstant.URL;

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

    public static boolean deleteFileByUrl(String url) {
        try {
            if (Paths.get(url).toFile().exists()) {
                Files.deleteIfExists(Paths.get(url));
                log.info("File deleted successfully! ({})", url);
                return true;
            } else {
                log.error("The file is not exist to be deleted! {}", url);
                return false;
            }
        } catch (Exception e) {
            log.error("Error when deleting file in directory {}", url);
            return false;
        }
    }

    public static Resource download(Path filePath) throws IOException {
        if (Files.notExists(filePath)) {
            throw new CustomException(ExceptionGenerator.notFound(FILE, URL, filePath.toString()));
        }
        return new UrlResource(filePath.toUri());
    }

    public static String zipFiles(Path directory, String zippedFileName, List<File> fileToZip) {
        String zipPath = directory.toAbsolutePath().toString() +
                File.separator + zippedFileName + CommonConstant.FileConstant.ZIP_EXTENSION;

        if (Paths.get(zipPath).toFile().exists())
            Paths.get(zipPath).toFile().delete();

        try (FileOutputStream fos = new FileOutputStream(zipPath);
             ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos))) {
            zos.setLevel(9);

            for (File file : fileToZip) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry entry = new ZipEntry(file.getName());
                    zos.putNextEntry(entry);
                    for (int c = fis.read(); c != -1; c = fis.read()) {
                        zos.write(c);
                    }
                    zos.flush();
                }
            }
        } catch (Exception e) {
            log.error("Error when creating zip file!");
        }

        File zippedFile = new File(zipPath);
        if (!zippedFile.exists()) {
            log.error("The created zip file could not be found!");
        }
        return zippedFile.getAbsolutePath();
    }

}
