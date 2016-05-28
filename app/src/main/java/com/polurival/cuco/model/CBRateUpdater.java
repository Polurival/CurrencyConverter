package com.polurival.cuco.model;

import android.os.AsyncTask;
import android.widget.Toast;

import com.polurival.cuco.AppContext;
import com.polurival.cuco.MainActivity;
import com.polurival.cuco.R;

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
public class CBRateUpdater extends AsyncTask<Void, Void, EnumMap<CurrencyCharCode, Currency>> implements RateUpdater {

    public static final String CBR_URL = AppContext.getContext().getString(R.string.cbr_url);

    private EnumMap<CurrencyCharCode, Currency> currencyMap = new EnumMap<>(CurrencyCharCode.class);

    @Override
    protected EnumMap<CurrencyCharCode, Currency> doInBackground(Void... params) {
        try {
            URL url = new URL(CBR_URL);
            URLConnection connection = url.openConnection();

            Document doc = parseXML(connection.getInputStream());

            fillCurrencyMap(doc);
        } catch (Exception e) {
        }
        return currencyMap;
    }

    @Override
    protected void onPostExecute(EnumMap<CurrencyCharCode, Currency> result) {
        super.onPostExecute(result);

        MainActivity instance = MainActivity.getInstance();
        instance.setCurrencyMap(result);
        instance.initSpinners();

        if (currencyMap.size() == 0) {
            Toast.makeText(AppContext.getContext(),
                    AppContext.getContext().getString(R.string.update_error),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void fillCurrencyMap(Document doc) {
        NodeList descNodes = doc.getElementsByTagName("Valute");

        for (int i = 0; i < descNodes.getLength(); i++) {
            NodeList currencyNodeList = descNodes.item(i).getChildNodes();
            CurrencyCharCode charCode = null;
            String nominal = null;
            String value = null;

            for (int j = 0; j < currencyNodeList.getLength(); j++) {
                String nodeName = currencyNodeList.item(j).getNodeName();
                String textContent = currencyNodeList.item(j).getTextContent();

                if ("CharCode".equals(nodeName)) {
                    charCode = CurrencyCharCode.valueOf(textContent);
                } else if ("Nominal".equals(nodeName)) {
                    nominal = textContent;
                } else if ("Value".equals(nodeName)) {
                    value = textContent.replace(',', '.');
                }

                if (charCode != null && nominal != null && value != null) {
                    currencyMap.put(charCode, new Currency(nominal, value));
                    break;
                }
            }
        }
        if (!currencyMap.containsKey(CurrencyCharCode.RUB)) {
            currencyMap.put(CurrencyCharCode.RUB, new Currency("1", "1.0"));
        }
    }

    @Override
    public EnumMap<CurrencyCharCode, Currency> getCurrencyMap() {
        return currencyMap;
    }

    private Document parseXML(InputStream stream) throws Exception {
        DocumentBuilderFactory objDocumentBuilderFactory;
        DocumentBuilder objDocumentBuilder;
        Document doc = null;

        try {
            objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();

            doc = objDocumentBuilder.parse(stream);
        } catch (Exception e) {
            throw e;
        }

        return doc;
    }

    @Override
    public String getName() {
        return AppContext.getContext().getString(R.string.cbr);
    }
}
