package com.example.nakama.DataBase.Entities.UserScores;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserScoreDao {
    @Query("select count(1=1)")
    int fakeRead();

    @Insert
    void insertAll(UserScore... userScores);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void update(UserScore userScore);

    @Update
    void updateScores(UserScore... userScore);

    @Query("select * from UserScore where user_id = :userId and difficulty = :difficulty and ring = :ring ")
    UserScore getUserScore(int userId, String difficulty, String ring);

    @Query("delete from UserScore")
    void delete();

    @Query("update UserScore set attempt_time = :attempt_time where user_id = :userId and difficulty = :difficulty and ring = :ring")
    void updateUserScoresAttemptTime(int userId, String difficulty, String ring, String attempt_time);
    @Query("update UserScore set false_alarms = :false_alarms where user_id = :userId and difficulty = :difficulty and ring = :ring")
    void updateUserScoresFalseAlarms(int userId, String difficulty, String ring, int false_alarms);
    @Query("select false_alarms from UserScore where user_id = :userId and difficulty = :difficulty and ring = :ring")
    int getUserScoresFalseAlarms(int userId, String difficulty, String ring);
    @Query("select score from UserScore where user_id = :userId and difficulty = :difficulty and ring = :ring")
    int getUserScoresScore(int userId, String difficulty, String ring);
    @Query("update UserScore set score = :score where user_id = :userId and difficulty = :difficulty and ring = :ring")
    void updateScorePoints(int userId, String difficulty, String ring, int score);
    @Query("update UserScore set samples_found = :samplesFound where user_id = :userId and difficulty = :difficulty and ring = :ring")
    void updateUserScoresSamplesFound(int userId, String difficulty, String ring, int samplesFound);
    @Query("select samples_found from UserScore where user_id = :userId and difficulty = :difficulty and ring = :ring")
    int getUserScoresSamplesFound(int userId, String difficulty, String ring);
    @Query("update UserScore set treat_drop = :treatDrop where user_id = :userId and difficulty = :difficulty and ring = :ring")
    void updateUserScoresTreatDropped(int userId, String difficulty, String ring, int treatDrop);
    @Query("select treat_drop from UserScore where user_id = :userId and difficulty = :difficulty and ring = :ring")
    int getUserScoresTreatDropped(int userId, String difficulty, String ring);
    @Query("update UserScore set defecation = :defecation where user_id = :userId and difficulty = :difficulty and ring = :ring")
    void updateUserScoresDefecation(int userId, String difficulty, String ring, boolean defecation);
}