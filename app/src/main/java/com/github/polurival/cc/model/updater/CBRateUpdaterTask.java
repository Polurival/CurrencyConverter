package com.github.polurival.cc.model.updater;

import android.content.ContentValues;
import android.content.Context;

import com.github.polurival.cc.R;
import com.github.polurival.cc.model.CharCode;
import com.github.polurival.cc.model.dto.CurrenciesRelations;
import com.github.polurival.cc.model.dto.Currency;
import com.github.polurival.cc.model.db.DBOperations;
import com.github.polurival.cc.model.db.DBReaderTask;
import com.github.polurival.cc.model.dto.SpinnersPositions;
import com.github.polurival.cc.util.AppPreferences;
import com.github.polurival.cc.util.Logger;

import org.joda.time.LocalDateTime;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class CBRateUpdaterTask extends CommonRateUpdater {

    /**
     * See <a href="http://www.cbr.ru/scripts/XML_daily.asp">source</a>
     */
    private final static String CBRF_URL = "http://www.cbr.ru/scripts/XML_daily.asp";
    private final static String CURRENCY_NODE_LIST = "Valute";
    private final static String CHAR_CODE_NODE = "CharCode";
    private final static String NOMINAL_NODE = "Nominal";
    private final static String RATE_NODE = "Value";

    @Override
    protected Boolean doInBackground(Void... params) {
        Logger.logD(Logger.getTag(), "doInBackground");

        try {
            InputStream inputStream = getDataInputStream(CBRF_URL);
            if (inputStream != null) {
                Document xml = parseDataToXmlDocument(inputStream);
                if (xml != null) {
                    return fillCurrencyMap(xml);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.logD(Logger.getTag(), "can't connect or read from source!");
        }
        return false;
    }

    private Document parseDataToXmlDocument(InputStream inputStream) throws IOException {
        try {
            DocumentBuilderFactory objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();
            Document document = objDocumentBuilder.parse(inputStream);

            inputStream.close();
            return document;
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
            Logger.logD(Logger.getTag(), "can't parse - changes in source! handle it");
            return null;
        }
    }

    private boolean fillCurrencyMap(Document document) {
        Logger.logD(Logger.getTag(), "fillCurrencyMap");

        NodeList descNodes = document.getElementsByTagName(CURRENCY_NODE_LIST);

        for (int i = 0; i < descNodes.getLength(); i++) {
            NodeList currencyNodeList = descNodes.item(i).getChildNodes();
            CharCode charCode = null;
            String nominal = null;
            String rate = null;

            for (int j = 0; j < currencyNodeList.getLength(); j++) {
                String nodeName = currencyNodeList.item(j).getNodeName();
                String textContent = currencyNodeList.item(j).getTextContent();

                if (CHAR_CODE_NODE.equals(nodeName)) {
                    charCode = CharCode.valueOf(textContent);
                } else if (NOMINAL_NODE.equals(nodeName)) {
                    nominal = textContent;
                } else if (RATE_NODE.equals(nodeName)) {
                    rate = textContent.replace(',', '.');
                }

                if (null != charCode && null != nominal && null != rate) {
                    currencyMap.put(charCode,
                            new Currency(Integer.valueOf(nominal), Double.valueOf(rate)));
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public String getDescription() {
        return appContext.getString(R.string.cb_rf);
    }

    @Override
    public void saveSelectedCurrencySpinnersPositions(Context context,
                                                      int fromSpinnerSelectedPos,
                                                      int toSpinnerSelectedPos) {
        AppPreferences.saveMainActivityCBRateUpdaterSpinnersPositions(context, fromSpinnerSelectedPos, toSpinnerSelectedPos);
    }

    @Override
    public void saveUpDateTime(Context context, LocalDateTime upDateTime) {
        AppPreferences.saveCBRateUpdaterUpDateTime(context, upDateTime);
    }

    @Override
    public void readDataFromDB(DBReaderTask dbReaderTask) {
        dbReaderTask.execute(DBOperations.getColumnsForReadForCBSource());
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

    @Override
    public void fillContentValuesForUpdatingColumns(ContentValues contentValues,
                                                    Currency currency) {
        DBOperations.fillContentValuesForUpdatingCbRfColumns(contentValues, currency);
    }

    @Override
    public BigDecimal calculateConversionResult(CurrenciesRelations currenciesRelations,
                                                BigDecimal enteredAmountOfMoney) {
        return currenciesRelations.calculateConversionResultForCB(enteredAmountOfMoney);
    }

    @Override
    public boolean isUpdateFromNetworkUnavailable() {
        return false;
    }
}
