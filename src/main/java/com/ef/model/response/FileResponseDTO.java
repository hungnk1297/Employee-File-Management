package com.ef.model.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileResponseDTO {

    private Long fileID;
    private Long employeeID;

    private String fileName;
    private String url;

    private LocalDateTime createdOn;

    private Set<Long> sharedEmployeeID;
}
