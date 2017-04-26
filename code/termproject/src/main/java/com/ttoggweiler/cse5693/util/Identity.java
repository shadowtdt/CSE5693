package com.ttoggweiler.cse5693.util;

import java.util.UUID;

/**
 * Interface that gives an object a UUID,Name, and Creation time
 * // TODO: ttoggweiler 4/19/17 more 
 *
 */
public abstract class Identity
{
    private static final int SHORT_ID_TAIL_LENGTH = 4;
    private static final String NAME_DELIMINATOR = "_";

    private final UUID id = UUID.randomUUID();
    private final double creationTime = System.currentTimeMillis();
    private String name = null;

    private String customKeyString = null;

    public void setName(String name)
    {
        if(PreCheck.notEmpty(name))this.name = name;
    }

    /**
     * Common, changeable, human name,
     * Do no use for prop|map|config keys, use {@link #setMapKey(String)}
     * @return
     */
    public String name()
    {
        return PreCheck.notEmpty(name)
                ? name
                : getClass().getSimpleName()+ NAME_DELIMINATOR + shortId();
    }

    /**
     * Given name + full id
     * @return
     */
    public String fullName()
    {
        return PreCheck.notEmpty(name)
                ? name+NAME_DELIMINATOR+id.toString()
                : getId().toString();
    }


    /**
     * Last <ID_TAIL_Length>
     * @return
     */
    public String shortName()
    {
        return PreCheck.notEmpty(name)
                ? name + NAME_DELIMINATOR +shortId()
                : shortId();
    }

    public String describe(boolean useNewlines)
    {
        String breakPoint = useNewlines ? "\n" : " ";
        return breakPoint
                + "ID: " + id.toString() + breakPoint
                + "Name: " + name() + breakPoint
                + "Type: " + getClass().getTypeName() + breakPoint
                + "Class: " + getClass().getName() + breakPoint
                + "Created: " + creationMili() + breakPoint;
    }

    public UUID getId()
    {
        return id;
    }

    public String shortId()
    {
        return id.toString().substring(id.toString().length() - SHORT_ID_TAIL_LENGTH);
    }

    public double creationMili()
    {
        return creationTime;
    }

    public String key()
    {
        return PreCheck.isEmpty(customKeyString)
                ? name()
                : customKeyString;
    }

    public void setMapKey(String customKeyString)
    {
        this.customKeyString = customKeyString;
    }
}
