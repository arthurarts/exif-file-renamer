package it.codedvalue.photo.tools.exiffilerenamer.v2.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.codedvalue.photo.tools.exiffilerenamer.v2.model.RenameSingleResultImage;
import it.codedvalue.photo.tools.exiffilerenamer.v2.model.RenameSingleResultSpecific;
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


    public RenameTotalResult(List<RenameSingleResultImage> renameSingleResultImages, List<RenameSingleResultSpecific> renameSingleResultSpecifics) {
        this.totalOldvsNew =
                renameSingleResultImages.stream().map(RenameSingleResultImage::getOldVSNewImageName).filter(Objects::nonNull).collect(Collectors.toList());
        this.totalOldvsNew.addAll(renameSingleResultSpecifics.stream().map(RenameSingleResultSpecific::getOldVSNewVidName).filter(Objects::nonNull).collect(Collectors.toList()));
        this.imageRenamingResults = renameSingleResultImages;
        this.specificRenamingResults = renameSingleResultSpecifics;
    }

    private Integer totalRenamed;
    private List<Map<String, String>> totalOldvsNew;
    private List<RenameSingleResultImage> imageRenamingResults;
    private List<RenameSingleResultSpecific> specificRenamingResults;

    public Integer getTotalRenamed() {
        return totalOldvsNew.size();
    }


}
