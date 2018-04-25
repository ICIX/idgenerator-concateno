package com.icix.model;

import java.util.List;

public interface IdGenerator {
    String LAST_ID = "LAST_ID";
    String HOUR_OVERLAY = "HOUR_OVERLAY";
    String DAY_OVERLAY = "DAY_OVERLAY";
    String YEAR_OVERLAY = "YEAR_OVERLAY";
    List<String> generate(int amount) throws RangeLimitException;
}
