package com.ttoggweiler.cse5693.loader;

import com.ttoggweiler.cse5693.tree.Feature;
import com.ttoggweiler.cse5693.util.PreCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ttoggweiler on 2/16/17.
 */
public class FeatureLoader
{
    /**
     * Loads and parses features from provided Path
     * @param path path of the file to load
     * @return A set of features
     * @throws IOException when files does not exist or is not readable
     */
    public static List<Feature> loadFeaturesFromPath(Path path) throws IOException
    {
        if (path == null) throw new NullPointerException("Unable to load features from a null path");
        return Files.lines(path)
                .filter(PreCheck :: notEmpty)
                .map(Feature::parseFeature)
                .collect(Collectors.toList());
    }

    /**
     * Loads and parses features from provided file path
     * @param pathToFeatures path to file to load
     * @return A set of features parsed from the provided file
     * @throws IOException when files does not exist or is not readable
     */
    public static List<Feature> loadFeaturesFromFile(String pathToFeatures) throws IOException
    {
        if (pathToFeatures == null) throw new NullPointerException("Unable to load features from a null file path string");
        URL resource = DataLoader.class.getResource(pathToFeatures);
        Path path = resource==null? Paths.get(pathToFeatures) : Paths.get(resource.getPath());
        return loadFeaturesFromPath(path);
    }


    public static void main(String... args)
    {
        Logger log = LoggerFactory.getLogger(FeatureLoader.class);


        String fileToLoad = "/inputFiles/tennis-attr.txt";
        log.info("Loading file: {}", fileToLoad);

        try {
            List<Feature> features = FeatureLoader.loadFeaturesFromFile(fileToLoad);
            log.info("Loaded {} features: ", features.size());
            features.forEach(feature -> {
                log.info("Feature Name: {} Values: {}",feature.getName(),feature.getValues());
            });
        } catch (IOException e) {
            log.error("Failed loading features from file.", e);
        }


    }
}
