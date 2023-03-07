package com.example.nakama.DataBase.Entities.UserScores;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"uid"},
        unique = true)})
public class UserScores {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "difficulty")
    public String difficulty;

    @ColumnInfo(name = "ring")
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

    public UserScores(int userId, String difficulty, String ring) {
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
    }
}