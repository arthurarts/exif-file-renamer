package it.codedvalue.photo.tools.exiffilerenamer.v2.controller;

import it.codedvalue.photo.tools.exiffilerenamer.v2.model.RenameTotalResult;
import it.codedvalue.photo.tools.exiffilerenamer.v2.service.FileRenameService;
import java.nio.file.Paths;
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
    private final FileRenameService fileRenameService;

//    @GetMapping("test-single-file")
//    public RenameSingleResultImage renameSingleFile(@RequestParam String fileName) {
//        return fileRenameService.renameAllImagesInPath(Paths.get(fileName).normalize().toString());
//    }

    @GetMapping("rename-all-in-directory")
    public RenameTotalResult renameAllFilesInDirectory(@RequestParam String directory) {

        RenameTotalResult renameTotalResult = null;

        String normalizedDirName = Paths.get(directory).normalize().toString();

        if (normalizedDirName.startsWith(safeImageDirectory)) {
            renameTotalResult = fileRenameService.renameAllImagesInPath(normalizedDirName);
            log.info("Finished renaming {} files", renameTotalResult.getTotalRenamed());
        }
        return renameTotalResult;
    }


}