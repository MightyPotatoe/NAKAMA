package com.example.nakama.DataBase.Entities.UserScores;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserScoresDao {
    @Query("select count(1=1)")
    int fakeRead();

    @Insert
    void insertAll(UserScores... userScores);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void update(UserScores userScores);

    @Query("select * from UserScores where user_id = :userId and difficulty = :difficulty and ring = :ring ")
    List<UserScores> getUserScores(int userId, String difficulty, String ring);

    @Query("update UserScores set attempt_time = :attempt_time, false_alarms = :falseAlarms, treat_drop = :treatDrop, defecation = :defecation, score = :score, overview = :overview, samples_found = :samplesFound where user_id = :userId and difficulty = :difficulty and ring = :ring")
    void updateUserScores(int userId, String difficulty, String ring, String attempt_time, int falseAlarms, int treatDrop, boolean defecation, int score, String overview, int samplesFound);

    @Query("delete from UserScores")
    void delete();

    @Query("update UserScores set attempt_time = :attempt_time where user_id = :userId and difficulty = :difficulty and ring = :ring")
    void updateUserScoresAttemptTime(int userId, String difficulty, String ring, String attempt_time);
    @Query("update UserScores set false_alarms = :false_alarms where user_id = :userId and difficulty = :difficulty and ring = :ring")
    void updateUserScoresFalseAlarms(int userId, String difficulty, String ring, int false_alarms);
    @Query("select false_alarms from UserScores where user_id = :userId and difficulty = :difficulty and ring = :ring")
    int getUserScoresFalseAlarms(int userId, String difficulty, String ring);
    @Query("select score from UserScores where user_id = :userId and difficulty = :difficulty and ring = :ring")
    int getUserScoresScore(int userId, String difficulty, String ring);
    @Query("update UserScores set score = :score where user_id = :userId and difficulty = :difficulty and ring = :ring")
    void updateUserScoresScore(int userId, String difficulty, String ring, int score);
}