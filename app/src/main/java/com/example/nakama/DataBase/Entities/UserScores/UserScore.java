package com.example.nakama.DataBase.Entities.UserScores;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(primaryKeys = {"user_id", "difficulty", "ring"})
public class UserScore {

    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "difficulty")
    @NonNull
    public String difficulty;

    @ColumnInfo(name = "ring")
    @NonNull
    public String ring;

    @ColumnInfo(name = "samples_found")
    public int samplesFound;
    @ColumnInfo(name = "attempt_time")
    public String attemptTime;

    @ColumnInfo(name = "false_alarms")
    public int falseAlarms;

    @ColumnInfo(name = "treat_drop")
    public int treatDrop;

    @ColumnInfo(name = "defecation")
    public boolean defecation;

    @ColumnInfo(name = "score")
    public int score;

    @ColumnInfo(name = "overview")
    public String overview;

    @ColumnInfo(name = "disqualification_reason")
    public String disqualification_reason;

    public UserScore(int userId, @NonNull String difficulty, @NonNull String ring) {
        this.userId = userId;
        this.difficulty = difficulty;
        this.ring = ring;
        attemptTime = null;
        falseAlarms = 0;
        treatDrop = 0;
        defecation = false;
        score = 200;
        overview = null;
        samplesFound = 0;
        disqualification_reason = null;
    }

    @Ignore
    public UserScore(int userId, @NonNull String difficulty, @NonNull String ring, int score, String attemptTime, int falseAlarms, boolean defecation, int treatDrop, int samplesFound, String overview, String disqualification_reason) {
        this.userId = userId;
        this.difficulty = difficulty;
        this.ring = ring;
        this.samplesFound = samplesFound;
        this.attemptTime = attemptTime;
        this.falseAlarms = falseAlarms;
        this.treatDrop = treatDrop;
        this.defecation = defecation;
        this.score = score;
        this.overview = overview;
        this.disqualification_reason = disqualification_reason;
    }
}