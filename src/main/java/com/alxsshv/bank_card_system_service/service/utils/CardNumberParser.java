package com.alxsshv.bank_card_system_service.service.utils;

public class CardNumberParser {
    public static String getPublicNumber(String number) {
        number = number.replaceAll(" ", "");
        int visibleSymbolsLength = 4;
        int beginIndex = number.length() - visibleSymbolsLength;
        int endIndex = number.length();
        return number.substring(beginIndex, endIndex);
    }
}
