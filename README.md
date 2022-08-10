# exif-file-renamer


WARNING: use at own risk. Test first with a test directory before letting it loose on complete directory structure.

Renames images and video files to prefix it with yyyyDDmm_ pattern so the alphabetic order is identical to the chronological order.
Works on most video and image types, including raw files.
Notable exception: m2ts and mts files.

1. define safe base dir in application.properties
2. start up application
3. open browser
 - a to rename single image: 
    http://localhost:8080/rename-single?fileName=/Users/Foo/1.JPG
 - b to rename all files in directory and underlying directories:
    http://localhost:8080/rename-single?fileName=/Users/Foo/
   

## To be implemented:
- custom date format
- write conversions to logfile
- run only on specific filetypes
