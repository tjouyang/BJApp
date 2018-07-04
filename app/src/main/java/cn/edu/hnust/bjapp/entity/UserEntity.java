package cn.edu.hnust.bjapp.entity;

/**
 * Created by tjouyang on 2016/10/11.
 * 用户实体
 */

public class UserEntity implements Cloneable{
    private int id; //user id
    private String url; //头像地址
    private String nickname;    //昵称
    private int age;        //年龄
    private int sex;        //1男2女
    private String address; //住址
    private String date;    //签到时间,作为签到时的
    private String job;     //游客职业

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String text) {
        this.nickname = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public UserEntity clone(){
        try {
            return (UserEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return new UserEntity();
        }
    }

    @Override
    public String toString() {
        return nickname + id;
    }

}
