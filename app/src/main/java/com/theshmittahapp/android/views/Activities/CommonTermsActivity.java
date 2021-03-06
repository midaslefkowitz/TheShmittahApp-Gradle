package com.theshmittahapp.android.views.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ShareActionProvider;

import com.theshmittahapp.android.HelperClasses.NavDrawerListAdapter;
import com.theshmittahapp.android.R;
import com.theshmittahapp.android.views.NavDrawerItem;
import com.theshmittahapp.android.views.Fragments.AboutFragment;
import com.theshmittahapp.android.views.Fragments.ChartFragment;
import com.theshmittahapp.android.views.Fragments.CommonTermsFragment;
import com.theshmittahapp.android.views.Fragments.DonateDialogFragment;
import com.theshmittahapp.android.views.Fragments.FAQFragment;
import com.theshmittahapp.android.views.Fragments.PDFFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommonTermsActivity extends Activity {

    private static final String DONATE_DIALOG_TAG = "donate";

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private List<String> mNavMenuTitles;


    private ShareActionProvider mShareActionProvider;

	private ActionBarDrawerToggle mDrawerToggle;
    private String mDetailedName = "detailed.pdf";
    private String mOverviewName = "overview.pdf";
    private String mShiurimUrl = "http://mp3shiur.com/viewProd.asp?catID=279&max=all&startAt=1";
    private String mRabbisEmailAddress = "theshmittahapp@gmail.com";

	private String mSubject = "Question from The Shmittah App";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createLayout();

        setDrawerToggle();

        if (savedInstanceState == null) {
            // on first time display view for common terms
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, new CommonTermsFragment())
//                    .addToBackStack(CommonTermsFragment.class.getSimpleName())
                    .commit();
        }
    }

    private void createLayout() {
        setContentView(R.layout.drawer_with_fragment_activity);
        mTitle = mDrawerTitle = getTitle();

        createDrawer();
    }

    private void createDrawer() {
        // load slide menu items
        mNavMenuTitles = Arrays.asList(getResources().getStringArray(R.array.drawer_items));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        for (String title : mNavMenuTitles) {
            navDrawerItems.add(new NavDrawerItem(title));
        }

        // set click listener for menu items
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        NavDrawerListAdapter adapter = new NavDrawerListAdapter(this, navDrawerItems);
        mDrawerList.setAdapter(adapter);
    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    private void setDrawerToggle() {

        mDrawerToggle = new ActionBarDrawerToggle(
                this, 					/* host Activity */
                mDrawerLayout,			/* DrawerLayout object */
                R.drawable.ic_drawer, 	/* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,	/* "open drawer" description for accessibility */
                R.string.drawer_close	/* "close drawer" description for accessibility */
        ) {
            /* Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            /* Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate menu resource file.
	    getMenuInflater().inflate(R.menu.main, menu);

	    // Locate MenuItem with ShareActionProvider
	    MenuItem item = menu.findItem(R.id.menu_item_share);

	    // Fetch and store ShareActionProvider
	    mShareActionProvider = (ShareActionProvider) item.getActionProvider();
	    
	    // Create and set the share Intent
	    mShareActionProvider.setShareIntent(createShareIntent());
	    
	    // Return true to display menu
	    return true;
	}
	
	private Intent createShareIntent() {
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

		// Add data to the intent, the receiving app will decide what to do with it.
		intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_subject));
		intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_body));
		return intent;
	}
	
	// Call to update the share intent
	private void setShareIntent(Intent shareIntent) {
	    if (mShareActionProvider != null) {
	        mShareActionProvider.setShareIntent(shareIntent);
	    }
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/*
	 * Called when invalidateOptionsMenu() is triggered
	 */
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		// boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		// menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

    private void createDonateDialog(boolean fromDrawer) {
        DonateDialogFragment mDonateDialog = DonateDialogFragment.newInstance(fromDrawer);
        mDonateDialog.show(getFragmentManager(), DONATE_DIALOG_TAG);
    }


	/*
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	public void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		boolean ask = false;
		boolean produce = false;
        boolean donate = false;
		boolean shiurim = false;
		Bundle args;
		switch (position) {
            case 0:
                // donate dialog
                fragment = new CommonTermsFragment();
                donate = true;
                break;
			case 1:
				// produce_list
				produce = true;
				break;
			case 2:
				// common terms
				fragment = new CommonTermsFragment();
				break;
			case 3:
				// faqs
				fragment = new FAQFragment();
				break;
			case 4:
				// Halacha overview
                fragment = new PDFFragment();
                args = new Bundle();
                args.putString(PDFFragment.PDF, mOverviewName);
                fragment.setArguments(args);
                break;
            case 5:
                // detailed halachos
                fragment = new PDFFragment();
                args = new Bundle();
                args.putString(PDFFragment.PDF, mDetailedName);
                fragment.setArguments(args);
                break;
			case 6:
				// chart
				fragment = new ChartFragment();
				break;
			case 7:
				// shiurim
				fragment = new CommonTermsFragment();
				shiurim = true;
				break;
			case 8:
				// ask the rabbi
				fragment = new CommonTermsFragment();
				ask = true;
				break;
			case 9:
				// about
				fragment = new AboutFragment();
				break;
			default:
				break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment)
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
            if (position == mNavMenuTitles.indexOf(getResources().getString(R.string.donate_drawer)) ||
                    position == mNavMenuTitles.indexOf(getResources().getString(R.string.shiurim)) ||
                    position == mNavMenuTitles.indexOf(getResources().getString(R.string.rabbi)) ) {
                setTitle(mNavMenuTitles.get(mNavMenuTitles.indexOf(getResources().getString(R.string.terms))));
            } else {
                setTitle(mNavMenuTitles.get(position));
            }
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("Common Terms", "Error in creating fragment");
		}
				
		// placed here to force close drawer first
		if (ask) {
        	ask = false;
        	askTheRabbi();
        } else if (produce) {
        	produce = false;
        	produceList();
        } else if (shiurim) {
        	shiurim = false;
        	onlineShiruim();
        } else if (donate) {
            donate = false;
            createDonateDialog(true);
        }
	}
	
	private void askTheRabbi() {	
	    Intent intent = new Intent(Intent.ACTION_SENDTO);
	    intent.setType("plain/text");
	    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
	    intent.putExtra(Intent.EXTRA_EMAIL, new String[] { mRabbisEmailAddress });
	    intent.putExtra(Intent.EXTRA_SUBJECT, mSubject);
	    if (intent.resolveActivity(getPackageManager()) != null) {
	        startActivity(Intent.createChooser(intent, ""));
	    }
	}
	
	private void produceList() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	private void onlineShiruim() {
		Uri webpage = Uri.parse(mShiurimUrl);
	    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
	    if (intent.resolveActivity(getPackageManager()) != null) {
	        startActivity(Intent.createChooser(intent, ""));
	    }
	}
	
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
}