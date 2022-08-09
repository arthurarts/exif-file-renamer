package it.codedvalue.photo.tools.exiffilerenamer;

import it.codedvalue.photo.tools.exiffilerenamer.v2.controller.ExifController;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
//@ActiveProfiles(profiles = "test")
class ExifFileRenamerApplicationTests {

    @Value("${safe.base.path}")
    private String safeImageDirectory;
    @Autowired
    private ExifController controller;

    @Test
    public void testPathTraversal() throws IOException {

       controller.renameAllFilesInDirectory("/Users/Admin/Desktop/photo/2021-09-25/../..");


    }


}
