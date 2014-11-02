import com.spartango.air.imagery.BingImageSource;
import com.spartango.air.target.KmlTargetSource;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

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
        KmlTargetSource loader = new KmlTargetSource("doc.kml");
        final List<Placemark> allTargets = loader.getTargets();
        System.out.println("Loaded " + allTargets.size() + " targets");
    }

    @Test
    public void findCategories() throws Exception {
        KmlTargetSource loader = new KmlTargetSource("doc.kml");
        final Optional<KmlTargetSource> subSource = loader.getSubSourceByName("SAM Site Overview")
                                                          .flatMap(s -> s.getSubSourceByName("SAMs by country"))
                                                          .flatMap(s -> s.getSubSourceByName("Asia"))
                                                          .flatMap(s -> s.getSubSourceByName("China"))
                                                          .flatMap(s -> s.getSubSourceByName("Beijing MR"))
                                                          .flatMap(s -> s.getSubSourceByName("Active"));

        Assert.assertTrue(subSource.isPresent());

        List<Placemark> allTargets = subSource.get().getTargets();
        System.out.println("Filtered to " + allTargets.size() + " targets");
        System.out.println("Subcategories include: " + subSource.get().getSubSources().keySet());
    }

    @Test
    public void displayTargets() throws Exception {
        KmlTargetSource loader = new KmlTargetSource("doc.kml");
        final Optional<KmlTargetSource> subSource = loader.getSubSourceByName("SAM Site Overview")
                                                          .flatMap(s -> s.getSubSourceByName("SAMs by country"))
                                                          .flatMap(s -> s.getSubSourceByName("Europe"))
                                                          .flatMap(s -> s.getSubSourceByName("Russia"))
                                                          .flatMap(s -> s.getSubSourceByName("Western Command"))
                                                          .flatMap(s -> s.getSubSourceByName("Active"));

        Assert.assertTrue(subSource.isPresent());

        List<Placemark> allTargets = subSource.get().getTargets();
        System.out.println("Displaying " + allTargets.size() + " targets");

        allTargets.stream()
                  .filter(target -> target.getGeometry() instanceof Point)
                  .flatMap(target -> ((Point) target.getGeometry()).getCoordinates().stream())
                  .map(location -> source.getImageAround(location.getLatitude(),
                                                         location.getLongitude(),
                                                         15,
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
