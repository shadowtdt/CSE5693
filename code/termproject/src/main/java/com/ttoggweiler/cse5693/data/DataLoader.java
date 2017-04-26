package com.ttoggweiler.cse5693.data;

import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.feature.FeatureLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by ttoggweiler on 2/16/17.
 * @deprecated moved to {@link DataSet}
 */
@Deprecated
public class DataLoader
{




    public static void main(String... args)
    {
        Logger log = LoggerFactory.getLogger(DataLoader.class);


        String dataFilePath = "/inputFiles/tennis-train.txt";
        String featureFilePath = "/inputFiles/tennis-attr.txt";
        log.info("Loading file: {}", dataFilePath);

        try {
            List<Feature> features = FeatureLoader.loadFeaturesFromFile(featureFilePath);
            List<Map<String, Comparable>> datas = DataSet.loadDataFromFile(dataFilePath,features);

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

