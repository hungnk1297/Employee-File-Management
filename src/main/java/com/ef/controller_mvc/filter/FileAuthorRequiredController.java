package com.ef.controller_mvc.filter;

public interface FileAuthorRequiredController extends LoginRequiredController {

    boolean isFileAuthor(Long employeeID, Long fileID);
}
