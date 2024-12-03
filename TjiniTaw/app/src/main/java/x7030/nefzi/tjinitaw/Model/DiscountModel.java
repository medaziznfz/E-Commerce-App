package x7030.nefzi.tjinitaw.Model;

public class DiscountModel {
    private String key;
    private int percent;
    private long untilDate;

    public DiscountModel() {
    }

    public DiscountModel(String key, int percent, long unitDate) {
        this.key = key;
        this.percent = percent;
        this.untilDate = unitDate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public long getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(long untilDate) {
        this.untilDate = untilDate;
    }
}

