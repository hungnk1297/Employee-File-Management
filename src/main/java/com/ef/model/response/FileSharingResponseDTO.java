package com.ef.model.response;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileSharingResponseDTO extends BaseResponseDTO {

    private Long fileSharingID;

    private Long employeeID;

    private Long fileID;

    private Long sharingEmployeeID;

    private LocalDateTime createdOn;

    private boolean deleted;
}
