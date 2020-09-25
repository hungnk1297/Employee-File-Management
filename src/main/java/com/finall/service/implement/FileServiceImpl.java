package com.finall.service.implement;

import com.finall.config.FileStorageConfig;
import com.finall.entity.*;
import com.finall.exception.CustomException;
import com.finall.model.response.FileResponseDTO;
import com.finall.model.response.GenerateLinkResponseDTO;
import com.finall.repository.EmployeeRepository;
import com.finall.repository.FileRepository;
import com.finall.repository.GenerateLinkRepository;
import com.finall.service.FileService;
import com.finall.service.FileSharingService;
import com.finall.utils.ExceptionGenerator;
import com.finall.utils.FileUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import static com.finall.constant.CommonConstant.FileConstant.KILOBYTE;
import static com.finall.constant.CommonConstant.FileConstant.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.finall.constant.CommonConstant.EntityNameConstant.EMPLOYEE;
import static com.finall.constant.CommonConstant.EntityNameConstant.FILE;
import static com.finall.constant.CommonConstant.FieldNameConstant.EMPLOYEE_ID;
import static com.finall.constant.CommonConstant.FieldNameConstant.FILE_ID;
import static com.finall.constant.CommonConstant.FileConstant.COMPRESSES_FILE;

@AllArgsConstructor
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private final EmployeeRepository employeeRepository;
    private final FileRepository fileRepository;
    private final GenerateLinkRepository generateLinkRepository;

    private final FileSharingService fileSharingService;

    private final FileStorageConfig fileStorageConfig;

    @Qualifier("uploadPath")
    private final Path uploadPath;

    @Qualifier("tempPath")
    private final Path tempPath;

    @Transactional
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

    @Transactional
    @Override
    public List<FileResponseDTO> deleteFileInDB(Long employeeID, Set<Long> fileIDs) {
        getEmployee(employeeID);

        List<EmployeeFile> files = fileRepository.findAllByFileIDInAndDeletedIsFalse(fileIDs);

        files.forEach(employeeFile -> {
            fileSharingService.stopSharingFile(employeeFile.getFileID(), new HashSet<>());
            employeeFile.setDeleted(true);
        });
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

        List<EmployeeFile> employeeFileList = new ArrayList<>(files);
        employeeFileList.sort(Comparator.comparing(BaseEntity::getCreatedOn).reversed());

        return employeeFileList.stream().map(this::toFileResponseDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<FileResponseDTO> deleteAllFileOfEmployee(Long employeeID) {
        getEmployee(employeeID);

        List<EmployeeFile> employeeFileList = fileRepository.findAllByEmployee_EmployeeIDAndDeletedIsFalse(employeeID);

        employeeFileList.forEach(employeeFile -> employeeFile.setDeleted(true));
        employeeFileList = fileRepository.saveAll(employeeFileList);

        List<FileResponseDTO> fileResponseDTOList = employeeFileList.stream().map(this::toFileResponseDTO).collect(Collectors.toList());
        return deleteFileInDirectory(fileResponseDTOList);
    }

    @Transactional
    @Override
    public GenerateLinkResponseDTO generateDownLoadLink(Long fileID) {
        List<GenerateLink> generateLinkFromDB = generateLinkRepository.getUnExpireGeneratedLink(fileID, LocalDateTime.now());

        GenerateLink generated = CollectionUtils.isEmpty(generateLinkFromDB) ? null : generateLinkFromDB.get(0);

        LocalDateTime newExpireTime = LocalDateTime.now().plusHours(fileStorageConfig.getGenerateLinkExpireHours());
        if (generated != null) {
            //            generated case, extend expire time
            generated.setExpireTime(newExpireTime);
        } else {
            //            Add new case
            EmployeeFile file = fileRepository.getByFileIDAndDeletedIsFalse(fileID);
            if (file == null)
                throw new CustomException(ExceptionGenerator.notFound(FILE, FILE_ID, fileID));

            generated = GenerateLink.builder()
                    .employeeFile(file)
                    .link(this.generateRandomLink())
                    .expireTime(newExpireTime)
                    .build();
        }
        generateLinkRepository.save(generated);
        return toGenerateLinkResponseDTO(generated);
    }

    @Override
    public Resource downloadFileByGeneratedLink(String generatedLink) throws IOException {
        GenerateLink existLink = generateLinkRepository.getByLinkAndExpireTimeAfter(generatedLink, LocalDateTime.now());
        if (existLink == null)
            throw new CustomException(ExceptionGenerator.expiredLink(generatedLink));

        return downloadFileByFileID(existLink.getEmployeeFile().getFileID());
    }

    /*  ADD-ONs */

    private Employee getEmployee(Long employeeID) {
        Employee employee = employeeRepository.getByEmployeeIDAndDeletedIsFalse(employeeID);
        if (employee == null)
            throw new CustomException(ExceptionGenerator.notFound(EMPLOYEE, EMPLOYEE_ID, employeeID));

        return employee;
    }

    private FileResponseDTO toFileResponseDTO(EmployeeFile file) {

        Path filePath = uploadPath.resolve(file.getUrl()).toAbsolutePath();
        File currentFile = new File(filePath.toString());

        Set<FileSharing> sharings = CollectionUtils.isEmpty(file.getFileSharings()) ? new HashSet<>()
                : file.getFileSharings();
        sharings.removeIf(BaseEntity::isDeleted);

        Set<Long> sharingEmployeeIDs = sharings.stream()
                .map(fileSharing -> fileSharing.getSharingEmployee().getEmployeeID()).collect(Collectors.toSet());

        String sharedEmployeeNameStr = sharings.stream()
                .map(fileSharing -> fileSharing.getSharingEmployee().getUsername()).collect(Collectors.joining("; "));

        //  Generate Link
        List<GenerateLink> generateLinks = generateLinkRepository.getUnExpireGeneratedLink(file.getFileID(), LocalDateTime.now());
        GenerateLink generatedLink = CollectionUtils.isEmpty(generateLinks) ? null : generateLinks.get(0);

        return FileResponseDTO.builder()
                .employeeID(file.getEmployee().getEmployeeID())
                .fileID(file.getFileID())
                .fileName(file.getFileName())
                .url(file.getUrl())
                .sharedEmployeeID(sharingEmployeeIDs)
                .createdOn(file.getCreatedOn())
                .sharedEmployeeNameStr(sharedEmployeeNameStr)
                .fileSizeInMegaByte((currentFile.length() / 1024) + KILOBYTE)
                .generatedLink(generatedLink == null ? null : generatedLink.getLink())
                .build();
    }

    private GenerateLinkResponseDTO toGenerateLinkResponseDTO(GenerateLink generateLink) {
        return generateLink == null ? null
                : GenerateLinkResponseDTO.builder()
                .fileID(generateLink.getEmployeeFile().getFileID())
                .createdOn(generateLink.getCreatedOn())
                .expireTime(generateLink.getExpireTime())
                .generatedLink(generateLink.getLink())
                .build();
    }

    private String generateRandomLink() {
        Random random = new Random();
        String generatedLink;
        do {
            generatedLink = random.ints(ZERO, z_LOWER + 1)
                    .filter(i -> (i >= ZERO && i <= NINE)
                            || (i >= A_UPPER && i <= Z_UPPER)
                            || (i >= a_LOWER && i <= z_LOWER))
                    .limit(fileStorageConfig.getGenerateLinkLength())
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        } while (generateLinkRepository.existsByLink(generatedLink));
        return generatedLink;
    }
}
