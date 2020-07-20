package com.ef.constant;

public class CommonConstant {

    public static final String TOKEN_SIGNATURE = "lkzdhvakjoerwhgbADKLFHvlkjdzhf";
    public static final String EMPTY_STRING = "";

    public static class ApiErrorStringConstant{
        public static final String DUPLICATED_USERNAME = "The username is existed!";
        public static final String ENTITY_NOT_FOUND = "The Entity is NOT found!";
        public static final String USERNAME_OR_PASSWORD_INCORRECT = "Username or Password is incorrect!";
        public static final String UNAUTHORIZED = "The Employee Token is Unauthorized!";
        public static final String FILE_UPLOAD_FAILED = "Failed when uploading file!";
    }

    public static class FieldNameConstant{
        public static final String USERNAME = "username";
        public static final String EMPLOYEE_ID = "employeeID";
    }

    public static class EntityNameConstant{
        public static final String EMPLOYEE = "Employee";
        public static final String FILE = "File";
        public static final String FILE_SHARING = "File_Sharing";
    }
}
