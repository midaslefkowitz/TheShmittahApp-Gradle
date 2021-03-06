package com.theshmittahapp.android.views.Fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.theshmittahapp.android.HelperClasses.AppUtils;
import com.theshmittahapp.android.models.Produce;
import com.theshmittahapp.android.R;
import com.theshmittahapp.android.views.Activities.NoResults;
import com.theshmittahapp.android.views.Activities.ProduceDetailsActivity;
import com.theshmittahapp.android.views.MyApp;

public class ProduceFragment extends Fragment{
	
	private String url = "http://www.mymakolet.com";
	
	private View mRootView;
	private ListView mListView;

    private Tracker mTracker;
	private List<Produce> mAllProduce;


	public ProduceFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        // google analytics tracker
        mTracker = ((MyApp) getActivity().getApplication()).getTracker(MyApp.TrackerName.APP_TRACKER);
        mTracker.enableAdvertisingIdCollection(true);

        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Set screen name for analytics
        mTracker.setScreenName("The Shmittah App Produce List Fragment");

        // Send a screen view for analytics
        mTracker.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.produce_fragment, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search)
                .getActionView();
        searchView.setQueryHint(Html.fromHtml("<font color = #fffbe4>" + getResources().getString(R.string.action_search) + "</font>"));
        searchView.setOnQueryTextListener(queryTextListener);

        super.onCreateOptionsMenu(menu, inflater);
    }

    final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String newText) {
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            // strip trailing whitespaces
            String modString = query.trim();
            // capitalize evey word
            //modString = WordUtils.capitalizeFully(modString);
            Produce p = queryInList(modString, mAllProduce);
            Intent i;
            if (p==null) {
                i = new Intent(getActivity(), NoResults.class);
                i.putExtra(NoResults.QUERY, query);
            } else {
                i = new Intent(getActivity(),
                        ProduceDetailsActivity.class);
                i.putExtra(ProduceDetailsActivity.PRODUCE, p);
            }
            startActivity(i);
            return true;
        }
    };

    private Produce queryInList(String query, List<Produce> allProd){
        for (Produce p : allProd) {
          if (p.getName().equalsIgnoreCase(query)) {
              return p;
          }
        }
        return null;
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

		mRootView = inflater.inflate(R.layout.fragment_produce, container, false);

		ImageView ad = (ImageView) mRootView.findViewById(R.id.ad);
        AppUtils.setAd(getActivity(), ad, url);

    	List<Produce> allProduce = createProduceArrayList();
		
		addListToAdapter(allProduce);

		return mRootView;
	}

	private void addListToAdapter(List<Produce> allProd) {
		final List<Produce> allProduce = allProd;
		String[] allProduceNamesArray = getResources().getStringArray(R.array.fruits_and_veggies);
		
		List<String> allProduceNamesList = new ArrayList<String>(Arrays.asList(allProduceNamesArray));
		
		ArrayAdapter<String> produceAdapter =
                new ArrayAdapter<String>(
                    getActivity(), // The current context (this activity)
                    R.layout.produce_list_item, // The layout ID.
                    R.id.list_item_produce_textview, // The ID of the textview to populate.
                    allProduceNamesList);
		
		mListView = (ListView) mRootView.findViewById(R.id.producelist);
		mListView.setAdapter(produceAdapter);

		/* Add Click Listener */

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						ProduceDetailsActivity.class);
				Produce produce = allProduce.get(position);
				intent.putExtra(ProduceDetailsActivity.PRODUCE, produce);
				startActivity(intent);
			}
		});
	}

	private List<Produce> createProduceArrayList() {
		String[] allProduceNames = getResources().getStringArray(R.array.fruits_and_veggies);
		String[] allProduceFrom = getResources().getStringArray(R.array.from_date);
        String[] allProduceFromYear = getResources().getStringArray(R.array.from_year);
		String[] allProduceTo = getResources().getStringArray(R.array.to_date);
        String[] allProduceToYear = getResources().getStringArray(R.array.to_year);
        String[] allProduceSefichimFrom = getResources().getStringArray(R.array.sefichim_from);
        String[] allProduceSefichimTo = getResources().getStringArray(R.array.sefichim_to);
		String[] allProducePrepare = getResources().getStringArray(R.array.prepared);
		String[] allProducePeeled = getResources().getStringArray(R.array.peeled);
		String[] allProducePits = getResources().getStringArray(R.array.pits);
		String[] allProducePeel = getResources().getStringArray(R.array.peel);
		String[] allProduceMashed = getResources().getStringArray(R.array.mashed);
		String[] allProduceComments = getResources().getStringArray(R.array.comments);
		
		mAllProduce = new ArrayList<Produce>();
		for (int i=0; i<allProduceNames.length; i++)
		{
			Produce produce = new Produce(
					allProduceNames[i],
					allProduceFrom[i],
                    allProduceFromYear[i],
					allProduceTo[i],
                    allProduceToYear[i],
                    allProduceSefichimFrom[i],
                    allProduceSefichimTo[i],
					allProducePrepare[i],
					allProducePeeled[i],
					allProducePits[i],
					allProducePeel[i],
					allProduceMashed[i],
					allProduceComments[i]
					);
			mAllProduce.add(produce);
		}
		
		return mAllProduce;
	}
}