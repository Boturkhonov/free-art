package edu.istu.freeart.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormWrapper {

    private MultipartFile image;

    private String title;

    private String description;

    private Long[] tags;

}