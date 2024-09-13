package it.codedvalue.photo.tools.exiffilerenamer.v2.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Arthur Arts
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageDataRenameResult {

    private String renameSuccessLog;
    private String renameFailLog;
    private Map<String, String> oldVSNewImageName;

}
