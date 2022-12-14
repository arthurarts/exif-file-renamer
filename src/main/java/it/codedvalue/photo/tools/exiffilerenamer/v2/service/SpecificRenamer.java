package it.codedvalue.photo.tools.exiffilerenamer.v2.service;

import it.codedvalue.photo.tools.exiffilerenamer.v2.model.SpecificHandlingRenameResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Arthur Arts
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpecificRenamer {

    @Value("${prefixes.tobecut}")
    private List<String> prefixesToBeCut = Arrays.asList("VID_", "VID-", "IMG_", "IMG-");

    public SpecificHandlingRenameResult rename(Path path) {
        SpecificHandlingRenameResult specificHandlingRenameResult = new SpecificHandlingRenameResult();
        String fileName = path.getFileName().toString();

        log.debug("try to rename file {}", path.getFileName());
        prefixesToBeCut.forEach(prefix -> {
            if (fileName.startsWith(prefix)) {
                String newName = fileName.replace(prefix, "");
                try {
                    Files.move(path, path.resolveSibling(newName));
                    specificHandlingRenameResult.setLogSpecificSuccess(String.format("renamed %s to %s", fileName, newName));
                    log.info("renamed {} to {}", fileName, newName);
                    specificHandlingRenameResult.setOldVSNewVidName(Collections.singletonMap(path.getFileName().toString(), newName));
                } catch (IOException e) {
                    specificHandlingRenameResult.setLogSpecificFail(String.format("could not rename %s \n %s", fileName, e));
                }
            }
        });

        return specificHandlingRenameResult;
    }


}
