package com.example.nakama.Activities.OverallImpressionActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nakama.Activities.TopBar.TopBarViewManager;
import com.example.nakama.DataBase.AppDatabase;
import com.example.nakama.R;
import com.example.nakama.SharedPreferences.AppPreferences;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class OverallImpressionViewManager {

    AppCompatActivity activity;
    LinearLayout impressionsLayout;

    public OverallImpressionViewManager(AppCompatActivity activity) {
        this.activity = activity;
        AppPreferences appPreferences = new AppPreferences(activity.getSharedPreferences(AppPreferences.NAME, Context.MODE_PRIVATE));
        AppDatabase db = AppDatabase.getInstance(activity);
        impressionsLayout = activity.findViewById(R.id.impressionsLinearLayout);
        TopBarViewManager topBarViewManager = new TopBarViewManager(activity, db.getUser(appPreferences.getUserId()), appPreferences.getDifficulty(), appPreferences.getActiveRing());
    }

    public void addNewImpression(){
        View view = LayoutInflater.from(activity).inflate(R.layout.overall_impression_item, impressionsLayout, false);
        impressionsLayout.addView(view);
        MaterialButton removeButton = view.findViewById(R.id.removeImpressionButton);
        removeButton.setOnClickListener(view1 -> {
            if(impressionsLayout.getChildCount() > 1){
                impressionsLayout.removeView(view);
                if(impressionsLayout.getChildCount()==1){
                    impressionsLayout.getChildAt(0).findViewById(R.id.removeImpressionButton).setVisibility(View.INVISIBLE);
                }
            }
            activity.findViewById(R.id.addImpressionButton).setVisibility(View.VISIBLE);
        });
        if(impressionsLayout.getChildCount()==1){
            impressionsLayout.getChildAt(0).findViewById(R.id.removeImpressionButton).setVisibility(View.INVISIBLE);
        }
        else if(impressionsLayout.getChildCount()==2){
            impressionsLayout.getChildAt(0).findViewById(R.id.removeImpressionButton).setVisibility(View.VISIBLE);
        }
        else{
            if(impressionsLayout.getChildCount()==4){
                activity.findViewById(R.id.addImpressionButton).setVisibility(View.GONE);
            }
        }
    }

    public void resetErrors(){
        for(int i = 0; i < impressionsLayout.getChildCount(); i++){
            TextInputLayout descriptionBox = impressionsLayout.getChildAt(i).findViewById(R.id.impressionEditTextLayout);
            descriptionBox.setErrorEnabled(false);
            TextInputLayout scoreBox = impressionsLayout.getChildAt(i).findViewById(R.id.minusScoreEditTextLayout);
            scoreBox.setErrorEnabled(false);
        }
    }
}
