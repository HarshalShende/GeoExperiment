import com.spartango.air.imagery.BingImageSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

import static org.bytedeco.javacpp.opencv_core.IplImage;
import static org.bytedeco.javacpp.opencv_imgproc.*;

/**
 * Author: spartango
 * Date: 10/22/14
 * Time: 13:37.
 */
public class JavaCVTest {
    private JFrame frame;
    private JLabel imageView;

    @Before
    public void setUp() throws Exception {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setSize(500, 500);
        frame.setVisible(true);

        imageView = new JLabel();
        imageView.setSize(500, 500);
        frame.add(imageView);
    }

    @Test
    public void testFilter() throws Exception {
        BingImageSource source = new BingImageSource();
        final Optional<BufferedImage> imageResult = source.getImageAround(32.164985, -110.861441, 18, 500, 500);
        org.junit.Assert.assertTrue(imageResult.isPresent());

        imageResult.ifPresent(image -> {
            IplImage target = IplImage.createFrom(image);
            IplImage gray = IplImage.create(target.cvSize(), target.depth(), 1);
            cvCvtColor(target, gray, COLOR_BGR2GRAY);

            IplImage result = IplImage.create(target.cvSize(), target.depth(), 1);
            cvSobel(gray, result, 1, 1, 5);

            BufferedImage processed = result.getBufferedImage();
            imageView.setIcon(new ImageIcon(processed));
        });

        Thread.sleep(10000);

    }


    @After
    public void tearDown() throws Exception {
        frame.setVisible(false);
    }
}
