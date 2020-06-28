package com.fiek.hitchhikerkosova.models;

public class LanguageModel {
    private String languageTxt;
    private int flagImg;

    public LanguageModel(String languageTxt, int flagImg) {
        this.languageTxt = languageTxt;
        this.flagImg = flagImg;
    }

    public String getLanguageTxt() {
        return languageTxt;
    }

    public int getFlagImg() {
        return flagImg;
    }
}
