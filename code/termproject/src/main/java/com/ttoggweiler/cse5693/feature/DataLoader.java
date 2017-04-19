package com.ttoggweiler.cse5693.feature;

import com.ttoggweiler.cse5693.util.PreCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ttoggweiler on 2/16/17.
 */
public class DataLoader
{
    /**
     * Loads and parses features from provided Path
     * @param path path of the file to load
     * @return A set of features
     * @throws IOException when files does not exist or is not readable
     */
    public static List<Map<String,Comparable>> loadDataFromPath(Path path, List<Feature> featureSet) throws IOException
    {
        if (path == null) throw new NullPointerException("Unable to load features from a null path");
        List<String> lines = Files.lines(path)
                .filter(PreCheck::notEmpty)
                .collect(Collectors.toList());

        List<Map<String,  Comparable>> data = new ArrayList<>();
        // For each example
        for (String line : lines) {
            String[] splitLine = line.trim().split("\\s+");
            if (splitLine.length != featureSet.size()) throw new IllegalArgumentException("Data line does not have correct number of features: " + line);
            Map<String, Comparable> dataMap = new HashMap<>();
            // for each value in example
            for (int i = 0; i < featureSet.size(); i++){
                Feature feature = featureSet.get(i);
                Comparable featureValue = feature.parseValue(splitLine[i])
                        .orElseThrow(() -> new IllegalArgumentException("Failed to parse feature " + feature.getName() + "from line: "+splitLine));
                dataMap.put(featureSet.get(i).getName(), featureValue);
            }
            data.add(dataMap);
        }
        return data;
    }

    /**
     * Loads and parses features from provided file path
     * @param pathToData path to file to load
     * @return A set of features parsed from the provided file
     * @throws IOException when files does not exist or is not readable
     */
    public static List<Map<String, Comparable>> loadDataFromFile(String pathToData, List<Feature> featureSet) throws IOException
    {
        if (PreCheck.isEmpty(pathToData)) throw new NullPointerException("Unable to load features from a null file path string");
        URL resource = DataLoader.class.getResource(pathToData);
        Path path = resource==null? Paths.get(pathToData) : Paths.get(resource.getPath());
        return loadDataFromPath(path, featureSet);
    }


    public static void main(String... args)
    {
        Logger log = LoggerFactory.getLogger(DataLoader.class);


        String dataFilePath = "/inputFiles/tennis-train.txt";
        String featureFilePath = "/inputFiles/tennis-attr.txt";
        log.info("Loading file: {}", dataFilePath);

        try {
            List<Feature> features = FeatureLoader.loadFeaturesFromFile(featureFilePath);
            List<Map<String, Comparable>> datas = loadDataFromFile(dataFilePath,features);

            log.debug("Loaded {} instances: ", datas.size());
            int i =0;
            for(Map<String,? extends Comparable> data : datas ){
                log.info("#{} Values: {}",i++, data.toString());
            }
        } catch (IOException e) {
            log.error("Failed loading data from file.", e);
        }


    }
}

