package org.puzzlehead.infiniteretribution;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

/**
 * An activity representing a single Target detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link TargetListActivity}.
 */
public class TargetDetailActivity extends AppCompatActivity
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
                target.setCount(RetributionUtil.add(target.getCount(), 1));
                AppDatabase.getInstance().updateTarget(target);
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
                edit();
                break;

            case R.id.delete:
                delete();
                break;

            case R.id.add:
                updateTargetCount(RetributionUtil.add(target.getCount(), 1));
                break;

            case R.id.remove:
                updateTargetCount(RetributionUtil.add(target.getCount(), -1));
                break;

            case R.id.max:
                updateTargetCount(Long.MAX_VALUE);
                break;

            case R.id.min:
                updateTargetCount(Long.MIN_VALUE);
                break;

            case R.id.doubleUp:
                updateTargetCount(RetributionUtil.multiply(target.getCount(), 2));
                break;

            case R.id.halve:
                updateTargetCount(target.getCount() / 2);
                break;

            case R.id.change_sign:
                long count;

                if (target.getCount() == Long.MAX_VALUE)
                {
                    count = Long.MIN_VALUE;
                }

                else if (target.getCount() == Long.MIN_VALUE)
                {
                    count = Long.MAX_VALUE;
                }

                else
                {
                    count = target.getCount() * -1;
                }

                updateTargetCount(count);
                break;

            case R.id.reset:
                updateTargetCount(0);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    protected void edit()
    {
        final EditText editText = new EditText(this);
        editText.setText(target.getName() == null ? "" : target.getName());

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Target Name");
        alert.setView(editText);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                target.setName(editText.getText().toString());
                AppDatabase.getInstance().updateTarget(target);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                ;
            }
        });

        alert.show();
    }

    protected void delete()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(target.getName());
        alert.setMessage("Delete this target?");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                AppDatabase.getInstance().deleteTarget(target);
                finish();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                ;
            }
        });

        alert.show();
    }

    protected void updateTargetCount(long count)
    {
        target.setCount(count);
        AppDatabase.getInstance().updateTarget(target);
    }
}
