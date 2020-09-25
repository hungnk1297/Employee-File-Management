package com.finall.model.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DownloadFromFormRequestDTO {

    private String generatedLink;
}
