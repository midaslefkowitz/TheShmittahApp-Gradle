package com.theshmittahapp.android.views.Fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.theshmittahapp.android.HelperClasses.AppUtils;
import com.theshmittahapp.android.HelperClasses.ExpandableListAdapter;
import com.theshmittahapp.android.R;
import com.theshmittahapp.android.views.MyApp;

public class FAQFragment extends Fragment{
	
	private String url = "http://www.mymakolet.com";
	
	private ExpandableListAdapter listAdapter;
	private ExpandableListView expListView;
	private List<String> listDataHeader;
	private HashMap<String, List<String>> listDataChild;

    private Tracker mTracker;

	public FAQFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTracker = ((MyApp) getActivity().getApplication()).getTracker(MyApp.TrackerName.APP_TRACKER);
        mTracker.enableAdvertisingIdCollection(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Set screen name.
        mTracker.setScreenName("The Shmittah App FAQ Fragment");

        // Send a screen view.
        mTracker.send(new HitBuilders.AppViewBuilder().build());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		// inflate view
		View mRootView = inflater.inflate(R.layout.fragment_faq, container, false);
		ImageView ad = (ImageView) mRootView.findViewById(R.id.ad);

        AppUtils.setAd(getActivity(), ad, url);
		
		// get the listview
        expListView = (ExpandableListView) mRootView.findViewById(R.id.faq_questions_lv);
 
        // preparing list data
        prepareListData();
 
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
 
        // setting list adapter
        expListView.setAdapter(listAdapter);
        
		return mRootView;
	}
	
	private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
 
        String[] questions = getResources().getStringArray(R.array.FAQquestions);
        String[] answers = getResources().getStringArray(R.array.FAQanswers);
        
        for (int i=0; i<questions.length; i++)
        {
        	listDataHeader.add(questions[i]);
        	List<String> answer = new ArrayList<String>();
        	answer.add(answers[i]);
        	listDataChild.put(listDataHeader.get(i), answer);
        }
    }
}