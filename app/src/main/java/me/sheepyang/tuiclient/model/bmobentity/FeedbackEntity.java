package me.sheepyang.tuiclient.model.bmobentity;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/7/1.
 */

public class FeedbackEntity extends BmobObject implements Serializable {
    private UserEntity user;//反馈用户
    private String content;//反馈内容
    private String versionName;
    private Integer versionCode;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }
}
