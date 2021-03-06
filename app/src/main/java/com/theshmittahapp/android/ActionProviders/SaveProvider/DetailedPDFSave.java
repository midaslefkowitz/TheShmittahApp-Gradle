package com.theshmittahapp.android.ActionProviders.SaveProvider;


import android.content.Context;

public class DetailedPDFSave extends SaveItem {

    public DetailedPDFSave (Context context) {
        super(context);
    }

    @Override
    public String getEmailBody() {
        StringBuilder body  = new StringBuilder();
        body.append("Here is a link to the Detailed Halachos of Shmittah ebook found in The Shmittah App. Just click to open it or right-click and \"Save link as...\" to download it.");
        body.append('\n');
        body.append("<a href=\"https://s3.eu-central-1.amazonaws.com/shmittahappfiles/Detailed+Halachos+of+Shmittah.pdf\">Detailed Halachos of Shmittah (PDF)</a>");
        body.append('\n');
        body.append("</br>");
        body.append('\n');
        body.append( "Thanks for using <a href=\"http://www.TheShmittahApp.com\">The Shmittah App</a>");

        return body.toString();
    }

    @Override
    public String getEmailSubject() {
        return "Detailed Halachos of Shmittah ebook (PDF)";
    }
}
