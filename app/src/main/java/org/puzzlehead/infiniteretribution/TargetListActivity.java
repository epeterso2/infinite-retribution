package org.puzzlehead.infiniteretribution;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
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
public class TargetListActivity extends AppCompatActivity
{
    public static final String TAG = TargetListActivity.class.getSimpleName();

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Created new target", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                AppDatabase.getInstance().createTarget(new Target(0, "Primary Target", 0));
            }
        });

        View recyclerView = findViewById(R.id.target_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.target_detail_container) != null)
        {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView)
    {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(AppDatabase.getInstance().getTargets()));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>
        implements AppDatabase.Listener
    {

        private final List<Target> mValues;

        public SimpleItemRecyclerViewAdapter(List<Target> items)
        {
            mValues = items;
            AppDatabase.getInstance().addListener(this);
        }

        public void update()
        {
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
            long count = holder.mItem.getCount();

            String countText;

            if (count == Long.MAX_VALUE)
            {
                countText = "∞";
            }

            else if (count == Long.MIN_VALUE)
            {
                countText = "-∞";
            }

            else
            {
                countText = Long.toString(count);
            }

            holder.mIdView.setText(countText);
            holder.mContentView.setText(mValues.get(position).getName());

            holder.mView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Log.d(TAG, "onClick() id: " + Long.toString(holder.mItem.getId()));

                    holder.mItem.setCount(holder.mItem.getCount() + 1);
                    AppDatabase.getInstance().updateTarget(holder.mItem);
                }
            });

            holder.mView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    Log.d(TAG, "onLongClick() id: " + Long.toString(holder.mItem.getId()));

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

                    return true;
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return mValues.size();
        }

        @Override
        public void onChange()
        {
            mValues.clear();
            mValues.addAll(AppDatabase.getInstance().getTargets());
            notifyDataSetChanged();
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
}
