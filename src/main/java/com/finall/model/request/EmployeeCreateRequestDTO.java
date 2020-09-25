package com.finall.model.request;

import com.finall.constant.EmployeeType;
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
