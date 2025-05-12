package com.alxsshv.bank_card_system_service.configuration;

import lombok.Getter;

@Getter
public abstract class AppConstants {
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_PAGE_SORT_BY = "id";
    public static final String DEFAULT_PAGE_SORT_DIR = "ASC";

    public static final int DECIMAL_PRECISION = 12;
    public static final int DECIMAL_SCALE = 2;
}
