package com.luce.healthmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

public class HealthFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health, container, false);

        // 設置心律卡片的點擊事件
        LinearLayout heartCard = view.findViewById(R.id.heart_card);
        heartCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "心律");
                startActivity(intent);
            }
        });

        // 設置血氧卡片的點擊事件
        LinearLayout oxygenCard = view.findViewById(R.id.oxygen_card);
        oxygenCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "血氧");
                startActivity(intent);
            }
        });

        // 設置血壓卡片的點擊事件
        LinearLayout bloodCard = view.findViewById(R.id.blood_card);
        bloodCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "血壓");
                startActivity(intent);
            }
        });

        // 設置血糖卡片的點擊事件
        LinearLayout sugarCard = view.findViewById(R.id.sugar_card);
        sugarCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "血糖");
                startActivity(intent);
            }
        });

        // 設置卡路里卡片的點擊事件
        LinearLayout caloriesCard = view.findViewById(R.id.calories_card);
        caloriesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "卡路里");
                startActivity(intent);
            }
        });


        // 設置身高體重卡片的點擊事件
        LinearLayout heightCard = view.findViewById(R.id.height_card);
        heightCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "身高體重");
                startActivity(intent);
            }
        });

        // 設置抽菸卡片的點擊事件
        LinearLayout smokeCard = view.findViewById(R.id.smoke_card);
        smokeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "抽菸");
                startActivity(intent);
            }
        });

        // 設置喝酒卡片的點擊事件
        LinearLayout beerCard = view.findViewById(R.id.beer_card);
        beerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "喝酒");
                startActivity(intent);
            }
        });

        // 設置檳榔卡片的點擊事件
        LinearLayout leafCard = view.findViewById(R.id.leaf_card);
        leafCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "檳榔");
                startActivity(intent);
            }
        });

        return view;
    }
}
