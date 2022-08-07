package it.codedvalue.photo.tools.exiffilerenamer;

import it.codedvalue.photo.tools.exiffilerenamer.controller.ExifController;
import it.codedvalue.photo.tools.exiffilerenamer.service.FileRenamer;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;


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
