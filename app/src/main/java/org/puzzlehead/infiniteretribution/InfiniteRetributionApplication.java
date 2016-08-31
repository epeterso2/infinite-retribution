package org.puzzlehead.infiniteretribution;

import android.app.Application;

/**
 * Created by epeterson on 8/31/2016.
 */
public class InfiniteRetributionApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        AppDatabase.initialize(this);
    }
}