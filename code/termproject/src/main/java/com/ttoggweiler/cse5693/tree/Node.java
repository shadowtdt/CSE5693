package com.ttoggweiler.cse5693.tree;

import com.ttoggweiler.cse5693.util.Identity;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by ttoggweiler on 2/15/17.
 */
public class Node<T> extends Identity
{
    public static final Node ROOT_NODE_PARENT = new Node("ROOT_NODE_PARENT");

    private Node<T> parentNode;
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
        setName(name);
        setParentNode(PreCheck.defaultTo(parentNode, ROOT_NODE_PARENT));
        this.data = data;
    }

    public boolean hasParent()
    {
        return this.parentNode != null && this.parentNode != ROOT_NODE_PARENT;
    }

    public Optional<? extends Node> getParentNode()
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

    public Optional<ArrayList<? extends Node>> getChildrenNodes()
    {
        if (hasChildren()) return Optional.of(childrenNodes);
        else return Optional.empty();
    }

    public void setChildNode(Node<T> childNode)
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

    public Node<T> getRootNode()
    {
        return getParentNode().isPresent() ? getParentNode().get() : this;
    }

    public int distanceFromRoot()
    {
        return getParentNode().map(Node::distanceFromRoot).orElse(0) + 1;
    }

    public List<? extends Node> getPathNodes()
    {
        List<Node<T>> pathNodes = new ArrayList<>(distanceFromRoot());
        pathNodes.add(this);
        Optional<? extends Node> oParent = getParentNode();
        while(oParent.isPresent())
        {
            Node<T> parent = oParent.get();
            pathNodes.add(parent);
            oParent = parent.getParentNode();
        }
        return pathNodes;
    }

    public int getMaxDepth()
    {
        return hasChildren()
                ? getChildrenNodes().get().stream().mapToInt(Node :: getMaxDepth).max().getAsInt()
                : distanceFromRoot();
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
        return name();
    }

}
