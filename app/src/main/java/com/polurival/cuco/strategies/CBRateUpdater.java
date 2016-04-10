package com.polurival.cuco.strategies;

import android.os.AsyncTask;

import com.polurival.cuco.MainActivity;

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
public class CBRateUpdater extends AsyncTask<Void, Void, Void> implements RateUpdater {

    public static final String CBR_URL = "http://www.cbr.ru/scripts/XML_daily.asp";
    private MainActivity mainActivity;
    private EnumMap<ValuteCharCode, Valute> valuteMap = new EnumMap<>(ValuteCharCode.class);

    public CBRateUpdater(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            URL url = new URL(CBR_URL);
            URLConnection connection = url.openConnection();

            Document doc = parseXML(connection.getInputStream());
            NodeList descNodes = doc.getElementsByTagName("Valute");

            fillValuteMap(descNodes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        mainActivity.setValuteMap(valuteMap);

    }

    @Override
    public void fillValuteMap(NodeList descNodes) {
        for (int i = 0; i < descNodes.getLength(); i++) {
            NodeList valuteNodeList = descNodes.item(i).getChildNodes();
            ValuteCharCode charCode = null;
            String nominal = null;
            String value = null;
            for (int j = 0; j < valuteNodeList.getLength(); j++) {
                String nodeName = valuteNodeList.item(j).getNodeName();
                String textContent = valuteNodeList.item(j).getTextContent();
                if ("CharCode".equals(nodeName)) {
                    charCode =
                            ValuteCharCode.valueOf(textContent);
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
        valuteMap.put(ValuteCharCode.RUB, new Valute("1", "1.0"));
    }

    @Override
    public EnumMap<ValuteCharCode, Valute> getValuteMap() {
        return valuteMap;
    }

    private Document parseXML(InputStream stream) throws Exception {
        DocumentBuilderFactory objDocumentBuilderFactory;
        DocumentBuilder objDocumentBuilder;
        Document doc;
        try {
            objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();

            doc = objDocumentBuilder.parse(stream);
        } catch (Exception e) {
            throw e;
        }

        return doc;
    }
}
