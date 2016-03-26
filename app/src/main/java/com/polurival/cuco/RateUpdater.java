package com.polurival.cuco;

import android.os.AsyncTask;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Polurival
 * on 26.03.2016.
 */
public class RateUpdater extends AsyncTask<Void, Void, Void> {

    private MainActivity mainActivity;
    private String rateText;

    public RateUpdater(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            URL url = new URL("http://www.cbr.ru/scripts/XML_daily.asp");
            URLConnection connection = url.openConnection();

            Document doc = parseXML(connection.getInputStream());
            NodeList descNodes = doc.getElementsByTagName("Valute");

            setDollarValueToRateText(descNodes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        TextView tvResult = mainActivity.getTvResult();
        tvResult.setText(rateText);
        mainActivity.setTvResult(tvResult);
    }

    private Document parseXML(InputStream stream) throws Exception {
        DocumentBuilderFactory objDocumentBuilderFactory = null;
        DocumentBuilder objDocumentBuilder = null;
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

    private void setDollarValueToRateText(NodeList descNodes) {
        for (int i = 0; i < descNodes.getLength(); i++) {
            NamedNodeMap ids = descNodes.item(i).getAttributes();
            if ("R01235".equals(ids.getNamedItem("ID").getNodeValue())) {
                NodeList valuteNodeList = descNodes.item(i).getChildNodes();
                for (int j = 0; j < valuteNodeList.getLength(); j++) {
                    if ("Value".equals(valuteNodeList.item(j).getNodeName())) {
                        String dollarValue = valuteNodeList.item(j).getTextContent().replace(',', '.').substring(0, 5);
                        rateText = "1руб = " + dollarValue + "$";
                    }
                }
            }
        }
    }
}
