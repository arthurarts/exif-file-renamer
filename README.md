# exif-file-renamer


WARNING: use at own risk. Test first with a test directory before letting it loose on complete directory structure.

renames images and video files to prefix it with yyyyDDmm_ pattern so the alphabetic order is identical to the chronological order.


1. start up application
2. open browser

 - 3a to rename single image: 
    http://localhost:8080/rename-single?fileName=/Users/Foo/1.JPG
 -  3b to rename all files in directory and underlying directories:
    http://localhost:8080/rename-single?fileName=/Users/Foo/
   
