package org.puzzlehead.infiniteretribution;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * An activity representing a list of Targets. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link TargetDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class TargetListActivity extends BaseActivity implements AppDatabase.OnChangeListener
{
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    protected RecyclerView recyclerView = null;

    protected AppDatabase.TargetOrder targetOrder = AppDatabase.TargetOrder.NAME_ASC;

    protected static final String KEY_TARGET_ORDER = "TARGET_ORDER";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_list);

        targetOrder = AppDatabase.TargetOrder.valueOf(getSharedPreferences(getClass().getSimpleName(), 0).getString(KEY_TARGET_ORDER, AppDatabase.TargetOrder.NAME_ASC.name()));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                TargetListActivity.this.startActivity(new Intent(TargetListActivity.this, AddTargetActivity.class));
            }
        });

        AppDatabase.getInstance().addOnChangeListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.target_list);
        assert recyclerView != null;
        setupRecyclerView();

        if (findViewById(R.id.target_detail_container) != null)
        {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.target_list_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        boolean result = true;
        boolean updateItems = false;

        switch (item.getItemId())
        {
            case R.id.sort_by_name_asc:
                targetOrder = AppDatabase.TargetOrder.NAME_ASC;
                updateItems = true;
                break;

            case R.id.sort_by_name_desc:
                targetOrder = AppDatabase.TargetOrder.NAME_DESC;
                updateItems = true;
                break;

            case R.id.sort_by_units_asc:
                targetOrder = AppDatabase.TargetOrder.UNITS_ASC;
                updateItems = true;
                break;

            case R.id.sort_by_units_desc:
                targetOrder = AppDatabase.TargetOrder.UNITS_DESC;
                updateItems = true;
                break;

            default:
                result = super.onOptionsItemSelected(item);
        }

        if (updateItems)
        {
            SharedPreferences.Editor editor = getSharedPreferences(getClass().getSimpleName(), 0).edit();
            editor.putString(KEY_TARGET_ORDER, targetOrder.name());
            editor.commit();

            setupRecyclerView();
        }

        return result;
    }

    private void setupRecyclerView()
    {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(new AppDatabase(this).getTargets(targetOrder)));
    }

    @Override
    public void onChange()
    {
        setupRecyclerView();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        AppDatabase.getInstance().removeOnChangeListener(this);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>
    {

        private final List<Target> mValues;

        public SimpleItemRecyclerViewAdapter(List<Target> items)
        {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.target_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position)
        {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).getName());
            holder.mContentView.setText(Long.toString(mValues.get(position).getUnits()));

            holder.mView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mTwoPane)
                    {
                        Bundle arguments = new Bundle();
                        arguments.putLong(TargetDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
                        TargetDetailFragment fragment = new TargetDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.target_detail_container, fragment)
                                .commit();
                    } else
                    {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, TargetDetailActivity.class);
                        intent.putExtra(TargetDetailFragment.ARG_ITEM_ID, holder.mItem.getId());

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public Target mItem;

            public ViewHolder(View view)
            {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString()
            {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        setupRecyclerView();
    }
}
