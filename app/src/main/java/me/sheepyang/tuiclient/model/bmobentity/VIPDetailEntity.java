package me.sheepyang.tuiclient.model.bmobentity;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by SheepYang on 2017-07-21.
 */

public class VIPDetailEntity extends BmobObject {
    private String name;//套餐名称
    private Boolean isShow;//是否展示该套餐
    private Boolean isRecommend = false;//是否被推荐
    private Double price;//原价
    private Double rebate;//折扣
    private Long duration;//套餐持续时长
    private List<UserEntity> userList;//购买了该套餐的用户

    public List<UserEntity> getUserList() {
        return userList;
    }

    public void setUserList(List<UserEntity> userList) {
        this.userList = userList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getShow() {
        return isShow;
    }

    public void setShow(Boolean show) {
        isShow = show;
    }

    public Boolean getRecommend() {
        return isRecommend;
    }

    public void setRecommend(Boolean recommend) {
        isRecommend = recommend;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getRebate() {
        return rebate;
    }

    public void setRebate(Double rebate) {
        this.rebate = rebate;
    }
}
