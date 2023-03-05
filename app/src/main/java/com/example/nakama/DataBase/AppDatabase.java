package com.example.nakama.DataBase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.nakama.DataBase.Entities.UserScores.UserScores;
import com.example.nakama.DataBase.Entities.UserScores.UserScoresDao;
import com.example.nakama.DataBase.Entities.Users.Users;
import com.example.nakama.DataBase.Entities.Users.UsersDao;

import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {UserScores.class, Users.class},  version = 3)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;
    public abstract UserScoresDao userScoresDao();
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

    public boolean addUserScore(int userId, String difficulty, String ring){
        List<UserScores> users = userScoresDao().getUserScores(userId, difficulty, ring);
        UserScores userScore = new UserScores(userId, difficulty, ring);
        if(users.isEmpty()){
            userScoresDao().insertAll(userScore);
            return true;
        }
        return false;
    }

    public void clearUserScore(int userId, String difficulty, String ring){
        userScoresDao().updateUserScores(userId, difficulty, ring, null, 0,0, false, 200, null);
    }
}
