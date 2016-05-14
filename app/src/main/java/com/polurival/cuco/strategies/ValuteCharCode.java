package com.polurival.cuco.strategies;

import com.polurival.cuco.AppContext;
import com.polurival.cuco.R;

/**
 * Created by Polurival
 * on 10.04.2016.
 */
public enum ValuteCharCode {
    RUB {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.RUB);
        }
    },
    AUD {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.AUD);
        }
    },
    AZN {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.AZN);
        }
    },
    GBP {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.GBP);
        }
    },
    AMD {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.AMD);
        }
    },
    BYR {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.BYR);
        }
    },
    BGN {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.BGN);
        }
    },
    BRL {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.BRL);
        }
    },
    HUF {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.HUF);
        }
    },
    DKK {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.DKK);
        }
    },
    USD {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.USD);
        }
    },
    EUR {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.EUR);
        }
    },
    INR {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.INR);
        }
    },
    KZT {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.KZT);
        }
    },
    CAD {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.CAD);
        }
    },
    KGS {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.KGS);
        }
    },
    CNY {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.CNY);
        }
    },
    MDL {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.MDL);
        }
    },
    NOK {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.NOK);
        }
    },
    PLN {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.PLN);
        }
    },
    RON {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.RON);
        }
    },
    XDR {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.XDR);
        }
    },
    SGD {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.SGD);
        }
    },
    TJS {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.TJS);
        }
    },
    TRY {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.TRY);
        }
    },
    TMT {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.TMT);
        }
    },
    UZS {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.UZS);
        }
    },
    UAH {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.UAH);
        }
    },
    CZK {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.CZK);
        }
    },
    SEK {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.SEK);
        }
    },
    CHF {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.CHF);
        }
    },
    ZAR {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.ZAR);
        }
    },
    KRW {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.KRW);
        }
    },
    JPY {
        @Override
        public String getName() {
            return AppContext.getContext().getString(R.string.JPY);
        }
    };

    public abstract String getName();
}
