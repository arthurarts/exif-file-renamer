package it.codedvalue.photo.tools.exiffilerenamer.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

/**
 * @author Arthur Arts
 */
@Component
public class MightComeHandyLaterMaybe {

    public void printGpsInfo(JpegImageMetadata jpegMetadata, TiffImageMetadata exifMetadata) throws ImageReadException {
        if (null != exifMetadata) {
            final TiffImageMetadata.GPSInfo gpsInfo = exifMetadata.getGPS();
            if (null != gpsInfo) {
                final String gpsDescription = gpsInfo.toString();
                final double longitude = gpsInfo.getLongitudeAsDegreesEast();
                final double latitude = gpsInfo.getLatitudeAsDegreesNorth();

                System.out.println("    " + "GPS Description: "
                        + gpsDescription);
                System.out.println("    "
                        + "GPS Longitude (Degrees East): " + longitude);
                System.out.println("    "
                        + "GPS Latitude (Degrees North): " + latitude);
            }
        }

        // more specific example of how to manually access GPS values
        final TiffField gpsLatitudeRefField = jpegMetadata.findEXIFValueWithExactMatch(
                GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF);
        final TiffField gpsLatitudeField = jpegMetadata.findEXIFValueWithExactMatch(
                GpsTagConstants.GPS_TAG_GPS_LATITUDE);
        final TiffField gpsLongitudeRefField = jpegMetadata.findEXIFValueWithExactMatch(
                GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF);
        final TiffField gpsLongitudeField = jpegMetadata.findEXIFValueWithExactMatch(
                GpsTagConstants.GPS_TAG_GPS_LONGITUDE);
        if (gpsLatitudeRefField != null && gpsLatitudeField != null &&
                gpsLongitudeRefField != null &&
                gpsLongitudeField != null) {
            // all of these values are strings.
            final String gpsLatitudeRef = (String) gpsLatitudeRefField.getValue();
            final RationalNumber[] gpsLatitude = (RationalNumber[]) (gpsLatitudeField.getValue());
            final String gpsLongitudeRef = (String) gpsLongitudeRefField.getValue();
            final RationalNumber[] gpsLongitude = (RationalNumber[]) gpsLongitudeField.getValue();

            final RationalNumber gpsLatitudeDegrees = gpsLatitude[0];
            final RationalNumber gpsLatitudeMinutes = gpsLatitude[1];
            final RationalNumber gpsLatitudeSeconds = gpsLatitude[2];

            final RationalNumber gpsLongitudeDegrees = gpsLongitude[0];
            final RationalNumber gpsLongitudeMinutes = gpsLongitude[1];
            final RationalNumber gpsLongitudeSeconds = gpsLongitude[2];

            // This will format the gps info like so:
            //
            // gpsLatitude: 8 degrees, 40 minutes, 42.2 seconds S
            // gpsLongitude: 115 degrees, 26 minutes, 21.8 seconds E

            System.out.println("    " + "GPS Latitude: "
                    + gpsLatitudeDegrees.toDisplayString() + " degrees, "
                    + gpsLatitudeMinutes.toDisplayString() + " minutes, "
                    + gpsLatitudeSeconds.toDisplayString() + " seconds "
                    + gpsLatitudeRef);
            System.out.println("    " + "GPS Longitude: "
                    + gpsLongitudeDegrees.toDisplayString() + " degrees, "
                    + gpsLongitudeMinutes.toDisplayString() + " minutes, "
                    + gpsLongitudeSeconds.toDisplayString() + " seconds "
                    + gpsLongitudeRef);

        }

    }

