package com.example.mywine.model;

import androidx.room.TypeConverter;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

public class Converters implements Serializable {
    @TypeConverter
    public String fromStringArray(ArrayList<String> stringArrayList) {
        if (stringArrayList == null) { return (null); }

        Gson gson = new Gson();
        String json = gson.toJson(stringArrayList,String.class);
        return json;
    }

    @TypeConverter
    public ArrayList<String> toStringList(String ArrayListString ){
        if ( ArrayListString == null) { return (null);}

        Gson gson = new Gson();
        ArrayList<String> stringArrayList = gson.fromJson(ArrayListString,ArrayList.class);

        return stringArrayList;
    }
}

