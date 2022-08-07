package it.codedvalue.photo.tools.exiffilerenamer.service;


import it.codedvalue.photo.tools.exiffilerenamer.model.CustomExifInfo;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.springframework.stereotype.Service;

/**
 * @author Arthur Arts
 * https://github.com/apache/commons-imaging/blob/master/src/test/java/org/apache/commons/imaging/examples/MetadataExample.java
 */

@Service
@Slf4j
public class ExifService {

    public CustomExifInfo getCustomExifInfo(final File file) {
        final ImageMetadata metadata;
        try {
            metadata = Imaging.getMetadata(file);

            if (metadata instanceof JpegImageMetadata) {
                final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
                TiffField creationDateTime = jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
                return new CustomExifInfo(creationDateTime.getStringValue());
            }
        } catch (Exception e) {
//            log.warn("couldn't read exif data for {}", file.getName());
        }
        return null;
    }


}
