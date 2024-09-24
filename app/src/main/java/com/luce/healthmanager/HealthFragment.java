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

        // 設置睡眠卡片的點擊事件
        LinearLayout sleepCard = view.findViewById(R.id.sleep_card);
        sleepCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "sleep");
                startActivity(intent);
            }
        });

        // 設置心律卡片的點擊事件
        LinearLayout heartCard = view.findViewById(R.id.heart_card);
        heartCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "heart");
                startActivity(intent);
            }
        });

        // 設置血氧卡片的點擊事件
        LinearLayout oxygenCard = view.findViewById(R.id.oxygen_card);
        oxygenCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "oxygen");
                startActivity(intent);
            }
        });

        // 設置血壓卡片的點擊事件
        LinearLayout bloodCard = view.findViewById(R.id.blood_card);
        bloodCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "blood");
                startActivity(intent);
            }
        });

        // 設置血糖卡片的點擊事件
        LinearLayout sugarCard = view.findViewById(R.id.sugar_card);
        sugarCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "sugar");
                startActivity(intent);
            }
        });

        // 設置步數卡片的點擊事件
        LinearLayout stepsCard = view.findViewById(R.id.steps_card);
        stepsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "steps");
                startActivity(intent);
            }
        });

        // 設置卡路里卡片的點擊事件
        LinearLayout caloriesCard = view.findViewById(R.id.calories_card);
        caloriesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "calories");
                startActivity(intent);
            }
        });

        // 設置站立次數卡片的點擊事件
        LinearLayout standCard = view.findViewById(R.id.stand_card);
        standCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "stand");
                startActivity(intent);
            }
        });

        // 設置壓力指數卡片的點擊事件
        LinearLayout pressureCard = view.findViewById(R.id.pressure_card);
        pressureCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "pressure");
                startActivity(intent);
            }
        });

        // 設置身高體重卡片的點擊事件
        LinearLayout heightCard = view.findViewById(R.id.height_card);
        heightCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "height");
                startActivity(intent);
            }
        });

        // 設置抽菸卡片的點擊事件
        LinearLayout smokeCard = view.findViewById(R.id.smoke_card);
        smokeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "smoke");
                startActivity(intent);
            }
        });

        // 設置喝酒卡片的點擊事件
        LinearLayout beerCard = view.findViewById(R.id.beer_card);
        beerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "beer");
                startActivity(intent);
            }
        });

        // 設置檳榔卡片的點擊事件
        LinearLayout leafCard = view.findViewById(R.id.leaf_card);
        leafCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra("CARD_TYPE", "leaf");
                startActivity(intent);
            }
        });

        return view;
    }
}
