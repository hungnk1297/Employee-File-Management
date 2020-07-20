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
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ef.constant.CommonConstant.EntityNameConstant.EMPLOYEE;
import static com.ef.constant.CommonConstant.FieldNameConstant.EMPLOYEE_ID;

@AllArgsConstructor
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private final EmployeeRepository employeeRepository;
    private final FileRepository fileRepository;

    private final Path uploadPath;

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
        return null;
    }

    @Override
    public List<FileResponseDTO> deleteFileInDirectory(List<FileResponseDTO> fileResponseDTOList) {
        return null;
    }

    @Override
    public Resource downloadFileByFileID(Long fileID) {
        return null;
    }

    @Override
    public Resource zipAndDownloadFiles(Set<Long> fileIds) {
        return null;
    }

    @Override
    public List<FileResponseDTO> getAllFilesOfEmployee(Long employeeID) {
        return null;
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
