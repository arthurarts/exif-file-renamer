package it.codedvalue.photo.tools.exiffilerenamer.v2.service;

import com.drew.metadata.Metadata;
import it.codedvalue.photo.tools.exiffilerenamer.v2.model.CustomExifInfo;
import it.codedvalue.photo.tools.exiffilerenamer.v2.model.ImageDataRenameResult;
import it.codedvalue.photo.tools.exiffilerenamer.v2.repository.ExifReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Arthur Arts
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImageDataRenamer {

    final ExifReader reader;

    public ImageDataRenameResult rename(Path path) {
        ImageDataRenameResult renameSingleResult = new ImageDataRenameResult();
        if (!filenameAlreadyStartsWithYear(path)) {
            log.debug("try to rename file {}", path.getFileName());
            CustomExifInfo customExifInfo = getCustomExifInfo(path.toFile());
            if (Objects.nonNull(customExifInfo)) {
                String newFileName = customExifInfo.getCreationDateYYYYMMDD() + "-" + path.getFileName();
                try {
                    Files.move(path, path.resolveSibling(newFileName));
                    renameSingleResult.setRenameSuccessLog(String.format("renamed %s to %s", path.getFileName().toString(), newFileName));
                    renameSingleResult.setOldVSNewImageName(Collections.singletonMap(path.getFileName().toString(), newFileName));
                    log.info("renamed {} to {}", path.getFileName().toString(), newFileName);
                } catch (Exception e) {
                    renameSingleResult.setRenameFailLog(String.format("could not rename image \n %s", e));
                }
            } else {
                renameSingleResult.setRenameFailLog(String.format("Could not rename image %s because retrieved exif info was null", path.getFileName()));
            }
        }
        return renameSingleResult;
    }

    private boolean filenameAlreadyStartsWithYear(Path path) {
        String s = path.getFileName().toString();
        log.debug("Yearcheck for {} : year is {}?", s, s.substring(0, 4));
        try {
            if ((Integer.valueOf(s.substring(0, 4)).compareTo(2002) >= 0) && (Integer.valueOf(s.substring(0, 4)).compareTo(2023) < 0)) {
                return true;
            }
        } catch (NumberFormatException nf) {
            // no spang
        }
        return false;
    }

    /**
     * @param file
     * @return returns @{@link CustomExifInfo}
     */
    public CustomExifInfo getCustomExifInfo(final File file) {
        Metadata metadata = reader.readData(file);
        return new CustomExifInfo(metadata);
    }

}
