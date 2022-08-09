package it.codedvalue.photo.tools.exiffilerenamer.v2.service;

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
public class RenameSingleResultSpecific {
    private String logSpecificSuccess;
    private String logSpecificFail;
    private Map<String, String> oldVSNewVidName;
}
