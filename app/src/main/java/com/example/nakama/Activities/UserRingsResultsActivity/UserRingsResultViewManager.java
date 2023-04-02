package com.example.nakama.Activities.UserRingsResultsActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nakama.Activities.TopBar.TopBarViewManager;
import com.example.nakama.AttemptDetailsCard.AttemptDetailsCard;
import com.example.nakama.DataBase.AppDatabase;
import com.example.nakama.DataBase.Entities.UserScores.UserScore;
import com.example.nakama.DataBase.Entities.Users.Users;
import com.example.nakama.R;
import com.example.nakama.Utils.Dictionary;

public class UserRingsResultViewManager {

    private final AppCompatActivity activity;
    private final LinearLayout userScoreScrollView;
    private final AppDatabase db;
    public UserRingsResultViewManager(AppCompatActivity activity) {
        this.activity = activity;
        userScoreScrollView = activity.findViewById(R.id.userScoreScrollView);
        db = AppDatabase.getInstance(activity);
    }

    public void setViewToDefaultState(String difficulty, String ring, Users user){
        new TopBarViewManager(activity, user, difficulty, ring);

        for(String ringName : Dictionary.Rings.ringsList){
            UserScore userScore = db.userScoresDao().getUserScore(user.uid, difficulty, ringName);
            View view = LayoutInflater.from(activity).inflate(R.layout.card_ring_results, userScoreScrollView, false);
            userScoreScrollView.addView(view);
            AttemptDetailsCard attemptDetailsCard = new AttemptDetailsCard(view, activity, difficulty);
            attemptDetailsCard.setCardContent(ringName, userScore, getDisqualificationReasonIfAny(difficulty, user));
        }
    }

    public String getDisqualificationReasonIfAny(String difficulty, Users user){
        for(String ringName : Dictionary.Rings.ringsList){
            UserScore userScore = db.userScoresDao().getUserScore(user.uid, difficulty, ringName);
            if(userScore != null){
                if(userScore.disqualification_reason != null){
                    return userScore.disqualification_reason;
                }
            }
        }
        return null;
    }
}
