package com.ef.utils;

import com.ef.exception.ApiError;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static com.ef.constant.CommonConstant.ApiErrorStringConstant.*;
import static com.ef.constant.CommonConstant.EntityNameConstant.FILE;
import static com.ef.constant.CommonConstant.FieldNameConstant.FILE_ID;
import static com.ef.constant.CommonConstant.FieldNameConstant.USERNAME;

public class ExceptionGenerator {

    private ExceptionGenerator(){}

    public static ApiError duplicateUsername(String username) {
        return ApiError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .statusMessage(DUPLICATED_USERNAME)
                .timestamp(LocalDateTime.now())
                .fieldName(USERNAME)
                .fieldValue(username)
                .build();
    }

    public static ApiError notFound(String entityName, String fieldName, Object fieldValue) {
        return ApiError.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .statusMessage(ENTITY_NOT_FOUND)
                .timestamp(LocalDateTime.now())
                .entityName(entityName)
                .fieldName(fieldName)
                .fieldValue(fieldValue)
                .build();
    }

    public static ApiError invalidLogin() {
        return ApiError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .statusMessage(USERNAME_OR_PASSWORD_INCORRECT)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ApiError unAuthorize() {
        return ApiError.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .statusMessage(UNAUTHORIZED)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ApiError uploadFailed() {
        return ApiError.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .statusMessage(FILE_UPLOAD_FAILED)
                .timestamp(LocalDateTime.now())
                .entityName(FILE)
                .build();
    }

    public static ApiError noReadPermission(Long fileID) {
        return ApiError.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .statusMessage(NO_READ_PERMISSION)
                .timestamp(LocalDateTime.now())
                .entityName(FILE)
                .fieldName(FILE_ID)
                .fieldValue(fileID)
                .build();
    }

    public static ApiError notOwnerOfTheFile() {
        return ApiError.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .statusMessage(NOT_OWNER_OF_THE_FILE)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
