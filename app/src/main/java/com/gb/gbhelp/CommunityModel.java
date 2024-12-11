package com.gb.gbhelp;

import java.util.Objects;

public class CommunityModel {
    private String senderId,nickName,tag;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "CommunityModel{" +
                "senderId='" + senderId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return getSenderId().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommunityModel that = (CommunityModel) o;
        return Objects.equals(senderId, that.senderId) && Objects.equals(nickName, that.nickName);
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
