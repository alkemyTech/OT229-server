package com.alkemy.ong.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncodedImageDTO {

    @NotBlank
    @Schema(description = "String representing the encoded image in base64. Will be quite long.", example = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAFCAIAAADDivseAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAA5SURBVBhXZctBDgAhEAJB//9pBJkQMvZhU6x6EoDRc2ZbeeSvwdpuLkdGzP6pb9To1ul+4HraTAYuqMKPcY0zc9EAAAAASUVORK5CYII=")
    private String encoded_string;
    @NotBlank
    @Schema(description = "The file name with its extension.", example = "file_name.jpg")
    private String file_name;

}
