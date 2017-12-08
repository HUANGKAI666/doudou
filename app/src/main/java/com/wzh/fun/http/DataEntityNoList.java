package com.wzh.fun.http;

import com.google.gson.annotations.SerializedName;
import com.wzh.fun.entity.BaseEntity;
import com.wzh.fun.entity.WeatherEntity;

import java.util.List;



public class DataEntityNoList<T> extends BaseEntity {
    @SerializedName("data")
    private WeatherEntity data ;

    public WeatherEntity getData() {
        return data;
    }

    public void setData(WeatherEntity data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataEntity{" +
                ", data=" + data +
                '}';
    }
}
