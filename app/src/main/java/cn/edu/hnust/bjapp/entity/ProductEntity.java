package cn.edu.hnust.bjapp.entity;

import org.json.JSONArray;

import java.util.Date;

/**
 * Created by tjouyang on 2016/10/11.
 * 订单实体
 */

public class ProductEntity implements Cloneable{
    private int id; //单号
    //以下是游客的

    private String dest;    //目的地
    private String starttime; //开始时间
    private String endtime;   //结束时间
    private String description; //简单的介绍
    private String guide_ids;   //点了接受该单的导游
    private int state;      //0代表未被导游带,1代表已被导游带,用于控制该订单是否在导游方显示
    //导游的
    private int limit;      //订单最大人数
    private String type;    //filter,用于过滤
    private String deadlinetime;  //报名截止日期
    private JSONArray tourist_ids;  //已经成功报名的游客

    //

    private String name;    //订单名->最后会变成团名,或者游客昵称
    private String img;     //团头像地址,或者游客头像
    private int isPraised;
    private int num;        //目前多少人

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getGuide_ids() {
        return guide_ids;
    }

    public void setGuide_ids(String guide_ids) {
        this.guide_ids = guide_ids;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDeadlinetime() {
        return deadlinetime;
    }

    public void setDeadlinetime(String deadlinetime) {
        this.deadlinetime = deadlinetime;
    }

    public JSONArray getTourist_ids() {
        return tourist_ids;
    }

    public void setTourist_ids(JSONArray tourist_ids) {
        this.tourist_ids = tourist_ids;
    }

    public int getIsPraised() {
        return isPraised;
    }

    public void setIsPraised(int isPraised) {
        this.isPraised = isPraised;
    }

    public ProductEntity clone(){
        try {
            return (ProductEntity)super.clone();
        } catch (CloneNotSupportedException e) {
            return new ProductEntity();
        }
    }
}
