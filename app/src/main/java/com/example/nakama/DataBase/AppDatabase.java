package com.example.nakama.DataBase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.nakama.DataBase.Entities.UserScores.UserScore;
import com.example.nakama.DataBase.Entities.UserScores.UserScoreDao;
import com.example.nakama.DataBase.Entities.Users.Users;
import com.example.nakama.DataBase.Entities.Users.UsersDao;
import com.example.nakama.Utils.Converter;
import com.example.nakama.Utils.Dictionary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

@Database(entities = {UserScore.class, Users.class},  version = 6)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;
    public abstract UserScoreDao userScoresDao();
    public abstract UsersDao usersDao();

    public synchronized static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = buildDatabase(context);
        }
        //---PERFORMING FAKE READ TO INITIALIZE DB
        Executors.newSingleThreadExecutor().execute(() -> {
            INSTANCE.userScoresDao().fakeRead();
            INSTANCE.usersDao().fakeRead();
        });
        return INSTANCE;
    }

    private static AppDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context,
                AppDatabase.class,
                "nakama_nosework_db")
                .allowMainThreadQueries()
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadExecutor().execute(() -> {});
                    }
                })
                .fallbackToDestructiveMigration()
                .build();
    }

    public boolean addUser(Users user){
        List<Users> users = usersDao().selectAllUsers(user.userName, user.usersSurname, user.dogName);
        if(users.isEmpty()){
            usersDao().insertAll(user);
            return true;
        }
        return false;
    }

    public int getUserId(Users users){
        return usersDao().getUserId(users.userName, users.usersSurname, users.dogName);
    }

    public Users getUser(int id){
        return usersDao().getUser(id);
    }

    public boolean addUserScoreIfNotExists(int userId, String difficulty, String ring){
        UserScore userScore = userScoresDao().getUserScore(userId, difficulty, ring);
        if(userScore == null){
            userScoresDao().insertAll(new UserScore(userId, difficulty, ring));
            return true;
        }
        return false;
    }

    public void addUserScoreForAllRingsIfNotExists(int userId, String difficulty){
        addUserScoreIfNotExists(userId, difficulty, Dictionary.Rings.RING_1);
        addUserScoreIfNotExists(userId, difficulty, Dictionary.Rings.RING_2);
        addUserScoreIfNotExists(userId, difficulty, Dictionary.Rings.RING_3);
        addUserScoreIfNotExists(userId, difficulty, Dictionary.Rings.RING_4);
    }

    public void clearUserScore(int userId, String difficulty, String ring){
        UserScore userScore = new UserScore(userId, difficulty, ring);
        userScoresDao().updateScores(userScore);
    }
    public boolean checkAllRingsForAnyDefecation(int userId, String difficulty){
        UserScore userScoreForRing1 = userScoresDao().getUserScore(userId, difficulty, Dictionary.Rings.RING_1);
        UserScore userScoreForRing2 = userScoresDao().getUserScore(userId, difficulty, Dictionary.Rings.RING_2);
        UserScore userScoreForRing3 = userScoresDao().getUserScore(userId, difficulty, Dictionary.Rings.RING_3);
        UserScore userScoreForRing4 = userScoresDao().getUserScore(userId, difficulty, Dictionary.Rings.RING_4);
        return userScoreForRing1.defecation || userScoreForRing2.defecation || userScoreForRing3.defecation || userScoreForRing4.defecation;
    }

    public void disqualifyContestant(int userId, String difficulty, String reason){
        addUserScoreForAllRingsIfNotExists(userId, difficulty);
        Map<String, UserScore> ringMap = new HashMap<>();
        ringMap.put(Dictionary.Rings.RING_1, userScoresDao().getUserScore(userId, difficulty, Dictionary.Rings.RING_1));
        ringMap.put(Dictionary.Rings.RING_2, userScoresDao().getUserScore(userId, difficulty, Dictionary.Rings.RING_2));
        ringMap.put(Dictionary.Rings.RING_3, userScoresDao().getUserScore(userId, difficulty, Dictionary.Rings.RING_3));
        ringMap.put(Dictionary.Rings.RING_4, userScoresDao().getUserScore(userId, difficulty, Dictionary.Rings.RING_4));

        for(String ring: ringMap.keySet()){
            UserScore userScore = ringMap.get(ring);
            assert userScore != null;
            userScore.score = 0;
            switch (difficulty){
                case Dictionary.Difficulty.Basic.NAME:
                    userScore.attemptTime = Converter.millisToString(Dictionary.Difficulty.Basic.ATTEMPT_TIME);
                    break;
                case Dictionary.Difficulty.Advanced.NAME:
                    userScore.attemptTime = Converter.millisToString(Dictionary.Difficulty.Advanced.ATTEMPT_TIME);
                    break;
            }
            userScore.disqualification_reason = reason;
            userScoresDao().updateScores(userScore);
        }
    }
}
