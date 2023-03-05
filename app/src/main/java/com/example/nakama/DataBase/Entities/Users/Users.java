package com.example.nakama.DataBase.Entities.Users;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"dog_name"},
        unique = true)})
public class Users {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "name")
    public String userName;

    @ColumnInfo(name = "surname")
    public String usersSurname;

    @ColumnInfo(name = "dog_name")
    public String dogName;

    public Users(String userName, String usersSurname, String dogName) {
        this.userName = userName;
        this.usersSurname = usersSurname;
        this.dogName = dogName;
    }
}