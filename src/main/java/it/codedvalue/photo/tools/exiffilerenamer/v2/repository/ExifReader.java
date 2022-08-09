package it.codedvalue.photo.tools.exiffilerenamer.v2.repository;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.iptc.IptcReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.springframework.stereotype.Repository;

/**
 * @author Arthur Arts
 */
@Repository
public class ExifReader {

    public Metadata readData(File file) {
        Metadata metadata = null;
        try {
            metadata = ImageMetadataReader.readMetadata(file);
            printValues(metadata);
        } catch (ImageProcessingException | IOException e) {
            throw new RuntimeException(e);
        }

        return metadata;
    }

    private static void printValues(Metadata metadata) {
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                System.out.format("[%s] - %s = %s\n",
                        directory.getName(), tag.getTagName(), tag.getDescription());
            }
            if (directory.hasErrors()) {
                for (String error : directory.getErrors()) {
                    System.err.format("ERROR: %s", error);
                }
            }
        }
    }

    private static Metadata getSpecificMetadata(File file) {
        //
        // APPROACH 3: SPECIFIC METADATA TYPE
        //
        // If you only wish to read a subset of the supported metadata types, you can do this by
        // passing the set of readers to use.
        //
        // This currently only applies to JPEG file processing.
        //
        Metadata metadata = null;
        try {
            // We are only interested in handling
            Iterable<JpegSegmentMetadataReader> readers = Arrays.asList(new com.drew.metadata.exif.ExifReader(), new IptcReader());

            metadata = JpegMetadataReader.readMetadata(file, readers);

            print(metadata, "Using JpegMetadataReader for Exif and IPTC only");

        } catch (JpegProcessingException e) {
            print(e);
        } catch (IOException e) {
            print(e);
        }


        return metadata;
    }

    private static Metadata getMetadataJpeg(File file) {
        //
        // SCENARIO 2: SPECIFIC FILE TYPE
        //
        // If you know the file to be a JPEG, you may invoke the JpegMetadataReader, rather than the generic reader
        // used in approach 1.  Similarly, if you knew the file to be a TIFF/RAW image you might use TiffMetadataReader,
        // PngMetadataReader for PNG files, BmpMetadataReader for BMP files, or GifMetadataReader for GIF files.
        //
        // Using the specific reader offers a very, very slight performance improvement.
        //
        Metadata metadata = null;
        try {
            metadata = JpegMetadataReader.readMetadata(file);

            print(metadata, "Using JpegMetadataReader");
        } catch (JpegProcessingException e) {
            print(e);
        } catch (IOException e) {
            print(e);
        }
        return metadata;
    }

    private static Metadata getMetadataAnyFileType(File file) {
        //
        // SCENARIO 1: UNKNOWN FILE TYPE
        //
        // This is the most generic approach.  It will transparently determine the file type and invoke the appropriate
        // readers.  In most cases, this is the most appropriate usage.  This will handle JPEG, TIFF, GIF, BMP and RAW
        // (CRW/CR2/NEF/RW2/ORF) files and extract whatever metadata is available and understood.
        //
        Metadata metadata = null;
        try {
            metadata = ImageMetadataReader.readMetadata(file);

            print(metadata, "Using ImageMetadataReader");
        } catch (ImageProcessingException e) {
            print(e);
        } catch (IOException e) {
            print(e);
        }
        return metadata;
    }

    /**
     * Write all extracted values to stdout.
     */
    private static void print(Metadata metadata, String method) {
        System.out.println();
        System.out.println("-------------------------------------------------");
        System.out.print(' ');
        System.out.print(method);
        System.out.println("-------------------------------------------------");
        System.out.println();

        //
        // A Metadata object contains multiple Directory objects
        //
        for (Directory directory : metadata.getDirectories()) {

            //
            // Each Directory stores values in Tag objects
            //
            for (Tag tag : directory.getTags()) {
                System.out.println(tag);
            }

            //
            // Each Directory may also contain error messages
            //
            for (String error : directory.getErrors()) {
                System.err.println("ERROR: " + error);
            }
        }
    }

    private static void print(Exception exception) {
        System.err.println("EXCEPTION: " + exception);
    }


}
