package it.codedvalue.photo.tools.exiffilerenamer.v2.service;

import it.codedvalue.photo.tools.exiffilerenamer.v2.model.RenameSingleResultImage;
import it.codedvalue.photo.tools.exiffilerenamer.v2.model.RenameSingleResultSpecific;
import it.codedvalue.photo.tools.exiffilerenamer.v2.model.RenameTotalResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * @author Arthur Arts
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class FileRenameService {

    @Value("${prefixes.ignorable}")
    private List<String> prefixesOfFilesToBeIgnored;

    @Value("${prefixes.specific.handling}")
    private List<String> prefixesOfFilesToBeRenamedSpecific;

    final ImageDataRenamer imageDataRenamer;
    final SpecificRenamer specificRenamer;

    public RenameTotalResult renameAllImagesInPath(String readDirectory) {
        RenameTotalResult renameTotalResult;
        List<RenameSingleResultImage> renameSingleResultImages = new ArrayList<>();
        List<RenameSingleResultSpecific> renameSingleResultSpecifics = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(readDirectory))) {
            paths.filter(this::isNotIgnorableFile)
                    .filter(Files::isRegularFile)
                    .filter(path -> !Files.isDirectory(path))
                    .filter(this::notHidden)
                    .forEach(path -> {
                        RenameSingleResultImage renameSingleResultImage;
                        RenameSingleResultSpecific renameSingleResultSpecific;
                        if (!isSpecificFileNameToBeRenamed(path.getFileName().toString())) {
                            renameSingleResultImage = imageDataRenamer.rename(path);
                            if (Objects.nonNull(renameSingleResultImage.getRenameLogImagesFail()) || Objects.nonNull(renameSingleResultImage.getRenameLogImagesSuccess())) {
                                renameSingleResultImages.add(renameSingleResultImage);
                            }
                        } else {
                            renameSingleResultSpecific = specificRenamer.renameSpecific(path);
                            if (Objects.nonNull(renameSingleResultSpecific.getLogSpecificFail()) || Objects.nonNull(renameSingleResultSpecific.getLogSpecificSuccess())) {
                                renameSingleResultSpecifics.add(renameSingleResultSpecific);
                            }
                        }
                    });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return new RenameTotalResult(renameSingleResultImages, renameSingleResultSpecifics);
    }


    private boolean isNotIgnorableFile(Path path) {
        return prefixesOfFilesToBeIgnored.stream().noneMatch(s -> path.getFileName().toString().toLowerCase().endsWith(s.toLowerCase()));
    }

    private boolean isSpecificFileNameToBeRenamed(String fileName) {
        return prefixesOfFilesToBeRenamedSpecific.stream().anyMatch(fileName::startsWith);
    }

    private boolean notHidden(Path path) {
        try {
            boolean notHidden = !Files.isHidden(path);
            return notHidden;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
