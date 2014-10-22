import com.spartango.air.imagery.BingImageSource;
import org.bytedeco.javacpp.opencv_imgproc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

import static org.bytedeco.javacpp.opencv_core.IplImage;

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
    public void testBlur() throws Exception {
        BingImageSource source = new BingImageSource();

        for (double i = 32.154985; i < 32.154985 + .02; i += .001) {
            for (double j = -110.861441; j < -110.861441 + .02; j += .001) {
                final Optional<BufferedImage> imageResult = source.getImageAround(i, j, 16, 500, 500);
                org.junit.Assert.assertTrue(imageResult.isPresent());

                imageResult.ifPresent(image -> {
                    imageView.setIcon(new ImageIcon(image));
                    IplImage target = IplImage.createFrom(image);
                    IplImage result = IplImage.createCompatible(target);
                    opencv_imgproc.cvSmooth(target, result);
                    BufferedImage processed = result.getBufferedImage();
                    imageView.setIcon(new ImageIcon(processed));
                });

                Thread.sleep(16);
            }
        }

    }

    @After
    public void tearDown() throws Exception {
        frame.setVisible(false);
    }
}
