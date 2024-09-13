package it.codedvalue.photo.tools.exiffilerenamer.v2.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * @author Arthur Arts
 */

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RenameTotalResult {


    public RenameTotalResult(List<ImageDataRenameResult> renameSingleResultImages, List<SpecificHandlingRenameResult> specificHandlingRenameResults) {
        this.totalOldvsNew =
                renameSingleResultImages.stream().map(ImageDataRenameResult::getOldVSNewImageName).filter(Objects::nonNull).collect(Collectors.toList());
        this.totalOldvsNew.addAll(specificHandlingRenameResults.stream().map(SpecificHandlingRenameResult::getOldVSNewVidName).filter(Objects::nonNull).collect(Collectors.toList()));
        this.imageRenamingResults = renameSingleResultImages;
        this.specificRenamingResults = specificHandlingRenameResults;
    }

    private Integer totalRenamed;
    private List<Map<String, String>> totalOldvsNew;
    private List<ImageDataRenameResult> imageRenamingResults;
    private List<SpecificHandlingRenameResult> specificRenamingResults;

    public Integer getTotalRenamed() {
        return totalOldvsNew.size();
    }


}
