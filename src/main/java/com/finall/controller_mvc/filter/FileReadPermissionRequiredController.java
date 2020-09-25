package com.finall.controller_mvc.filter;

public interface FileReadPermissionRequiredController extends LoginRequiredController {

    boolean isFileReadable(Long employeeID, Long fileID);
}
