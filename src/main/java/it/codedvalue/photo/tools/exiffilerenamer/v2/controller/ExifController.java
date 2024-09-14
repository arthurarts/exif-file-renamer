package it.codedvalue.photo.tools.exiffilerenamer.v2.controller;

import it.codedvalue.photo.tools.exiffilerenamer.v2.model.ImageDataRenameResult;
import it.codedvalue.photo.tools.exiffilerenamer.v2.model.RenameTotalResult;
import it.codedvalue.photo.tools.exiffilerenamer.v2.service.FileRenameService;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Arthur Arts
 * Made with apache commons imaging.
 * Also try https://github.com/drewnoakes/metadata-extractor
 */
@Slf4j
@RequiredArgsConstructor
@Controller
public class ExifController {

    @Value("#{'${safe.base.path}'.split(',')}")
    private List<String> safeImageDirectories;
    private final FileRenameService fileRenameService;

    @GetMapping
    public String listRenameOptions(Model model) {
        return "index";
    }

    @GetMapping("rename-single")
    public ImageDataRenameResult renameSingleFile(@RequestParam String fileName) {
        return fileRenameService.renameSingleResultImage(Paths.get(fileName).normalize());
    }

    @PostMapping("rename-list")
    public String renameSingleFile(@RequestParam("files") MultipartFile[] files) {
        Arrays.stream(files).forEach(multipartFile -> log.info(multipartFile.getOriginalFilename()));
        return "results";

    }

    @GetMapping("rename-all")
    public String renameAllFilesInDirectory(@RequestParam String directory, Model model) {

        RenameTotalResult renameTotalResult = null;

        String normalizedDirName = Paths.get(directory).normalize().toString();
        boolean isSafeDirectory = safeImageDirectories.stream().anyMatch(normalizedDirName::startsWith);

        if (isSafeDirectory) {
            renameTotalResult = fileRenameService.renameAllImagesInPath(normalizedDirName);
            log.info("Finished renaming {} files", renameTotalResult.getTotalRenamed());
        } else {
            log.info("Directory is not a safe directory. No work will be done.");
        }

        model.addAttribute("results", renameTotalResult);
        return "results";
    }


}