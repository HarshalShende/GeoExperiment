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
 * <p>
 * Time: 15:34.
 */
public class KmlTargetSource {
    private Container document;

    public KmlTargetSource(File file) {
        this((Document) Kml.unmarshal(file).getFeature());
    }

    public KmlTargetSource(String filePath) {
        this(new File(filePath));
    }

    public KmlTargetSource(Container document) {
        this.document = document;
    }

    public List<Placemark> getTargets() {
        return extractPlaces(document);
    }

    public Optional<KmlTargetSource> getSubSourceByName(String name) {
        if (document instanceof Document) {
            return getSubSourceByName((Document) document, name);
        } else if (document instanceof Folder) {
            return getSubSourceByName((Folder) document, name);
        } else {
            return Optional.empty();
        }
    }

    public List<String> getSubSourceNames() {
        if (document instanceof Document) {
            return getSubSourceNames((Document) document);
        } else if (document instanceof Folder) {
            return getSubSourceNames((Folder) document);
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    private static List<String> getSubSourceNames(Document document) {
        return document.getFeature()
                       .stream()
                       .filter(feature -> feature instanceof Folder)
                       .map(Feature::getName)
                       .collect(Collectors.toList());
    }

    private static List<String> getSubSourceNames(Folder document) {
        return document.getFeature()
                       .stream()
                       .filter(feature -> feature instanceof Folder)
                       .map(Feature::getName)
                       .collect(Collectors.toList());
    }

    private static Optional<KmlTargetSource> getSubSourceByName(Folder target, String name) {
        final Optional<Feature> featureByName = getFeatureByName(target, name);
        if (featureByName.isPresent() && featureByName.get() instanceof Folder) {
            return Optional.of(new KmlTargetSource((Folder) featureByName.get()));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<KmlTargetSource> getSubSourceByName(Document target, String name) {
        final Optional<Feature> featureByName = getFeatureByName(target, name);
        if (featureByName.isPresent() && featureByName.get() instanceof Folder) {
            return Optional.of(new KmlTargetSource((Folder) featureByName.get()));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<Feature> getFeatureByName(Folder target, String name) {
        return target.getFeature()
                     .stream()
                     .filter(feature -> feature.getName().equals(name))
                     .findFirst();
    }

    private static Optional<Feature> getFeatureByName(Document target, String name) {
        return target.getFeature()
                     .stream()
                     .filter(feature -> feature.getName().equals(name))
                     .findFirst();
    }

    private static List<Placemark> extractPlaces(Container document) {
        if (document instanceof Document) {
            return extractPlaces((Document) document);
        } else if (document instanceof Folder) {
            return extractPlaces((Folder) document);
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    private static List<Placemark> extractPlaces(Document target) {
        return target.getFeature()
                     .stream()
                     .flatMap(KmlTargetSource::extractPlaces)
                     .collect(Collectors.toList());
    }

    private static List<Placemark> extractPlaces(Folder target) {
        return target.getFeature()
                     .stream()
                     .flatMap(KmlTargetSource::extractPlaces)
                     .collect(Collectors.toList());
    }

    private static Stream<Placemark> extractPlaces(Feature feature) {
        if (feature instanceof Folder) {
            return extractPlaces((Folder) feature).stream();
        } else if (feature instanceof Placemark) {
            return Arrays.asList((Placemark) feature).stream();
        } else {
            return Stream.empty();
        }
    }

}
