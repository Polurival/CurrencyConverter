package com.polurival.cuco.strategies;

import android.graphics.drawable.Drawable;
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
public class CBRateUpdater extends AsyncTask<Void, Void, EnumMap<ValuteCharCode, Valute>> implements RateUpdater {

    public static final String CBR_URL = AppContext.getContext().getString(R.string.cbr_url);

    private EnumMap<ValuteCharCode, Valute> valuteMap = new EnumMap<>(ValuteCharCode.class);

    @Override
    protected EnumMap<ValuteCharCode, Valute> doInBackground(Void... params) {
        try {
            URL url = new URL(CBR_URL);
            URLConnection connection = url.openConnection();

            Document doc = parseXML(connection.getInputStream());

            fillValuteMap(doc);
        } catch (Exception e) {
        }
        return valuteMap;
    }

    @Override
    protected void onPostExecute(EnumMap<ValuteCharCode, Valute> result) {
        super.onPostExecute(result);
        MainActivity.getInstance().setValuteMap(result);
        MainActivity.getInstance().initSpinners();

        if (valuteMap.size() == 0) {
            Toast.makeText(AppContext.getContext(),
                    AppContext.getContext().getString(R.string.update_error),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void fillValuteMap(Document doc) {
        NodeList descNodes = doc.getElementsByTagName("Valute");

        for (int i = 0; i < descNodes.getLength(); i++) {
            NodeList valuteNodeList = descNodes.item(i).getChildNodes();
            ValuteCharCode charCode = null;
            String nominal = null;
            String value = null;

            for (int j = 0; j < valuteNodeList.getLength(); j++) {
                String nodeName = valuteNodeList.item(j).getNodeName();
                String textContent = valuteNodeList.item(j).getTextContent();

                if ("CharCode".equals(nodeName)) {
                    charCode = ValuteCharCode.valueOf(textContent);
                } else if ("Nominal".equals(nodeName)) {
                    nominal = textContent;
                } else if ("Value".equals(nodeName)) {
                    value = textContent.replace(',', '.');
                }

                if (charCode != null && nominal != null && value != null) {
                    valuteMap.put(charCode, new Valute(nominal, value));
                    break;
                }
            }
        }
        if (!valuteMap.containsKey(ValuteCharCode.RUB)) {
            valuteMap.put(ValuteCharCode.RUB, new Valute("1", "1.0"));
        }
    }

    @Override
    public EnumMap<ValuteCharCode, Valute> getValuteMap() {
        return valuteMap;
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
