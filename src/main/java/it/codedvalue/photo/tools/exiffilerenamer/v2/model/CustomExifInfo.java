package it.codedvalue.photo.tools.exiffilerenamer.v2.model;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Arthur Arts
 */

@RequiredArgsConstructor
public class CustomExifInfo {
    private SimpleDateFormat sm = new SimpleDateFormat("yyyyMMdd");
    @Setter
    private final Metadata metadata;

    public String getCreationDateYYYYMMDD() {
        if (Objects.nonNull(this.metadata)) {
            List<Directory> result = new ArrayList<>();
            metadata.getDirectories().iterator().forEachRemaining(result::add);
            Date creationDate = result.get(4).getDate(3);
            return sm.format(creationDate);
        }
        return "";
    }
}
