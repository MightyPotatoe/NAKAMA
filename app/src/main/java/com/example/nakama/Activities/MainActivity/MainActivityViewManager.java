package com.example.nakama.Activities.MainActivity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nakama.R;

public class MainActivityViewManager {

    private final AppCompatActivity activity;
    TextView basicLevelCardTitle;
    TextView basicLevelCardDescription;

    public Button getBasicLevelButton() {
        return basicLevelButton;
    }

    public Button getAdvancedLevelButton() {
        return advancedLevelButton;
    }

    Button basicLevelButton;
    ImageView basicLevelImage;

    TextView advancedLevelCardTitle;
    TextView advancedLevelCardDescription;
    Button advancedLevelButton;
    ImageView advancedLevelImage;



    public MainActivityViewManager(AppCompatActivity activity) {
        this.activity = activity;

        View basicLevelCardView = activity.findViewById(R.id.basicLevelCardView);
        View advancedLevelCardView = activity.findViewById(R.id.advancedLevelCardView);

        basicLevelCardTitle = basicLevelCardView.findViewById(R.id.difficultySelectionCardHeadline);
        basicLevelCardDescription = basicLevelCardView.findViewById(R.id.difficultySelectionCardDescription);
        basicLevelButton = basicLevelCardView.findViewById(R.id.ringSummaryNextButton);
        basicLevelImage = basicLevelCardView.findViewById(R.id.difficultySelectionCardImage);

        advancedLevelCardTitle = advancedLevelCardView.findViewById(R.id.difficultySelectionCardHeadline);
        advancedLevelCardDescription = advancedLevelCardView.findViewById(R.id.difficultySelectionCardDescription);
        advancedLevelButton = advancedLevelCardView.findViewById(R.id.ringSummaryNextButton);
        advancedLevelImage = advancedLevelCardView.findViewById(R.id.difficultySelectionCardImage);
    }

    public void setCardsDefaultContent(){
        basicLevelCardTitle.setText(R.string.basic_level_title);
        basicLevelCardDescription.setText(R.string.basic_level_description);
        basicLevelImage.setBackgroundResource(R.color.basic_difficulty_color);
        basicLevelImage.setImageResource(R.drawable.basic_level_icon);
        basicLevelButton.setBackgroundColor(activity.getColor(R.color.basic_theme_light_primary));

        advancedLevelCardTitle.setText(R.string.advanced_level_title);
        advancedLevelCardDescription.setText(R.string.advanced_level_description);
        advancedLevelImage.setBackgroundResource(R.color.advanced_difficulty_color);
        advancedLevelImage.setImageResource(R.drawable.advanced_level_icon);
        advancedLevelButton.setBackgroundColor(activity.getColor(R.color.advanced_theme_light_primary));
    }
}
