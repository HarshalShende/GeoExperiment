import com.spartango.air.imagery.BingImageSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

/**
 * Author: spartango
 * Date: 10/22/14
 * Time: 00:33.
 */
public class BingImageTest {

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
    public void testFetchAndDisplay() throws Exception {
        BingImageSource source = new BingImageSource();

        for (double i = 32.154985; i < 32.154985 + .02; i += .001) {
            for (double j = -110.861441; j < -110.861441 + .02; j += .001) {
                final Optional<BufferedImage> imageResult = source.getImageAround(i, j, 16, 500, 500);
                org.junit.Assert.assertTrue(imageResult.isPresent());

                imageResult.ifPresent(image -> imageView.setIcon(new ImageIcon(image)));

                Thread.sleep(16);
            }
        }

    }

    @After
    public void tearDown() throws Exception {
        frame.setVisible(false);
    }
}
