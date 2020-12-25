package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.Dynamic;
import io.github.vipcxj.beanknife.runtime.annotations.InjectProperty;
import io.github.vipcxj.beanknife.runtime.annotations.NewViewProperty;

import java.util.Calendar;
import java.util.Date;

public class InheritedConfigBaseViewConfig {

    @NewViewProperty("type")
    public static String type(Object source) {
        return source.getClass().getCanonicalName();
    }

    @NewViewProperty("timestamp")
    public static Date timestamp() {
        return new Date();
    }

    @NewViewProperty("typeWithTimestamp")
    @Dynamic
    public static String typeWithTimestamp(@InjectProperty String type, @InjectProperty Date timestamp) {
        return type + timestamp.getTime();
    }

    private static Calendar currentDay() {
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR_OF_DAY, 0);
        return instance;
    }

    @NewViewProperty("today")
    @Dynamic
    public static Date today() {
        return currentDay().getTime();
    }

    @NewViewProperty("yesterday")
    @Dynamic
    public static Date yesterday() {
        Calendar instance = currentDay();
        instance.add(Calendar.DAY_OF_MONTH, -1);
        return instance.getTime();
    }

    @NewViewProperty("tomorrow")
    @Dynamic
    public static Date tomorrow() {
        Calendar instance = currentDay();
        instance.add(Calendar.DAY_OF_MONTH, 1);
        return instance.getTime();
    }
}
