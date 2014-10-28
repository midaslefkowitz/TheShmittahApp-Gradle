package com.theshmittahapp.android.views.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.theshmittahapp.android.R;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

public class DonateDialogFragment extends DialogFragment {

    private final int TOTAL_NEVER_DAYS = 20;
    private final int TOTAL_NEVER_ENTRIES = 20;
    private final int TOTAL_NOT_NOW_DAYS = 5;
    private final int TOTAL_NOT_NOW_ENTRIES = 5;
    private final int TOTAL_FREE_DAYS = 5;
    private final int TOTAL_FREE_ENTRIES = 5;

    private final String USER_DONATED = "user donated";
    private final String CLICKED_NEVER = "clicked never";
    private final String CLICKED_NOT_NOW = "clicked not now";
    private final String NEVER_CLICKED_DATE = "never clicked date";
    private final String NEVER_ENTRIES_REMAINING = "never entries remaining";
    private final String NOT_NOW_CLICKED_DATE = "not now clicked date";
    private final String NOT_NOW_ENTRIES_REMAINING = "not now entries remaining";
    private final String FREE_DATE = "free date";
    private final String FREE_ENTRIES_REMAINING = "free entries remaining";
    private final String TAG = "Donate Dialog";

    private SharedPreferences mPrefs;
    private int mNeverRemaining;
    private int mNotNowRemaining;
    private int mFreeRemaining;
    private LayoutInflater mInflater;

    private String mPaypalUser = "theShmittahApp@gmail.com";
    private String mPaypalCurrencyCode = "USD";

    public DonateDialogFragment() {  }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        firstEntranceInit();

        logEntries();

        if (!displayDialog()) {
            //return null;
        }

        View v = getCustomView();

        Dialog alertDialog = createDialog(v);

        alertDialog = customizeButtons(alertDialog);

        alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return alertDialog;
    }

    private void firstEntranceInit() {
        if(!mPrefs.getBoolean("firstTimeDonate", false)) {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean("firstTimeDonate", true);
            editor.putString(FREE_DATE, new DateTime().plusDays(TOTAL_FREE_DAYS).toString());
            editor.commit();
        }
    }

    private View getCustomView() {
        mInflater = getActivity().getLayoutInflater();
        View v = mInflater.inflate(R.layout.fragment_donate_dialog, null);
        ImageView paypal = (ImageView) v.findViewById(R.id.donate_paypal);
        paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donatePayPalOnClick(v);
            }
        });
        return v;
    }

    private Dialog createDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(v)
                //.setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.never_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Never
                        resetNeverValues();
                    }
                })
                .setNegativeButton(R.string.not_now_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Not Now
                        resetNotNowValues();
                    }
                });
        //builder.setCustomTitle(mInflater.inflate(R.layout.dialog_title_layout, null));

        return (builder.create());
    }

    private Dialog customizeButtons(Dialog alertDialog) {

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog)
                        .getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setBackgroundResource(R.drawable.button_custom);
                positiveButton.setTextColor(getResources().getColor(R.color.custom_theme_color));
                positiveButton.setTextSize(getResources().getDimension(R.dimen.textsize));


                Button negativeButton = ((AlertDialog) dialog)
                        .getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setBackgroundResource(R.drawable.button_custom);
                negativeButton.setTextColor(getResources().getColor(R.color.custom_theme_color));
                negativeButton.setTextSize(getResources().getDimension(R.dimen.textsize));
            }
        });
        return alertDialog;
    }

    private void resetNeverValues() {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(NEVER_ENTRIES_REMAINING, TOTAL_NEVER_ENTRIES);
        editor.putString(NEVER_CLICKED_DATE, new DateTime().plusDays(TOTAL_NEVER_DAYS).toString());
        editor.commit();
    }

    private void resetNotNowValues() {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(NOT_NOW_ENTRIES_REMAINING, TOTAL_NOT_NOW_ENTRIES);
        editor.putString(NOT_NOW_CLICKED_DATE, new DateTime().plusDays(TOTAL_NOT_NOW_DAYS).toString());
        editor.commit();
    }

    private void logEntries() {
        SharedPreferences.Editor editor = mPrefs.edit();

        // Log free entries
        mFreeRemaining = (mPrefs.getInt(FREE_ENTRIES_REMAINING, TOTAL_FREE_ENTRIES)) - 1;
        editor.putInt(FREE_ENTRIES_REMAINING, mFreeRemaining);

        // Log never entries
        mNeverRemaining = (mPrefs.getInt(NEVER_ENTRIES_REMAINING, TOTAL_NEVER_ENTRIES)) - 1;
        editor.putInt(NEVER_ENTRIES_REMAINING, mNeverRemaining);

        // Log not now entries
        mNotNowRemaining = (mPrefs.getInt(NOT_NOW_ENTRIES_REMAINING, TOTAL_NOT_NOW_ENTRIES)) - 1;
        editor.putInt(NOT_NOW_ENTRIES_REMAINING, mNotNowRemaining);

        editor.commit();
    }

    private boolean displayDialog() {
        if (userDonated()) {
            return false;
        }

        if (freeUsesRemaining()) {
            return false;
        }

        if (neverClickedRecently()) {
            return false;
        }

        if (notNowClickedRecently()) {
            return false;
        }
        return true;
    }

    private boolean freeUsesRemaining() {
        DateTime today = new DateTime();
        DateTime freeDate = DateTime.parse(mPrefs.getString(FREE_DATE,
                today.plusDays(TOTAL_FREE_DAYS).toString()));

        return ( ( (Seconds.secondsBetween(freeDate, today)).getSeconds() < 0) &&
                (mPrefs.getInt(NEVER_ENTRIES_REMAINING, TOTAL_NEVER_DAYS) > 0));
    }

    /**
     * @return true if the user has donated
     */
    private boolean userDonated() {
        return mPrefs.getBoolean(USER_DONATED, false);
    }

    /**
     * @return true if "never" has been clicked recently
     */
    private boolean neverClickedRecently() {
        // if "never" wasn't clicked don't display popup
        if (mPrefs.getBoolean(CLICKED_NEVER, false)) {
            return false;
        }
        DateTime today = new DateTime();
        // if have a saved date to show popup use that
        // by default set date to show popup for total days from now
        DateTime neverClickedDate = DateTime.parse(mPrefs.getString(NEVER_CLICKED_DATE,
                today.plusDays(TOTAL_NEVER_DAYS).toString()));

        return ( ( (Seconds.secondsBetween(neverClickedDate, today)).getSeconds() < 0) &&
                (mPrefs.getInt(NEVER_ENTRIES_REMAINING, TOTAL_NEVER_DAYS) > 0) );
    }

    /**
     * @return true if "not now" has been clicked recently
     */
    private boolean notNowClickedRecently() {
        // check if "not now" has been clicked at all
        if (!mPrefs.getBoolean(CLICKED_NOT_NOW, true)) {
            return false;
        }
        DateTime today = new DateTime();
        // if have a saved date to show popup use that
        // by default set date to show popup for total days from now
        DateTime notNowClickedDate = DateTime.parse(mPrefs.getString(NOT_NOW_CLICKED_DATE,
                today.plusDays(TOTAL_NOT_NOW_DAYS).toString()));

        return ( ( (Seconds.secondsBetween(notNowClickedDate, today)).getSeconds() < 0) &&
                (mPrefs.getInt(NOT_NOW_ENTRIES_REMAINING, TOTAL_NOT_NOW_DAYS) > 0) );
    }

    public void donatePayPalOnClick(View view) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https").authority("www.paypal.com").path("cgi-bin/webscr");
        uriBuilder.appendQueryParameter("cmd", "_donations");

        uriBuilder.appendQueryParameter("business", mPaypalUser);
        uriBuilder.appendQueryParameter("lc", "US");
        uriBuilder.appendQueryParameter("item_name", getString(R.string.donation_paypal_item));
        uriBuilder.appendQueryParameter("no_note", "1");
        // uriBuilder.appendQueryParameter("no_note", "0");
        // uriBuilder.appendQueryParameter("cn", "Note to the developer");
        uriBuilder.appendQueryParameter("no_shipping", "1");
        uriBuilder.appendQueryParameter("currency_code", mPaypalCurrencyCode);
        Uri payPalUri = uriBuilder.build();

        // Start your favorite browser
        try {
            Intent viewIntent = new Intent(Intent.ACTION_VIEW, payPalUri);
            startActivity(viewIntent);
        } catch (ActivityNotFoundException e) {
            openDialog(android.R.drawable.ic_dialog_alert, R.string.donations__alert_dialog_title,
                    getString(R.string.donations__alert_dialog_no_browser));
        }
    }

    void openDialog(int icon, int title, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setIcon(icon);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(true);
        dialog.setNeutralButton(R.string.donations__button_close,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
        dialog.show();
    }
}