    public void printExifValues(JpegImageMetadata jpegMetadata) {
        // Jpeg EXIF metadata is stored in a TIFF-based directory structure
        // and is identified with TIFF tags.
        // Here we look for the "x resolution" tag, but
        // we could just as easily search for any other tag.
        //
        // see the TiffConstants file for a list of TIFF tags.

        // print out various interesting EXIF tags.
        printTagValue(jpegMetadata, TiffTagConstants.TIFF_TAG_XRESOLUTION);
        printTagValue(jpegMetadata, TiffTagConstants.TIFF_TAG_DATE_TIME);
        printTagValue(jpegMetadata,
                ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
        printTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_DATE_TIME_DIGITIZED);
        printTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_ISO);
        printTagValue(jpegMetadata,
                ExifTagConstants.EXIF_TAG_SHUTTER_SPEED_VALUE);
        printTagValue(jpegMetadata,
                ExifTagConstants.EXIF_TAG_APERTURE_VALUE);
        printTagValue(jpegMetadata,
                ExifTagConstants.EXIF_TAG_BRIGHTNESS_VALUE);
        printTagValue(jpegMetadata,
                GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF);
        printTagValue(jpegMetadata, GpsTagConstants.GPS_TAG_GPS_LATITUDE);
        printTagValue(jpegMetadata,
                GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF);
        printTagValue(jpegMetadata, GpsTagConstants.GPS_TAG_GPS_LONGITUDE);

    }

    ;


    public void printTagValue(final JpegImageMetadata jpegMetadata,
                              final TagInfo tagInfo) {
        final TiffField field = jpegMetadata.findEXIFValueWithExactMatch(tagInfo);
        if (field == null) {
            System.out.println(tagInfo.name + ": " + "Not Found.");
        } else {
            System.out.println(tagInfo.name + ": "
                    + field.getValueDescription());
        }
    }


    public void removeExifMetadata(final File jpegImageFile, final File dst)
            throws IOException, ImageReadException, ImageWriteException {
        try (FileOutputStream fos = new FileOutputStream(dst);
             OutputStream os = new BufferedOutputStream(fos)) {
            new ExifRewriter().removeExifMetadata(jpegImageFile, os);
        }
    }

    /**
     * This example illustrates how to add/update EXIF metadata in a JPEG file.
     *
     * @param jpegImageFile A source image file.
     * @param dst           The output file.
     * @throws IOException
     * @throws ImageReadException
     * @throws ImageWriteException
     */

    public void changeExifMetadata(final File jpegImageFile, final File dst)
            throws IOException, ImageReadException, ImageWriteException {

        try (FileOutputStream fos = new FileOutputStream(dst);
             OutputStream os = new BufferedOutputStream(fos)) {

            TiffOutputSet outputSet = null;

            // note that metadata might be null if no metadata is found.
            final ImageMetadata metadata = Imaging.getMetadata(jpegImageFile);
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            if (null != jpegMetadata) {
                // note that exif might be null if no Exif metadata is found.
                final TiffImageMetadata exif = jpegMetadata.getExif();

                if (null != exif) {
                    // TiffImageMetadata class is immutable (read-only).
                    // TiffOutputSet class represents the Exif data to write.
                    //
                    // Usually, we want to update existing Exif metadata by
                    // changing
                    // the values of a few fields, or adding a field.
                    // In these cases, it is easiest to use getOutputSet() to
                    // start with a "copy" of the fields read from the image.
                    outputSet = exif.getOutputSet();
                }
            }

            // if file does not contain any exif metadata, we create an empty
            // set of exif metadata. Otherwise, we keep all of the other
            // existing tags.
            if (null == outputSet) {
                outputSet = new TiffOutputSet();
            }

            {
                // Example of how to add a field/tag to the output set.
                //
                // Note that you should first remove the field/tag if it already
                // exists in this directory, or you may end up with duplicate
                // tags. See above.
                //
                // Certain fields/tags are expected in certain Exif directories;
                // Others can occur in more than one directory (and often have a
                // different meaning in different directories).
                //
                // TagInfo constants often contain a description of what
                // directories are associated with a given tag.
                //
                final TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
                // make sure to remove old value if present (this method will
                // not fail if the tag does not exist).
                exifDirectory.removeField(ExifTagConstants.EXIF_TAG_APERTURE_VALUE);
                exifDirectory.add(ExifTagConstants.EXIF_TAG_APERTURE_VALUE,
                        new RationalNumber(3, 10));
            }

            {
                // Example of how to add/update GPS info to output set.

                // New York City
                final double longitude = -74.0; // 74 degrees W (in Degrees East)
                final double latitude = 40 + 43 / 60.0; // 40 degrees N (in Degrees
                // North)

                outputSet.setGPSInDegrees(longitude, latitude);
            }

            // printTagValue(jpegMetadata, TiffConstants.TIFF_TAG_DATE_TIME);

            new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os,
                    outputSet);
        }
    }


    /**
     * This example illustrates how to remove a tag (if present) from EXIF metadata in a JPEG file.
     * <p>
     * In this case, we remove the "aperture" tag from the EXIF metadata if present.
     *
     * @param jpegImageFile A source image file.
     * @param dst           The output file.
     * @throws IOException
     * @throws ImageReadException
     * @throws ImageWriteException
     */

    public void removeExifTag(final File jpegImageFile, final File dst) throws IOException,
            ImageReadException, ImageWriteException {
        try (FileOutputStream fos = new FileOutputStream(dst);
             OutputStream os = new BufferedOutputStream(fos)) {
            TiffOutputSet outputSet = null;

            // note that metadata might be null if no metadata is found.
            final ImageMetadata metadata = Imaging.getMetadata(jpegImageFile);
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            if (null != jpegMetadata) {
                // note that exif might be null if no Exif metadata is found.
                final TiffImageMetadata exif = jpegMetadata.getExif();

                if (null != exif) {
                    // TiffImageMetadata class is immutable (read-only).
                    // TiffOutputSet class represents the Exif data to write.
                    //
                    // Usually, we want to update existing Exif metadata by
                    // changing
                    // the values of a few fields, or adding a field.
                    // In these cases, it is easiest to use getOutputSet() to
                    // start with a "copy" of the fields read from the image.
                    outputSet = exif.getOutputSet();
                }
            }

            if (null == outputSet) {
                // file does not contain any exif metadata. We don't need to
                // update the file; just copy it.
                FileUtils.copyFile(jpegImageFile, dst);
                return;
            }

            {
                // Example of how to remove a single tag/field.
                // There are two ways to do this.

                // Option 1: brute force
                // Note that this approach is crude: Exif data is organized in
                // directories. The same tag/field may appear in more than one
                // directory, and have different meanings in each.
                outputSet.removeField(ExifTagConstants.EXIF_TAG_APERTURE_VALUE);

                // Option 2: precision
                // We know the exact directory the tag should appear in, in this
                // case the "exif" directory.
                // One complicating factor is that in some cases, manufacturers
                // will place the same tag in different directories.
                // To learn which directory a tag appears in, either refer to
                // the constants in ExifTagConstants.java or go to Phil Harvey's
                // EXIF website.
                final TiffOutputDirectory exifDirectory = outputSet.getExifDirectory();
                if (null != exifDirectory) {
                    exifDirectory.removeField(ExifTagConstants.EXIF_TAG_APERTURE_VALUE);
                }
            }

            new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os,
                    outputSet);
        }
    }

    /**
     * This example illustrates how to set the GPS values in JPEG EXIF metadata.
     *
     * @param jpegImageFile A source image file.
     * @param dst           The output file.
     * @throws IOException
     * @throws ImageReadException
     * @throws ImageWriteException
     */

    public void setExifGPSTag(final File jpegImageFile, final File dst) throws IOException,
            ImageReadException, ImageWriteException {
        try (FileOutputStream fos = new FileOutputStream(dst);
             OutputStream os = new BufferedOutputStream(fos)) {
            TiffOutputSet outputSet = null;

            // note that metadata might be null if no metadata is found.
            final ImageMetadata metadata = Imaging.getMetadata(jpegImageFile);
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            if (null != jpegMetadata) {
                // note that exif might be null if no Exif metadata is found.
                final TiffImageMetadata exif = jpegMetadata.getExif();

                if (null != exif) {
                    // TiffImageMetadata class is immutable (read-only).
                    // TiffOutputSet class represents the Exif data to write.
                    //
                    // Usually, we want to update existing Exif metadata by
                    // changing
                    // the values of a few fields, or adding a field.
                    // In these cases, it is easiest to use getOutputSet() to
                    // start with a "copy" of the fields read from the image.
                    outputSet = exif.getOutputSet();
                }
            }

            // if file does not contain any exif metadata, we create an empty
            // set of exif metadata. Otherwise, we keep all of the other
            // existing tags.
            if (null == outputSet) {
                outputSet = new TiffOutputSet();
            }

            {
                // Example of how to add/update GPS info to output set.

                // New York City
                final double longitude = -74.0; // 74 degrees W (in Degrees East)
                final double latitude = 40 + 43 / 60.0; // 40 degrees N (in Degrees
                // North)

                outputSet.setGPSInDegrees(longitude, latitude);
            }

            new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os,
                    outputSet);
        }
    }

}
