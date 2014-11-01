package com.spartango.air.target;

import de.micromata.opengis.kml.v_2_2_0.*;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: spartango
 * Date: 11/1/14
 * Time: 15:34.
 */
public class TargetLoader {
    private Kml      kml;
    private Document document;

    public TargetLoader(File file) {
        kml = Kml.unmarshal(file);
        document = (Document) kml.getFeature();
    }


    public TargetLoader(String filePath) {
        this(new File(filePath));
    }

    public List<Placemark> getAllTargets() {
        final List<Feature> features = document.getFeature();
        if (features.size() > 0 && features.get(0) instanceof Folder) {
            return extractPlaces((Folder) features.get(0));
        } else if (features.get(0) instanceof Placemark) {
            return Collections.singletonList((Placemark) features.get(0));
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    private static Optional<Feature> getFeatureByName(Folder target, String name) {
        return target.getFeature()
                     .stream()
                     .filter(feature -> feature.getName().equals(name))
                     .findFirst();
    }

    private static List<Placemark> extractPlaces(Folder target) {
        return target.getFeature()
                     .stream()
                     .flatMap(feature -> {
                         if (feature instanceof Folder) {
                             return extractPlaces((Folder) feature).stream();
                         } else if (feature instanceof Placemark) {
                             return Arrays.asList((Placemark) feature).stream();
                         } else {
                             return Stream.empty();
                         }
                     }).collect(Collectors.toList());
    }
}
