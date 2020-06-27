package com.fiek.hitchhikerkosova.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fiek.hitchhikerkosova.R;
import com.fiek.hitchhikerkosova.models.LanguageModel;

import java.util.ArrayList;

public class LanguageAdapter extends ArrayAdapter<LanguageModel> {
    public LanguageAdapter(Context context, ArrayList<LanguageModel> languageModels) {
        super(context, 0, languageModels);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }
    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.language_spinner_row, parent, false);
        }
        ImageView ivFlag = convertView.findViewById(R.id.ivFlag);
        TextView tvLang = convertView.findViewById(R.id.tvLang);
        LanguageModel languageModel = getItem(position);
        if (languageModel != null) {
            ivFlag.setImageResource(languageModel.getFlagImg());
            tvLang.setText(languageModel.getLanguageTxt());
        }
        return convertView;
    }
}

