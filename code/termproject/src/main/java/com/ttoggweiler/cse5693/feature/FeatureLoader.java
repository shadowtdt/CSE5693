package com.ttoggweiler.cse5693.feature;

import com.ttoggweiler.cse5693.util.PreCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class extracts Feature objects from text files.
 * That static methods can be used to extract a list of features with no separation of input/target
 * or create an instance and the argument features and target features will be separated and accessible through getters
 */
public class FeatureLoader
{
    private List<Feature> argumentFeatures = null;
    private List<Feature> targetFeatures = null;

    public FeatureLoader(String pathString)throws IOException{
        this(getPathForString(pathString));
    }

    public FeatureLoader(Path path) throws IOException
    {
        List<String> linesInFile = Files.readAllLines(path);
        List<String> argumentFeatureStringList = new ArrayList<>();
        List<String> targetFeatureStringList = new ArrayList<>();

        // Separate argument features from target features
        for (int i = 0; i < linesInFile.size(); i++) {
            if(PreCheck.isEmpty(linesInFile.get(i))) {
                argumentFeatureStringList = linesInFile.subList(0,i);
                targetFeatureStringList = linesInFile.subList(i+1,linesInFile.size());
                break;
            }
        }

        PreCheck.ifEmpty(()-> new IllegalStateException("No features were detected in file: " +path.toString()),argumentFeatureStringList);
        PreCheck.ifEmpty(()-> new IllegalStateException("No target features were detected in file: " +path.toString()),targetFeatureStringList);

        // Parse features from partitioned list
        this.argumentFeatures = loadFeaturesFromArray(argumentFeatureStringList);
        this.targetFeatures = loadFeaturesFromArray(targetFeatureStringList);

        PreCheck.ifEmpty(()-> new IllegalStateException("No features were loaded from file: " +path.toString()),argumentFeatures);
        PreCheck.ifEmpty(()-> new IllegalStateException("No target features were loaded from file: " +path.toString()),targetFeatures);
    }

    public List<Feature> getArgumentFeatures()
    {
        return this.argumentFeatures;
    }

    public List<Feature> getTargetFeatures()
    {
        return this.targetFeatures;
    }

    public List<Feature> getAllFeatures()
    {
        List<Feature> allFeaturesList = new ArrayList<>(argumentFeatures);
        allFeaturesList.addAll(targetFeatures);
        return allFeaturesList;
    }
    /* Static */

    /**
     * Loads and parses features from provided Array
     * @param featureList Array of features
     * @return A List of features
     */
    public static List<Feature> loadFeaturesFromArray(List<String> featureList)
    {
        PreCheck.ifEmpty(()-> new NullPointerException("Unable to load features from a null path"),featureList);
        return featureList.stream()
                .filter(PreCheck:: notEmpty)
                .map(Feature::parseFeature)
                .collect(Collectors.toList());
    }

    /**
     * Loads and parses features from provided Path
     * @param path path of the file to load
     * @return A set of features
     * @throws IOException when files does not exist or is not readable
     */
    public static List<Feature> loadFeaturesFromPath(Path path) throws IOException
    {
        return Files.lines(path)
                .filter(PreCheck:: notEmpty)
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
        return loadFeaturesFromPath(getPathForString(pathToFeatures));
    }

    /**
     * Attempts to get Path object for provided path string
     * @param pathString path to file
     * @return Path of resource or external file
     */
    private static Path getPathForString(String pathString)  throws IOException
    {
        if (pathString == null) throw new NullPointerException("Unable to load features from a null file path string");
        URL resource = FeatureLoader.class.getResource(pathString);
        Path path = resource==null? Paths.get(pathString) : Paths.get(resource.getPath());
        if(path == null || !path.toFile().exists())
            throw new FileNotFoundException("Unable to find file as resource or system file: " + pathString);
        return path;
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
