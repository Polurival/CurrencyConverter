package com.polurival.cuco.strategies;

/**
 * Created by Polurival
 * on 10.04.2016.
 */
public enum ValuteCharCode {
    RUB {
        @Override
        public String getName() {
            return "Российский рубль";
        }
    },
    AUD {
        @Override
        public String getName() {
            return "Австралийский доллар";
        }
    },
    AZN {
        @Override
        public String getName() {
            return "Азербайджанский манат";
        }
    },
    GBP {
        @Override
        public String getName() {
            return "Фунт стерлингов";
        }
    },
    AMD {
        @Override
        public String getName() {
            return "Армянский драм";
        }
    },
    BYR {
        @Override
        public String getName() {
            return "Белорусский рубль";
        }
    },
    BGN {
        @Override
        public String getName() {
            return "Болгарский лев";
        }
    },
    BRL {
        @Override
        public String getName() {
            return "Бразильский реал";
        }
    },
    HUF {
        @Override
        public String getName() {
            return "Венгерский форинт";
        }
    },
    DKK {
        @Override
        public String getName() {
            return "Датская крона";
        }
    },
    USD {
        @Override
        public String getName() {
            return "Доллар США";
        }
    },
    EUR {
        @Override
        public String getName() {
            return "Евро";
        }
    },
    INR {
        @Override
        public String getName() {
            return "Индийских рупий";
        }
    },
    KZT {
        @Override
        public String getName() {
            return "Казахстанский тенге";
        }
    },
    CAD {
        @Override
        public String getName() {
            return "Канадский доллар";
        }
    },
    KGS {
        @Override
        public String getName() {
            return "Киргизский сом";
        }
    },
    CNY {
        @Override
        public String getName() {
            return "Китайский юань";
        }
    },
    MDL {
        @Override
        public String getName() {
            return "Молдавских лей";
        }
    },
    NOK {
        @Override
        public String getName() {
            return "Норвежская крона";
        }
    },
    PLN {
        @Override
        public String getName() {
            return "Польский злотый";
        }
    },
    RON {
        @Override
        public String getName() {
            return "Румынский лей";
        }
    },
    XDR {
        @Override
        public String getName() {
            return "СДР";
        }
    },
    SGD {
        @Override
        public String getName() {
            return "Сингапурский доллар";
        }
    },
    TJS {
        @Override
        public String getName() {
            return "Таджикский сомони";
        }
    },
    TRY {
        @Override
        public String getName() {
            return "Турецкая лира";
        }
    },
    TMT {
        @Override
        public String getName() {
            return "Туркменский манат";
        }
    },
    UZS {
        @Override
        public String getName() {
            return "Узбекский сум";
        }
    },
    UAH {
        @Override
        public String getName() {
            return "Украинская гривна";
        }
    },
    CZK {
        @Override
        public String getName() {
            return "Чешская крона";
        }
    },
    SEK {
        @Override
        public String getName() {
            return "Шведская крона";
        }
    },
    CHF {
        @Override
        public String getName() {
            return "Швейцарский франк";
        }
    },
    ZAR {
        @Override
        public String getName() {
            return "Южноафриканский рэнд";
        }
    },
    KRW {
        @Override
        public String getName() {
            return "Вон Республики Корея";
        }
    },
    JPY {
        @Override
        public String getName() {
            return "Японская иена";
        }
    };

    public abstract String getName();
}
