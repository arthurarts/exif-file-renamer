package it.codedvalue.photo.tools.exiffilerenamer.model;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * @author Arthur Arts
 */

@AllArgsConstructor
public class CustomExifInfo {

    @Setter
    private String creationDateYYYYMMDD;

    public String getCreationDateYYYYMMDD() {
        if (Objects.nonNull(this.creationDateYYYYMMDD)) {
            return creationDateYYYYMMDD.replace(":", "").substring(0, 8);
        }
        return "";
    }
}
