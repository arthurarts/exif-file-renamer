package it.codedvalue.photo.tools.exiffilerenamer.controller;

import it.codedvalue.photo.tools.exiffilerenamer.model.RenameSingleResultImage;
import it.codedvalue.photo.tools.exiffilerenamer.model.RenameSingleResultSpecific;
import it.codedvalue.photo.tools.exiffilerenamer.model.RenameTotalResult;
import it.codedvalue.photo.tools.exiffilerenamer.service.FileRenamer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Arthur Arts
 * Made with apache commons imaging.
 * Also try https://github.com/drewnoakes/metadata-extractor
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class ExifController {

    @Value("${safe.base.path}")
    private String safeImageDirectory;
    private final FileRenamer fileRenamer;


    @GetMapping("rename-all-in-directory")
    public RenameTotalResult renameAllFilesInDirectory(@RequestParam String directory) {

        RenameTotalResult renameTotalResult = null;

        String normalizedDirName = Paths.get(directory).normalize().toString();

        if (normalizedDirName.startsWith(safeImageDirectory)) {
            String readDirectory = normalizedDirName;

            List<RenameSingleResultImage> renameSingleResultImages = new ArrayList<>();
            List<RenameSingleResultSpecific> renameSingleResultSpecifics = new ArrayList<>();
            try (Stream<Path> paths = Files.walk(Paths.get(readDirectory))) {
                paths.filter(Files::isRegularFile).filter(path -> !Files.isDirectory(path)).filter(path -> {
                    try {
                        return !Files.isHidden(path);
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                }).forEach(path -> {

                    RenameSingleResultImage renameSingleResultImage = fileRenamer.renameImage(path);
                    RenameSingleResultSpecific renameSingleResultSpecific = fileRenamer.renameSpecific(path);
                    if (Objects.nonNull(renameSingleResultSpecific.getLogSpecificFail()) || Objects.nonNull(renameSingleResultSpecific.getLogSpecificSuccess())) {
                        renameSingleResultSpecifics.add(renameSingleResultSpecific);
                    }
                    if (Objects.nonNull(renameSingleResultImage.getRenameLogImagesFail()) || Objects.nonNull(renameSingleResultImage.getRenameLogImagesSuccess())) {
                        renameSingleResultImages.add(renameSingleResultImage);
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            List<Map<String, String>> totalOldvsNew = renameSingleResultImages.stream().map(RenameSingleResultImage::getOldVSNewImageName).filter(Objects::nonNull).collect(Collectors.toList());
            totalOldvsNew.addAll(renameSingleResultSpecifics.stream().map(RenameSingleResultSpecific::getOldVSNewVidName).filter(Objects::nonNull).collect(Collectors.toList()));


            renameTotalResult = RenameTotalResult.builder().imageRenamingResults(renameSingleResultImages).specificRenamingResults(renameSingleResultSpecifics).totalOldvsNew(totalOldvsNew).build();

            log.info("Finished renaming {} files", renameTotalResult.getTotalRenamed());
        }
        return renameTotalResult;
    }


}