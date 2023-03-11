package com.example.nakama.Activities.TopBar;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.nakama.DataBase.Entities.Users.Users;
import com.example.nakama.R;
import com.example.nakama.Utils.Dictionary;
import com.google.android.material.imageview.ShapeableImageView;

public class TopBarViewManager {
    private final ConstraintLayout topBarLayout;
    private final ShapeableImageView difficultyImageView;

    public TopBarViewManager(AppCompatActivity activity, Users user, String difficulty, String ring) {
        topBarLayout = activity.findViewById(R.id.topBarLayout);
        difficultyImageView = activity.findViewById(R.id.topBarDifficultyImage);
        TextView difficultyTextView = activity.findViewById(R.id.topBarDifficultyTextView);
        TextView ringDetailsTextView = activity.findViewById(R.id.topBarRingDetailsTextView);
        TextView userDetailsTextView = activity.findViewById(R.id.topBarUserDetailsTextView);

        String displayedDifficulty = String.format("Poziom %s", difficulty);
        String displayedUserName = String.format("%s %s i %s", user.userName, user.usersSurname, user.dogName);
        userDetailsTextView.setText(displayedUserName);
        difficultyTextView.setText(displayedDifficulty);
        ringDetailsTextView.setText(ring);
        configureViewColors(difficulty);
    }

    public void configureViewColors(String difficulty){
        switch (difficulty){
            case Dictionary.Difficulty.Basic.NAME:
                difficultyImageView.setImageResource(R.drawable.basic_level_icon);
                changeLayoutColors(R.color.basic_difficulty_color);
                break;
            case Dictionary.Difficulty.Advanced.NAME:
                difficultyImageView.setImageResource(R.drawable.advanced_level_icon);
                changeLayoutColors(R.color.advanced_difficulty_color);
                break;
        }
    }

    public void changeLayoutColors(int colorId){
        topBarLayout.setBackgroundResource(colorId);
    }


}
