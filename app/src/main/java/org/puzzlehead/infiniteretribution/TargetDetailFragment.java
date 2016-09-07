package org.puzzlehead.infiniteretribution;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
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
public class TargetDetailFragment extends Fragment implements AppDatabase.Listener
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

    protected View rootView = null;

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
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = AppDatabase.getInstance().getTarget(getArguments().getLong(ARG_ITEM_ID));
        }

        AppDatabase.getInstance().addListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.target_detail, container, false);

        // Show the dummy content as text in a TextView.
        onChange();

        return rootView;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        AppDatabase.getInstance().removeListener(this);
    }

    @Override
    public void onChange()
    {
        Log.d(getClass().getSimpleName(), "onChange()");

        mItem = AppDatabase.getInstance().getTarget(mItem.getId());

        if (mItem != null)
        {
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);

            if (appBarLayout != null)
            {
                appBarLayout.setTitle(mItem.getName());
            }

            ((TextView) rootView.findViewById(R.id.target_detail)).setText(Long.toString(mItem.getCount()));
        }
    }
}
