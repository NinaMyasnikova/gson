package com.myasnikova.centralbank;

import java.util.Map;

class Root
{
    String Date;
    String PreviousDate;
    String PreviousURL;
    String Timestamp;

    private Map<String, Valute> Valute;

    public Map<String, com.myasnikova.centralbank.Valute> getValute() {
        return Valute;
    }

    public void setValute(Map<String, com.myasnikova.centralbank.Valute> valute) {
        Valute = valute;
    }
}