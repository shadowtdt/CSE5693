package com.ttoggweiler.cse5693.util;

import java.util.UUID;

/**
 * Created by ttoggweiler on 3/9/17.
 */
public class Identity
{
    private UUID id = UUID.randomUUID();
    private String name = id.toString();
    private double creationTime = System.currentTimeMillis();

    public UUID getId()
    {
        return id;
    }

    public double getCreationTime()
    {
        return creationTime;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        if (PreCheck.notEmpty(name)) this.name = name.trim();
    }

}
