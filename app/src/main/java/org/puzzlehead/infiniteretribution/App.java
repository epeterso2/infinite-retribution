package org.puzzlehead.infiniteretribution;

import android.app.Application;

/**
 * Created by epeterson on 9/7/2016.
 */
public class App extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        AppDatabase.setContext(this);
    }
}
