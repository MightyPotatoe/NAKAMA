package com.example.nakama.DataBase.Entities.Users;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UsersDao {

    @Query("select count(1=1)")
    int fakeRead();

    @Insert
    void insertAll(Users... users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void update(Users users);

    @Query("select * from Users where name = :name and surname = :surname and dog_name = :dogName")
    List<Users> selectAllUsers(String name, String surname, String dogName);

    @Query("select uid from Users where name = :name and surname = :surname and dog_name = :dogName")
    int getUserId(String name, String surname, String dogName);

    @Query("select * from Users where uid = :uid")
    Users getUser(int uid);
}