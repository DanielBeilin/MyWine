package com.example.mywine.model;

import android.text.TextUtils;

import androidx.room.TypeConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Converters implements Serializable {
    @TypeConverter
    public String fromStringArray(ArrayList<String> stringArrayList) {
        if (stringArrayList == null) { return (null); }
        return TextUtils.join(",",stringArrayList);
    }

    @TypeConverter
    public ArrayList<String> toStringList(String ArrayListString ){
        if ( ArrayListString == null) { return (null);}
        return new ArrayList<String>(Arrays.asList(ArrayListString.split(",")));
    }
}