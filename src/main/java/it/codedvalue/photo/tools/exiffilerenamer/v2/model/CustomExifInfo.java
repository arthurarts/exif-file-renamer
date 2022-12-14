package it.codedvalue.photo.tools.exiffilerenamer.v2.model;

import com.drew.metadata.Metadata;
import com.drew.metadata.file.FileSystemDirectory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Arthur Arts
 */

@RequiredArgsConstructor
public class CustomExifInfo {

    // TODO implement define custom format.
    @Value("${image.date.prefix.format}")
    private String dateFormat;

    private SimpleDateFormat sm = new SimpleDateFormat("yyyyMMdd");
    @Setter
    private final Metadata metadata;

    public String getCreationDateYYYYMMDD() {
        if (Objects.nonNull(this.metadata)) {

            FileSystemDirectory firstDirectoryOfType = metadata.getFirstDirectoryOfType(FileSystemDirectory.class);
            Date creationDate = firstDirectoryOfType.getDate(FileSystemDirectory.TAG_FILE_MODIFIED_DATE);
            return sm.format(creationDate);
        }
        return "";
    }
}
