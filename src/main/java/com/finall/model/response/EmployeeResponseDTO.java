package com.finall.model.response;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponseDTO extends BaseResponseDTO {

    private Long employeeID;

    private String username;

    private String employeeType;

    private String employeeToken;

}
