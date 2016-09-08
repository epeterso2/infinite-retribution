package org.puzzlehead.infiniteretribution;

/**
 * Created by epeterson on 9/7/2016.
 */
public class Target
{
    protected long id = 0;

    protected String name = null;

    protected long count = 0;

    public Target()
    {
        ;
    }

    public Target(long id, String name, long count)
    {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    public long getCount()
    {
        return count;
    }

    public void setCount(long count)
    {
        this.count = count;
    }

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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Target target = (Target) o;

        if (id != target.id) return false;
        if (count != target.count) return false;
        return name != null ? name.equals(target.name) : target.name == null;

    }

    @Override
    public int hashCode()
    {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (int) (count ^ (count >>> 32));
        return result;
    }
}