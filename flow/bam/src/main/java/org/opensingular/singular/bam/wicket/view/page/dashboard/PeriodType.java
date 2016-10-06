/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.bam.wicket.view.page.dashboard;

import java.time.Period;

public enum PeriodType {

    WEEKLY("weekly", Period.ofWeeks(-1)),
    MONTHLY("monthly", Period.ofMonths(-1)),
    YEARLY("yearly", Period.ofYears(-1));

    private String name;
    private Period period;

    private PeriodType(String name, Period period) {
        this.name = name;
        this.period = period;
    }

    public static PeriodType valueOfName(String name) {
        for (PeriodType periodType : PeriodType.values()) {
            if (periodType.getName().equalsIgnoreCase(name)) {
                return periodType;
            }
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public Period getPeriod() {
        return period;
    }
}
