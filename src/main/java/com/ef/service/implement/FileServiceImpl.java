package com.ef.service.implement;

import com.ef.entity.BaseEntity;
import com.ef.entity.Employee;
import com.ef.entity.EmployeeFile;
import com.ef.entity.FileSharing;
import com.ef.exception.CustomException;
import com.ef.model.response.FileResponseDTO;
import com.ef.repository.EmployeeRepository;
import com.ef.repository.FileRepository;
import com.ef.service.FileService;
import com.ef.utils.ExceptionGenerator;
import com.ef.utils.FileUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.ef.constant.CommonConstant.EntityNameConstant.EMPLOYEE;
import static com.ef.constant.CommonConstant.EntityNameConstant.FILE;
import static com.ef.constant.CommonConstant.FieldNameConstant.EMPLOYEE_ID;
import static com.ef.constant.CommonConstant.FieldNameConstant.FILE_ID;
import static com.ef.constant.CommonConstant.FileConstant.COMPRESSES_FILE;

@AllArgsConstructor
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private final EmployeeRepository employeeRepository;
    private final FileRepository fileRepository;

    @Qualifier("uploadPath")
    private final Path uploadPath;

    @Qualifier("tempPath")
    private final Path tempPath;

    @Override
    public List<FileResponseDTO> uploadFile(Long employeeID, MultipartFile[] files) {
        Employee employee = getEmployee(employeeID);

        List<EmployeeFile> employeeFileList = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                Path uploadURL = FileUtil.storeFile(uploadPath, employeeID, file);
                EmployeeFile employeeFile = EmployeeFile.builder()
                        .employee(employee)
                        .fileName(file.getOriginalFilename())
                        .url(uploadPath.toAbsolutePath().relativize(uploadURL.toAbsolutePath()).toString())
                        .build();

                employeeFileList.add(employeeFile);
            } catch (Exception e) {
                log.error("Error uploading {} to employee {}", file.getOriginalFilename(), employee.getUsername());
            }
        }

        employeeFileList = fileRepository.saveAll(employeeFileList);
        return employeeFileList.stream().map(this::toFileResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<FileResponseDTO> deleteFileInDB(Long employeeID, Set<Long> fileIDs) {
        getEmployee(employeeID);

        List<EmployeeFile> files = fileRepository.findAllByFileIDInAndDeletedIsFalse(fileIDs);

        files.forEach(employeeFile -> employeeFile.setDeleted(true));
        files = fileRepository.saveAll(files);
        return files.stream().map(this::toFileResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<FileResponseDTO> deleteFileInDirectory(List<FileResponseDTO> fileResponseDTOList) {
        Iterator fileIterator = fileResponseDTOList.iterator();

        while ((fileIterator.hasNext())) {
            FileResponseDTO fileResponseDTO = (FileResponseDTO) fileIterator.next();
            boolean deleteSuccess = FileUtil.deleteFileByUrl(uploadPath.toAbsolutePath().resolve(fileResponseDTO.getUrl()).toString());
            if (deleteSuccess) {
                log.info("File {} of employee {} has been deleted SUCCESSFULLY!", fileResponseDTO.getFileName(), fileResponseDTO.getEmployeeID());
            } else {
                log.error("File {} of employee {} was FAILED to delete!", fileResponseDTO.getFileName(), fileResponseDTO.getEmployeeID());
                fileIterator.remove();
            }
        }

        return fileResponseDTOList;
    }

    @Override
    public Resource downloadFileByFileID(Long fileID) throws IOException {
        EmployeeFile file = fileRepository.getByFileIDAndDeletedIsFalse(fileID);
        if (file == null)
            throw new CustomException(ExceptionGenerator.notFound(FILE, FILE_ID, fileID));

        return FileUtil.download(uploadPath.toAbsolutePath().resolve(file.getUrl()));
    }

    @Override
    public Resource zipAndDownloadFiles(Long employeeID, Set<Long> fileIds) throws IOException {
        List<EmployeeFile> downloadFileList = fileRepository.findAllByFileIDInAndDeletedIsFalse(fileIds);

        List<File> filesToZip = new ArrayList<>();
        downloadFileList.forEach(employeeFile -> {
            File file = new File(uploadPath.toAbsolutePath().resolve(employeeFile.getUrl()).toString());
            if (file.exists()) {
                filesToZip.add(file);
            }
        });

        String zipFileUrl = FileUtil.zipFiles(tempPath, COMPRESSES_FILE, filesToZip);
        return FileUtil.download(Paths.get(zipFileUrl));
    }

    @Override
    public List<FileResponseDTO> getAllFilesOfEmployee(Long employeeID) {
        Employee employee = getEmployee(employeeID);

        Set<EmployeeFile> files = employee.getFiles();
        files.removeIf(BaseEntity::isDeleted);

        return files.stream().map(this::toFileResponseDTO).collect(Collectors.toList());
    }

    /*  ADD-ONs */

    private Employee getEmployee(Long employeeID) {
        Employee employee = employeeRepository.getByEmployeeIDAndDeletedIsFalse(employeeID);
        if (employee == null)
            throw new CustomException(ExceptionGenerator.notFound(EMPLOYEE, EMPLOYEE_ID, employeeID));

        return employee;
    }

    private FileResponseDTO toFileResponseDTO(EmployeeFile file) {

        Set<FileSharing> sharings = CollectionUtils.isEmpty(file.getFileSharings()) ? new HashSet<>()
                : file.getFileSharings();
        sharings.removeIf(BaseEntity::isDeleted);

        Set<Long> sharingEmployeeIDs = sharings.stream()
                .map(fileSharing -> fileSharing.getEmployee().getEmployeeID()).collect(Collectors.toSet());

        return FileResponseDTO.builder()
                .employeeID(file.getEmployee().getEmployeeID())
                .fileID(file.getFileID())
                .fileName(file.getFileName())
                .url(file.getUrl())
                .createdOn(file.getCreatedOn())
                .sharedEmployeeID(sharingEmployeeIDs)
                .build();
    }
}
