package io.github.vipcxj.beanknife.jpa;

import io.github.vipcxj.beanknife.core.models.Property;

import java.util.List;

public class Helper {

    public static int findViewPropertyData(List<PropertyData> viewPropertyData, Property property, int startPos) {
        for (int i = startPos; i < viewPropertyData.size();  ++i) {
            PropertyData propertyData = viewPropertyData.get(i);
            if (propertyData.getTarget() == property) {
                return i;
            }
        }
        for (int i = 0; i < startPos; ++i) {
            PropertyData propertyData = viewPropertyData.get(i);
            if (propertyData.getTarget() == property) {
                return i;
            }
        }
        return -1;
    }
}
