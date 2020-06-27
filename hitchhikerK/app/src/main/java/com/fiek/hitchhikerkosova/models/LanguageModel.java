package com.fiek.hitchhikerkosova.models;

public class LanguageModel {
    public String languageTxt;
    public int flagImg;

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
