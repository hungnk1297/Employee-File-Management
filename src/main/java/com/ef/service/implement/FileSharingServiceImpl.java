package com.ef.service.implement;

import com.ef.entity.Employee;
import com.ef.entity.EmployeeFile;
import com.ef.entity.FileSharing;
import com.ef.exception.CustomException;
import com.ef.model.response.FileSharingResponseDTO;
import com.ef.repository.EmployeeRepository;
import com.ef.repository.FileRepository;
import com.ef.repository.FileSharingRepository;
import com.ef.service.FileSharingService;
import com.ef.utils.ExceptionGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ef.constant.CommonConstant.EntityNameConstant.EMPLOYEE;
import static com.ef.constant.CommonConstant.EntityNameConstant.FILE;
import static com.ef.constant.CommonConstant.FieldNameConstant.EMPLOYEE_ID;
import static com.ef.constant.CommonConstant.FieldNameConstant.FILE_ID;

@Service
@Slf4j
@AllArgsConstructor
public class FileSharingServiceImpl implements FileSharingService {

    private final EmployeeRepository employeeRepository;
    private final FileRepository fileRepository;
    private final FileSharingRepository fileSharingRepository;

    @Override
    public List<FileSharingResponseDTO> findAllSharingFileOfEmployee(Long employeeID) {
        Employee employee = employeeRepository.getByEmployeeIDAndDeletedIsFalse(employeeID);
        if (employee == null)
            throw new CustomException(ExceptionGenerator.notFound(EMPLOYEE, EMPLOYEE_ID, employeeID));

        List<FileSharing> fileSharings = fileSharingRepository.findAllSharingOfEmployee(employeeID);

        return fileSharings.stream().map(this::toFileSharingResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<FileSharingResponseDTO> findAllSharedFileForEmployee(Long sharedEmployeeID) {
        Employee employee = employeeRepository.getByEmployeeIDAndDeletedIsFalse(sharedEmployeeID);
        if (employee == null)
            throw new CustomException(ExceptionGenerator.notFound(EMPLOYEE, EMPLOYEE_ID, sharedEmployeeID));

        List<FileSharing> fileSharingList = fileSharingRepository.findAllBySharingEmployee_EmployeeIDAndDeletedIsFalse(sharedEmployeeID);

        return fileSharingList.stream().map(this::toFileSharingResponseDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<FileSharingResponseDTO> shareFile(Long fileID, Set<Long> sharedEmployeeIDs) {
        EmployeeFile file = fileRepository.getByFileIDAndDeletedIsFalse(fileID);
        if (file == null)
            throw new CustomException(ExceptionGenerator.notFound(FILE, FILE_ID, fileID));

        List<Employee> employeeList = employeeRepository.findAllByEmployeeIDInAndDeletedIsFalse(sharedEmployeeIDs);

        List<FileSharing> fileSharingList = new ArrayList<>();
        for (Employee employee : employeeList) {
            FileSharing fileSharing = FileSharing.builder()
                    .employeeFile(file)
                    .sharingEmployee(employee)
                    .build();
            fileSharingList.add(fileSharing);
        }

        fileSharingList = fileSharingRepository.saveAll(fileSharingList);
        return fileSharingList.stream().map(this::toFileSharingResponseDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<FileSharingResponseDTO> stopSharingFile(Long fileID, Set<Long> sharedEmployeeIDs) {
        EmployeeFile file = fileRepository.getByFileIDAndDeletedIsFalse(fileID);
        if (file == null)
            throw new CustomException(ExceptionGenerator.notFound(FILE, FILE_ID, fileID));

        List<FileSharing> fileSharingList = fileSharingRepository.findAllByFileIDAndSharingEmployeeIDs(fileID, sharedEmployeeIDs);

        if (!CollectionUtils.isEmpty(fileSharingList)) {
            fileSharingList.forEach(fileSharing -> fileSharing.setDeleted(true));
        }

        fileSharingList = fileSharingRepository.saveAll(fileSharingList);
        return fileSharingList.stream().map(this::toFileSharingResponseDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<FileSharingResponseDTO> stopSharingFileWhenDeleteEmployee(Long employeeID) {
        Employee employee = employeeRepository.getByEmployeeIDAndDeletedIsFalse(employeeID);
        if (employee == null)
            throw new CustomException(ExceptionGenerator.notFound(EMPLOYEE, EMPLOYEE_ID, employeeID));

        List<FileSharing> fileSharingList = fileSharingRepository.findAllSharingOfEmployee(employeeID);

        if (!CollectionUtils.isEmpty(fileSharingList)) {
            fileSharingList.forEach(fileSharing -> fileSharing.setDeleted(true));
        }

        fileSharingList = fileSharingRepository.saveAll(fileSharingList);
        return fileSharingList.stream().map(this::toFileSharingResponseDTO).collect(Collectors.toList());
    }

    //  Add-Ons

    private FileSharingResponseDTO toFileSharingResponseDTO(FileSharing fileSharing) {
        return FileSharingResponseDTO.builder()
                .fileSharingID(fileSharing.getShareFileID())
                .fileID(fileSharing.getEmployeeFile().getFileID())
                .employeeID(fileSharing.getEmployeeFile().getEmployee().getEmployeeID())
                .sharingEmployeeID(fileSharing.getSharingEmployee().getEmployeeID())
                .createdOn(fileSharing.getCreatedOn())
                .deleted(fileSharing.isDeleted())
                .build();
    }
}
