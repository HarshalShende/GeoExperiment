import com.spartango.air.imagery.BingImageSource;
import com.spartango.air.target.TargetLoader;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Author: spartango
 * Date: 11/1/14
 * Time: 16:35.
 */
public class KmlTest {

    private JFrame frame;
    private JLabel imageView;

    private BingImageSource source;


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

        source = new BingImageSource();
    }

    @Test
    public void loadKml() throws Exception {
        TargetLoader loader = new TargetLoader("doc.kml");
        final List<Placemark> allTargets = loader.getAllTargets();
        System.out.println("Loaded " + allTargets.size() + " targets");
    }

    @Test
    public void displayTargets() throws Exception {
        TargetLoader loader = new TargetLoader("doc.kml");
        final List<Placemark> allTargets = loader.getAllTargets();
        System.out.println("Loaded " + allTargets.size() + " targets");


        allTargets.stream()
                  .flatMap(target -> ((Point) target.getGeometry()).getCoordinates().stream())
                  .map(location -> source.getImageAround(location.getLatitude(),
                                                         location.getLongitude(),
                                                         18,
                                                         500,
                                                         500))
                  .forEach(result -> result.ifPresent(this::setImage));
    }

    private void setImage(BufferedImage image) {
        imageView.setIcon(new ImageIcon(image));
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();  //TODO handle e
        }
    }
}
