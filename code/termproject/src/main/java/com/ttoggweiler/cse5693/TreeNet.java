package com.ttoggweiler.cse5693;

import com.ttoggweiler.cse5693.ann.Network;
import com.ttoggweiler.cse5693.data.DataSet;
import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.predict.Classifier;
import com.ttoggweiler.cse5693.tree.FeatureNode;
import com.ttoggweiler.cse5693.tree.ID3Tree;
import com.ttoggweiler.cse5693.tree.Node;
import com.ttoggweiler.cse5693.util.MapUtil;
import com.ttoggweiler.cse5693.util.PreCheck;
import com.ttoggweiler.cse5693.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Leafnet is a classifier that uses an ID3 tree to partition the data, then uses a back-propagation ANN for fine grain classification
 */
public class TreeNet extends Classifier
{

    // Net Vars
    private double learningRate;
    private double accuracyThreshold;
    private double momentum;
    private int iterations;
    private int[] hiddenLayerSizes;

    // Tree vars
    private Integer depth;

    // TreeNet vars
    private static Logger log = LoggerFactory.getLogger(TreeNet.class);

    private boolean pathVoting = false;
    private int pathDepth = 6;

    ID3Tree tree;
    private Map<FeatureNode, Network> nodeNetMap = new HashMap<>();

    public TreeNet()
    {
    }

    /* Classify Impl */
    @Override
    public void train(DataSet trainingSet)
    {
        initWithDataSet(trainingSet);
        tree = new ID3Tree(getTargetFeatures().get(0), depth);
        tree.train(trainingSet);

        //Map<FeatureNode, DataSet> leafToDataMap = mapLeafsToData(tree, trainingSet);

        StreamUtil.getStream(tree.getRootNode().getLeafs())
                .filter(n -> n.getTargetDistributions().size()>1
                        ||  n.getTargetDistributions().values().stream().mapToInt(s -> s).sum() <= 1).forEach(r -> {
            FeatureNode leaf = r;//.getKey();
            DataSet data = trainingSet;//r.getValue();
            Network net = createNetworkForNode(leaf, data);
            nodeNetMap.put(leaf, net);
            if(pathVoting)
            {
                StreamUtil.getStream(leaf.getPathNodes()).filter(n -> !n.equals(leaf))
                        .filter(n -> (leaf.distanceFromRoot() - n.distanceFromRoot()) <= pathDepth)
                        .forEach((pathNode -> {
                    Network pnet = createNetworkForNode(leaf, trainingSet);//fixme, use r?
                    nodeNetMap.put((FeatureNode) pathNode, pnet);
                }));
            }
        });
        log.debug("Converted {}, nodes to networks",nodeNetMap.size());
    }

    @Override
    public Map<String, Comparable> classifyExample(Map<String, Comparable> example)
    {
        return pathVoting
                ? classify_Path(example)
                :classify_Leaf(example);
    }

    @Override
    public String getClassifierString(boolean includeName)
    {
        return name();
    }


    private Map<String, Comparable> classify_Path(Map<String, Comparable> example)
    {

        FeatureNode leaf = tree.getRootNode().getClassificationLeaf(example);
        Map<Map<String, Comparable>,Integer> votes = new HashMap<>();

        Map<String,Comparable> leafVote = nodeNetMap.get(leaf).classifyExample(example);
        // # votes = depth
        Integer leafVoteCount = 2 * pathDepth;
        votes.put(leafVote,leafVoteCount);
        Set<Node> nodesWhoCanVote = leaf.getPathNodes().stream().filter(n -> (leaf.distanceFromRoot() - n.distanceFromRoot()) <= pathDepth).collect(Collectors.toSet());
        nodesWhoCanVote.remove(leaf);
        for (Node pNode : nodesWhoCanVote) {
            Network pNet = nodeNetMap.get(pNode);
            Map<String, Comparable> vote = pNet.classifyExample(example);
            Integer currentVote = PreCheck.defaultTo(votes.get(vote),0);
            int pNodeVoteCount = pathDepth - (leaf.distanceFromRoot() - pNode.distanceFromRoot());
            votes.put(vote,currentVote + pNodeVoteCount);
        }

        Set<Map<String, Comparable>> maxVotes = MapUtil.keysForMax(votes);

        return maxVotes.iterator().next();
    }

    private Map<String, Comparable> classify_Leaf(Map<String, Comparable> example)
    {
        FeatureNode leaf = tree.getRootNode().getClassificationLeaf(example);
        // If there is no confusion, use tree's classification
        if(leaf.getTargetDistributions().size()==1
                && leaf.getTargetDistributions().values().stream().mapToInt(s -> s).sum() > 1)
            return tree.classifyExample(example);

        Network leafValueNet = nodeNetMap.get(leaf);
        if(leafValueNet == null) {
            log.warn("No Net found for leaf!");
            return tree.classifyExample(example);
        }

        return leafValueNet.classifyExample(example);
    }

    //    private static Map<String, Comparable> edgeClassify
    private static Map<FeatureNode, DataSet> mapLeafsToData(ID3Tree tree, DataSet data)
    {
       return tree.getRootNode().getLeafs().stream()
               .collect(Collectors.toMap(s ->s, s ->data.subsetOnPredicate(s.getPathPredicate())));
    }

    /**
     * Trains network on subst of data that featurePath predicate matches
     * @param node
     * @param train
     * @return
     */
    private Network createNetworkForNode(FeatureNode node, DataSet train)
    {
//        FeatureNode trainNode = node.getTargetDistributions().size() > 1
//                ? node
//                :(FeatureNode) PreCheck.defaultTo(node.getParentNode().get(),node);
        // TODO: ttoggweiler 4/26/17  small number handleing?
        DataSet nodeDataSet = train.subsetOnPredicate(node.getPathPredicate());
        Network net = new Network(train.getTargets(),iterations,learningRate,momentum,accuracyThreshold,hiddenLayerSizes);
        net.train(nodeDataSet);
        return net;
    }

    private static Map<Comparable, DataSet> mapLeafEdgesToData(FeatureNode leaf,DataSet data)
    {
        Map<Comparable,DataSet> edgeValueDataMap = new HashMap<>();
        leaf.getTargetDistributions().keySet().forEach(val -> {
            Feature leafFeature = leaf.getData();
            DataSet valueData = data.subsetForFeatureValue(leafFeature,val,!leafFeature.getFeatureType().isContinous());
            edgeValueDataMap.put(val,valueData);
        });
        return edgeValueDataMap;
    }

    /* Field methods */

    public void setNetVars(int iterations, double learningRate, double momentum, double accuracyThreshold, int... hiddenLayerSizes){
        this.learningRate = learningRate;
        this.iterations = iterations;
        this.momentum = momentum;
        this.hiddenLayerSizes = hiddenLayerSizes;
        this.accuracyThreshold = accuracyThreshold;
    }

    public void setTreeVars(int depth)
    {
        this.depth = depth;
    }
}
