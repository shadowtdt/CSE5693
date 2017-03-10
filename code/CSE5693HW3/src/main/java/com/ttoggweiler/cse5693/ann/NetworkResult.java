package com.ttoggweiler.cse5693.ann;

import com.ttoggweiler.cse5693.util.Identity;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.List;

/**
 * Created by ttoggweiler on 3/10/17.
 */
public class NetworkResult extends Identity
{
    private List<LayerResult> layerResults;

    public NetworkResult(List<LayerResult> layerResults){
        PreCheck.ifEmpty(()->new IllegalArgumentException("Unable to create Network Result from null or empty layer result list"),layerResults);
        this.layerResults = layerResults;
    }

    public List<LayerResult> getLayerResults()
    {
        return layerResults;
    }
}
