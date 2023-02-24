package vn.elca.demo.model.enumType;

public enum ShopAvailabilityLevel {
    UNKNOWN, NOT_VISIBLE, SOLD_OUT, OUTSIDE_SALE_PERIOD, GOOD, OPTIONABLE, LIMITED;

    public boolean isVisible() {
        return true;
    }

    public boolean isVisibleExcludingSalesPeriods() {
        return true;
    }

    public boolean displayAsSoldOut() {
        return true;
    }
}
