package com.finall.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstant {

    public static final String TOKEN_SIGNATURE = "lkzdhvakjoerwhgbADKLFHvlkjdzhf";
    public static final String EMPTY_STRING = "";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ApiErrorStringConstant {
        public static final String DUPLICATED_USERNAME = "The username is existed!";
        public static final String ENTITY_NOT_FOUND = "The Entity is NOT found!";
        public static final String USERNAME_OR_PASSWORD_INCORRECT = "Username or Password is incorrect!";
        public static final String UNAUTHORIZED = "The Employee Token is Unauthorized!";
        public static final String FILE_UPLOAD_FAILED = "Failed when uploading file!";
        public static final String NO_READ_PERMISSION = "You have no read permission for the file!";
        public static final String NOT_OWNER_OF_THE_FILE = "You are not the owner of the file!";
        public static final String GENERATED_LINK_NOT_FOUND = "The file with the generated download link is NOT found or no longer exist!";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FieldNameConstant {
        public static final String USERNAME = "username";
        public static final String EMPLOYEE_ID = "employeeID";
        public static final String FILE_ID = "fileID";
        public static final String URL = "url";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EntityNameConstant {
        public static final String EMPLOYEE = "Employee";
        public static final String FILE = "File";
        public static final String FILE_SHARING = "File_Sharing";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FileConstant {
        public static final String ZIP_EXTENSION = ".zip";
        public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
        public static final String ATTACHMENT_FILENAME = "attachment; filename=\"";
        public static final String KEY_SLASH = "\"";
        public static final String COMPRESSES_FILE = "Compressed-files";
        public static final String KILOBYTE = "KB";
        public static final int a_LOWER = 'a';
        public static final int A_UPPER = 'A';
        public static final int z_LOWER = 'z';
        public static final int Z_UPPER = 'Z';
        public static final int ZERO = '0';
        public static final int NINE = '9';

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AttributeConstant {
        public static final String EMPLOYEE_LOGGED_IN = "employeeLoggedIn";
        public static final String EMPLOYEE_ID = "employeeID";
        public static final String AUTHORIZED_FILES = "authorizedFiles";
        public static final String SUCCESS_REGISTER_EMPLOYEE = "success_registerEmployee";
        public static final String ERROR_UPLOAD_FILE = "error_UploadFile";
        public static final String FORM_DOWNLOAD = "formDownloadRequestDTO";

        public static final List<String> ALL_ATTRIBUTES = Arrays.asList(
                EMPLOYEE_LOGGED_IN,
                EMPLOYEE_ID
        );
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TemplateConstant {
        public static final String REDIRECT = "redirect:/";
        public static final String INDEX = "index";
        public static final String HOME = "home";
        public static final String LOGIN = "employee/login";
        public static final String REGISTER = "employee/register";
    }
}
