package com.github.polurival.cc.model.updater;

import android.content.Context;
import android.os.AsyncTask;

import com.github.polurival.cc.R;
import com.github.polurival.cc.model.CharCode;
import com.github.polurival.cc.model.Currency;
import com.github.polurival.cc.model.db.DBHelper;
import com.github.polurival.cc.model.db.DBReaderTask;
import com.github.polurival.cc.model.dto.SpinnersPositions;
import com.github.polurival.cc.util.AppPreferences;
import com.github.polurival.cc.util.Constants;
import com.github.polurival.cc.util.Logger;

import org.joda.time.LocalDateTime;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class CBRateUpdaterTask extends CommonRateUpdater {

    @Override
    protected Boolean doInBackground(Void... params) {
        Logger.logD(Logger.getTag(), "doInBackground");

        try {
            URL url = new URL(Constants.CBRF_URL);
            URLConnection connection = url.openConnection();

            Document doc = parseXML(connection.getInputStream());

            fillCurrencyMapFromSource(doc);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.logD(Logger.getTag(), "changes in source! handle it");
            return false;
        }
        return true;
    }

    @Override
    public void execute() {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public <T> void fillCurrencyMapFromSource(T doc) {
        Logger.logD(Logger.getTag(), "fillCurrencyMapFromSource");

        NodeList descNodes = ((Document) doc).getElementsByTagName(Constants.CURRENCY_NODE_LIST);

        for (int i = 0; i < descNodes.getLength(); i++) {
            NodeList currencyNodeList = descNodes.item(i).getChildNodes();
            CharCode charCode = null;
            String nominal = null;
            String rate = null;

            for (int j = 0; j < currencyNodeList.getLength(); j++) {
                String nodeName = currencyNodeList.item(j).getNodeName();
                String textContent = currencyNodeList.item(j).getTextContent();

                if (Constants.CHAR_CODE_NODE.equals(nodeName)) {
                    charCode = CharCode.valueOf(textContent);
                } else if (Constants.NOMINAL_NODE.equals(nodeName)) {
                    nominal = textContent;
                } else if (Constants.RATE_NODE.equals(nodeName)) {
                    rate = textContent.replace(',', '.');
                }

                if (null != charCode && null != nominal && null != rate) {
                    currencyMap.put(charCode,
                            new Currency(Integer.valueOf(nominal), Double.valueOf(rate)));
                    break;
                }
            }
        }
    }

    private Document parseXML(InputStream stream) throws Exception {
        Logger.logD(Logger.getTag(), "parseXML");

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
        return appContext.getString(R.string.cb_rf);
    }

    @Override
    public void saveSelectedCurrencySpinnersPositions(Context context, int fromSpinnerSelectedPos, int toSpinnerSelectedPos) {
        AppPreferences.saveMainActivityCBRateUpdaterSpinnersPositions(context, fromSpinnerSelectedPos, toSpinnerSelectedPos);
    }

    @Override
    public void saveUpDateTime(Context context, LocalDateTime upDateTime) {
        AppPreferences.saveCBRateUpdaterUpDateTime(context, upDateTime);
    }

    @Override
    public void readDataFromDB(DBReaderTask dbReaderTask) {
        dbReaderTask.execute(DBHelper.COLUMN_NAME_CB_RF_SOURCE,
                DBHelper.COLUMN_NAME_CB_RF_NOMINAL,
                DBHelper.COLUMN_NAME_CB_RF_RATE);
    }

    @Override
    public LocalDateTime loadUpDateTime(Context context) {
        return AppPreferences.loadCBRateUpdaterUpDateTime(context);
    }

    @Override
    public int getDecimalScale() {
        return 4;
    }

    @Override
    public SpinnersPositions loadSpinnersPositions(Context context) {
        return AppPreferences.loadMainActivityCBRateUpdaterSpinnersPositions(context);
    }
}
