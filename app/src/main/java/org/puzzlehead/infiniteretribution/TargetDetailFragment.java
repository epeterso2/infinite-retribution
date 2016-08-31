package org.puzzlehead.infiniteretribution;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single Target detail screen.
 * This fragment is either contained in a {@link TargetListActivity}
 * in two-pane mode (on tablets) or a {@link TargetDetailActivity}
 * on handsets.
 */
public class TargetDetailFragment extends Fragment implements AppDatabase.OnChangeListener
{
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Target mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TargetDetailFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID))
        {
            long id = getArguments().getLong(ARG_ITEM_ID);

            mItem = AppDatabase.getInstance().getTarget(getArguments().getLong(ARG_ITEM_ID));
            AppDatabase.getInstance().addOnChangeListener(this);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null)
            {
                appBarLayout.setTitle(mItem.getName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.target_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null)
        {
            updateView(rootView);
        }

        return rootView;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        AppDatabase.getInstance().removeOnChangeListener(this);
    }

    protected void updateView(View view)
    {
        ((TextView) view.findViewById(R.id.target_detail)).setText(Long.toString(mItem.getUnits()));
    }

    @Override
    public void onChange()
    {
        mItem = AppDatabase.getInstance().getTarget(mItem.getId());
        updateView(getView());
    }
}