package com.yourdomain;

import java.time.LocalDate;

public class WasteItem {
    private LocalDate date;
    private String item;
    private int amountWasted;

    public WasteItem(LocalDate date, String item, int amountWasted) {
        this.date = date;
        this.item = item;
        this.amountWasted = amountWasted;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getAmountWasted() {
        return amountWasted;
    }

    public void setAmountWasted(int amountWasted) {
        this.amountWasted = amountWasted;
    }
}

