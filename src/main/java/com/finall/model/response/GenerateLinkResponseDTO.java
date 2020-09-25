package com.finall.model.response;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateLinkResponseDTO {

    private Long generateLinkID;

    private Long fileID;

    private String generatedLink;

    private LocalDateTime createdOn;

    private LocalDateTime expireTime;
}
