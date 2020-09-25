package com.finall.model.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareFileRequestDTO {

    @NotNull
    private Long fileID;

    @NotEmpty
    private Set<Long> sharedEmployeeIDs;
}
