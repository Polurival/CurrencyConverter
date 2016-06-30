package com.github.polurival.cc.model.updater;

import com.github.polurival.cc.R;
import com.github.polurival.cc.model.CharCode;
import com.github.polurival.cc.model.Currency;
import com.github.polurival.cc.util.Constants;
import com.github.polurival.cc.util.Logger;

import org.w3c.dom.Document;
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
public class CBRateUpdaterTask extends CommonRateUpdater {

    @Override
    protected Boolean doInBackground(Void... params) {
        Logger.logD(Logger.getTag(), "doInBackground");

        try {
            URL url = new URL(Constants.CBR_URL);
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
}
