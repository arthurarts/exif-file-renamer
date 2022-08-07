package it.codedvalue.photo.tools.exiffilerenamer.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Arthur Arts
 */
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RenameTotalResult {

    private Integer totalRenamed;
    private List<Map<String, String>> totalOldvsNew;
    private List<RenameSingleResultImage> imageRenamingResults;
    private List<RenameSingleResultSpecific> specificRenamingResults;

    public Integer getTotalRenamed() {
        return totalOldvsNew.size();
    }
}
