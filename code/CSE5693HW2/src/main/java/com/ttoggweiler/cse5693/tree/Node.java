package com.ttoggweiler.cse5693.tree;

import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ttoggweiler on 2/15/17.
 */
public class Node<T>
{
    public static final Node ROOT_NODE_PARENT = new Node("ROOT_NODE_PARENT");

    private UUID id = UUID.randomUUID();
    private String name;

    private Node parentNode;
    private T data;
    private ArrayList<Node> childrenNodes;

    private Node(String name)
    {
        setName(name);
    }

    public T getData()
    {
        return data;
    }

    public Node(String name, Node parentNode, T data)
    {
        setName(PreCheck.defaultTo(name, id.toString()));
        setParentNode(PreCheck.defaultTo(parentNode, ROOT_NODE_PARENT));
        this.data = data;
    }

    public UUID getId()
    {
        return id;
    }

    public String getName()
    {
        return PreCheck.defaultTo(name, id.toString());
    }

    public void setName(String name)
    {
        if (!PreCheck.isEmpty(name)) this.name = name.trim();
    }

    public boolean hasParent()
    {
        return this.parentNode != null && this.parentNode != ROOT_NODE_PARENT;
    }

    public Optional<Node<T>> getParentNode()
    {
        if (hasParent()) return Optional.of(parentNode);
        else return Optional.empty();
    }

    public void setParentNode(Node<T> parentNode)
    {
        if (parentNode == null)
            throw new IllegalArgumentException("Node parent cannot be set to null. Use ROOT_NODE_PARENT to make this node root.");
        if (childrenNodes != null && childrenNodes.contains(parentNode))
            throw new IllegalArgumentException("Node cannot have a parent and child who are equal");

        if (this.parentNode != parentNode) {
            this.parentNode = parentNode;
            if (this.parentNode != ROOT_NODE_PARENT) this.parentNode.setChildNode(this);
        }
    }

    public boolean hasChildren()
    {
        return PreCheck.notEmpty(childrenNodes);
    }

    public Optional<ArrayList<Node>> getChildrenNodes()
    {
        if (hasChildren()) return Optional.of(childrenNodes);
        else return Optional.empty();
    }

    public void setChildNode(Node childNode)
    {
        if (childNode == null)
            throw new IllegalArgumentException("Child Node cannot be set to null.");
        if (childNode == ROOT_NODE_PARENT)
            throw new IllegalArgumentException("ROOT_NODE_PARENT cannot be set as a child.");
        if (childNode == parentNode)
            throw new IllegalArgumentException("Node cannot have a parent and child who are equal");

        if (childrenNodes == null) childrenNodes = new ArrayList<>();
        if (!childrenNodes.contains(childNode)) {
            childrenNodes.add(childNode);
            if (childNode.getParentNode().isPresent() && !childNode.getParentNode().get().equals(this)) {
                childNode.setParentNode(this);
            }
        }
    }

    public Node getRootNode()
    {
        return hasParent() ? getRootNode() : this;
    }

    public int distanceFromRoot()
    {
        return getParentNode().map(Node::distanceFromRoot).orElse(0) + 1;
    }

    /*
    height = tall
    | size>2 = T
    | | color = black
    | | | weight = heavy : Yes (1,0)
    | | | weight = light : No (0,1)
    | | color = white
    | | | weight = heavy : Yes (2,0)
    | | | weight = light : No (0,1)
    | size>2 = F
    | | weight = heavy : Yes (4,0)
    | | weight = light : No
     */

    public String toString()
    {
        return getName();
    }

//    public String toTreeString()
//    {
//        String prefix = "";
//        for (int i = 1; i < distanceFromRoot(); i++) prefix += "|\t";
//        String treeStr = prefix + this.toString();
//        if (PreCheck.notEmpty(childrenNodes))
//            for (Node child : childrenNodes)
//                treeStr += "\n" + child.toTreeString();
//        return treeStr;
//    }

}
