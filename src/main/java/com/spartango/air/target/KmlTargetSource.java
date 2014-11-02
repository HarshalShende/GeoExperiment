package com.spartango.air.target;

import de.micromata.opengis.kml.v_2_2_0.*;

import java.io.File;
import java.util.*;
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

    public Map<String, KmlTargetSource> getSubSources() {
        if (document instanceof Document) {
            return getSubSources((Document) document);
        } else if (document instanceof Folder) {
            return getSubSources((Folder) document);
        } else {
            return Collections.EMPTY_MAP;
        }
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

    private static Map<String, KmlTargetSource> getSubSources(Document document) {
        return document.getFeature()
                       .stream()
                       .filter(feature -> feature instanceof Folder)
                       .collect(Collectors.toMap(Feature::getName, feature -> new KmlTargetSource((Folder) feature)));
    }

    private static Map<String, KmlTargetSource> getSubSources(Folder document) {
        return document.getFeature()
                       .stream()
                       .filter(feature -> feature instanceof Folder)
                       .collect(Collectors.toMap(Feature::getName, feature -> new KmlTargetSource((Folder) feature)));
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
