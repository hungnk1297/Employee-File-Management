package com.ef.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponseDTO implements Serializable {

    private boolean deleted;

    private LocalDateTime createdOn;

    private LocalDateTime lastModified;
}
