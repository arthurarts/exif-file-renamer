package it.codedvalue.photo.tools.exiffilerenamer.v2.service;

import com.drew.metadata.Metadata;
import it.codedvalue.photo.tools.exiffilerenamer.v2.model.CustomExifInfo;
import it.codedvalue.photo.tools.exiffilerenamer.v2.repository.ExifReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author Arthur Arts
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class FileRenameService {

    private static List<String> prefixesOfFilesToBeRenamedSpecific = Arrays.asList("VID_", "VID-", "IMG_201", "IMG-201", "IMG_202", "IMG-202");
    private static List<String> prefixesToBeCut = Arrays.asList("VID_", "VID-", "IMG_", "IMG-");

    final ExifReader reader;

    /**
     * @param file
     * @return returns @{@link CustomExifInfo}
     */
    public CustomExifInfo getCustomExifInfo(final File file) {
        Metadata metadata = reader.readData(file);
        return new CustomExifInfo(metadata);
    }

    public boolean filenameAlreadyStartsWithYear(Path path) {
        String s = path.getFileName().toString();
        log.debug("Yearcheck for {} : year is {}?", s, s.substring(0, 4));
        try {
            if ((Integer.valueOf(s.substring(0, 4)).compareTo(2002) >= 0) && (Integer.valueOf(s.substring(0, 4)).compareTo(2023) == -1)) {
                return true;
            }
        } catch (NumberFormatException nf) {
            // no spang
        }
        return false;
    }

    public RenameSingleResultImage renameImage(Path path) {
        RenameSingleResultImage renameSingleResult = new RenameSingleResultImage();
        if (!filenameAlreadyStartsWithYear(path) && !isSpecificFileNameToBeRenamed(path.getFileName().toString())) {
            log.debug("try to rename file {}", path.getFileName());
            CustomExifInfo customExifInfo = getCustomExifInfo(path.toFile());
            if (Objects.nonNull(customExifInfo)) {
                String newFileName = customExifInfo.getCreationDateYYYYMMDD() + "-" + path.getFileName();
                try {
                    Files.move(path, path.resolveSibling(newFileName));
                    renameSingleResult.setRenameLogImagesSuccess(String.format("renamed %s to %s", path.getFileName().toString(), newFileName));
                    renameSingleResult.setOldVSNewImageName(Collections.singletonMap(path.getFileName().toString(), newFileName));
                    log.info("renamed {} to {}", path.getFileName().toString(), newFileName);
                } catch (IOException e) {
                    renameSingleResult.setRenameLogImagesFail(String.format("could not rename image \n %s", e));
                }
            } else {
                renameSingleResult.setRenameLogImagesFail(String.format("Could not rename image %s because retrieved exif info was null", path.getFileName()));
            }
        } else {
//            renameSingleResult.setRenameLogImagesFail(String.format("Did not rename image %s because image %s already starts with year",
//                    path.getFileName()));
        }

        return renameSingleResult;
    }

    public RenameSingleResultSpecific renameSpecific(Path path) {
        RenameSingleResultSpecific renameSingleResultSpecific = new RenameSingleResultSpecific();
        String fileName = path.getFileName().toString();

        if (isSpecificFileNameToBeRenamed(fileName)) {
            log.debug("try to rename file {}", path.getFileName());
            prefixesToBeCut.forEach(prefix -> {
                if (fileName.startsWith(prefix)) {
                    String newName = fileName.replace(prefix, "");
                    try {
                        Files.move(path, path.resolveSibling(newName));
                        renameSingleResultSpecific.setLogSpecificSuccess(String.format("renamed %s to %s", fileName, newName));
                        log.info("renamed {} to {}", fileName, newName);
                        renameSingleResultSpecific.setOldVSNewVidName(Collections.singletonMap(path.getFileName().toString(), newName));
                    } catch (IOException e) {
                        renameSingleResultSpecific.setLogSpecificFail(String.format("could not rename %s \n %s", fileName, e));
                    }
                }
            });
        }
        return renameSingleResultSpecific;
    }

    private boolean isSpecificFileNameToBeRenamed(String fileName) {
     return prefixesOfFilesToBeRenamedSpecific.stream().anyMatch(fileName::startsWith);
    }
}
