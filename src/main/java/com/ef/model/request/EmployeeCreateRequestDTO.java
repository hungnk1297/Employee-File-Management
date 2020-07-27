package com.ef.model.request;

import com.ef.constant.EmployeeType;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeCreateRequestDTO implements Serializable {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private EmployeeType employeeType;
}
