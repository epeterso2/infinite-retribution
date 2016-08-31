package org.puzzlehead.infiniteretribution;

/**
 * Created by epeterson on 8/31/2016.
 */
public class Target
{
    protected long id = 0;

    protected String name = "";

    protected long units = 0;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public long getUnits()
    {
        return units;
    }

    public void setUnits(long units)
    {
        this.units = units;
    }

    public String toString()
    {
        return getName() + " (" + getId() + ")";
    }
}