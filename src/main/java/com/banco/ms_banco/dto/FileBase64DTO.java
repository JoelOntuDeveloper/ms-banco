package com.banco.ms_banco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileBase64DTO {
    private String fileName;
    private String fileType;
    private String base64Content;
}
