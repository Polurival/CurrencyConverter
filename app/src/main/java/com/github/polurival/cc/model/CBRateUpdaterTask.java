package com.github.polurival.cc.model;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.github.polurival.cc.AppContext;
import com.github.polurival.cc.R;
import com.github.polurival.cc.model.db.DBUpdaterTask;
import com.github.polurival.cc.util.Constants;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.EnumMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Polurival
 * on 26.03.2016.
 */
public class CBRateUpdaterTask
        extends AsyncTask<Void, Void, Boolean>
        implements RateUpdater {

    private static final String CBR_URL = Constants.CBR_URL;

    private Context appContext;
    private RateUpdaterListener rateUpdaterListener;
    private EnumMap<CharCode, Currency> currencyMap;

    @Override
    protected void onPreExecute() {
        appContext = AppContext.getContext();
        currencyMap = new EnumMap<>(CharCode.class);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            URL url = new URL(CBR_URL);
            URLConnection connection = url.openConnection();

            Document doc = parseXML(connection.getInputStream());

            fillCurrencyMapFromSource(doc);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            DBUpdaterTask dbUpdaterTask = new DBUpdaterTask();
            dbUpdaterTask.setRateUpdaterListener(rateUpdaterListener);
            dbUpdaterTask.setCurrencyMap(currencyMap);
            dbUpdaterTask.execute();
        } else {
            Toast.makeText(appContext, appContext.getString(R.string.update_error),
                    Toast.LENGTH_SHORT)
                    .show();
            rateUpdaterListener.stopRefresh();
        }
    }

    @Override
    public void setRateUpdaterListener(RateUpdaterListener rateUpdaterListener) {
        this.rateUpdaterListener = rateUpdaterListener;
    }

    @Override
    public void fillCurrencyMapFromSource(Document doc) {
        NodeList descNodes = doc.getElementsByTagName("Valute");

        for (int i = 0; i < descNodes.getLength(); i++) {
            NodeList currencyNodeList = descNodes.item(i).getChildNodes();
            CharCode charCode = null;
            String nominal = null;
            String value = null;

            for (int j = 0; j < currencyNodeList.getLength(); j++) {
                String nodeName = currencyNodeList.item(j).getNodeName();
                String textContent = currencyNodeList.item(j).getTextContent();

                if ("CharCode".equals(nodeName)) {
                    charCode = CharCode.valueOf(textContent);
                } else if ("Nominal".equals(nodeName)) {
                    nominal = textContent;
                } else if ("Value".equals(nodeName)) {
                    value = textContent.replace(',', '.');
                }

                if (charCode != null && nominal != null && value != null) {
                    currencyMap.put(charCode,
                            new Currency(Integer.valueOf(nominal), Double.valueOf(value)));
                    break;
                }
            }
        }
    }

    private Document parseXML(InputStream stream) throws Exception {
        DocumentBuilderFactory objDocumentBuilderFactory;
        DocumentBuilder objDocumentBuilder;
        Document doc;

        objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();
        doc = objDocumentBuilder.parse(stream);

        return doc;
    }

    @Override
    public String getDescription() {
        return AppContext.getContext().getString(R.string.cbr);
    }
}
