package pl.edu.amu.wmi.model.externallink;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ExternalLinkDTO {

    @NotNull
    private String id;

    private String url;

    private String name;

    private String columnHeader;

    private LocalDate deadline;

    private String contentType;

    // File related fields for internal uploads
    private String linkType;
    private String originalFileName;
    private Long fileSize;

    // Date fields
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;

}

