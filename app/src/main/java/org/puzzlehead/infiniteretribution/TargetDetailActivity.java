package org.puzzlehead.infiniteretribution;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * An activity representing a single Target detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link TargetListActivity}.
 */
public class TargetDetailActivity extends AppCompatActivity implements AppDatabase.Listener
{
    protected Target target = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                target.setCount(target.getCount() - 1);
                AppDatabase.getInstance().updateTarget(target);

                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null)
        {
            long id = getIntent().getLongExtra(TargetDetailFragment.ARG_ITEM_ID, 0);

            Log.d(getClass().getSimpleName(), "id = " + id);

            target = AppDatabase.getInstance().getTarget(id);

            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putLong(TargetDetailFragment.ARG_ITEM_ID,
                    getIntent().getLongExtra(TargetDetailFragment.ARG_ITEM_ID, 0));
            TargetDetailFragment fragment = new TargetDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.target_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.target_detail_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                navigateUpTo(new Intent(this, TargetListActivity.class));
                break;

            case R.id.edit:
                break;

            case R.id.add:
                target.setCount(target.getCount() + 1);
                AppDatabase.getInstance().updateTarget(target);
                break;

            case R.id.remove:
                target.setCount(target.getCount() - 1);
                AppDatabase.getInstance().updateTarget(target);
                break;

            case R.id.delete:
                AppDatabase.getInstance().deleteTarget(target);
                finish();
                break;

            case R.id.max:
                target.setCount(Long.MAX_VALUE);
                AppDatabase.getInstance().updateTarget(target);
                break;

            case R.id.min:
                target.setCount(Long.MIN_VALUE);
                AppDatabase.getInstance().updateTarget(target);
                break;

            case R.id.reset:
                target.setCount(0);
                AppDatabase.getInstance().updateTarget(target);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState)
    {
        super.onCreate(savedInstanceState, persistentState);

        AppDatabase.getInstance().addListener(this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        AppDatabase.getInstance().removeListener(this);
    }

    protected void updateView()
    {
        ;
    }

    @Override
    public void onChange()
    {
        Log.d(getClass().getSimpleName(), "onChange()");

        updateView();
    }
}
