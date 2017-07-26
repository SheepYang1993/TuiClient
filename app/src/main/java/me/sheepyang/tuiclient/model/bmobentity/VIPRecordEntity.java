package me.sheepyang.tuiclient.model.bmobentity;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by SheepYang on 2017-07-26.
 */

public class VIPRecordEntity extends BmobObject implements Serializable {
    private VIPDetailEntity vipDetail;
    private UserEntity user;
}
