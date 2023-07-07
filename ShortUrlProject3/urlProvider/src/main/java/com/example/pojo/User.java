package com.example.pojo;

public class User {
    private Integer id;
    private String short_url;
    private String long_url;
    private long expiredTime;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", short_url='" + short_url + '\'' +
                ", long_url='" + long_url + '\'' +
                ", expiredTime=" + expiredTime +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShort_url() {
        return short_url;
    }

    public void setShort_url(String short_url) {
        this.short_url = short_url;
    }

    public String getLong_url() {
        return long_url;
    }

    public void setLong_url(String long_url) {
        this.long_url = long_url;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }
}
