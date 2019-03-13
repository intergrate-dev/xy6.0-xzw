package com.founder.xy.h5;

import com.founder.xy.commons.InfoHelper;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.helper.StringUtil;

import java.io.UnsupportedEncodingException;

public class Mugeda extends MugedaModel {

    private String uniqueCode = "FOUNDER_XY_V5_";
    private String msg;
    private String url;

    public Mugeda(String userName, String userID, String type, int role, String group) {
        this.secret = getConfig("木疙瘩secret");
        this.domain = getConfig("木疙瘩domain");
        this.group = group;
        this.timestamp = System.currentTimeMillis() / 1000;
        this.openid = uniqueCode + userID;
        this.nickname = userName;
        this.role = role;
        this.signature = buildSignature(type);

        this.url = getConfig("木疙瘩域");
    }

    /**
     * 获得签名
     *
     * @param type
     * @return
     */
    private String buildSignature(String type) {
        String signature = null;
        switch (type) {
            case "login":
                signature = InfoHelper.MD5(
                        timestamp + "." + domain + "." + openid + "." + secret + "." + group + "." + role);
                break;
            case "works":
                signature = InfoHelper.MD5(timestamp + "." + domain + "." + openid + "." + secret);
                break;
            case "list":
                signature = InfoHelper.MD5(timestamp + "." + domain + "." + openid + "." + secret);
                break;
        }
        return signature;
    }

    private String getConfig(String key) {
        key = InfoHelper.getConfig("H5", key);
        if (StringUtil.isBlank(key)) {
            this.msg = "请配置\"" + key + "\"！";
        }
        return key;
    }

    public String buildFinalKey() {
        try {
            return new String(Base64.encodeBase64(JSONObject.fromObject(this).toString().getBytes("UTF-8")), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    String getErrorMsg() {
        return this.msg;
    }

    String buildUrl() {
        return this.url;
    }

    @Override
    public String toString() {
        return "Mugeda{" +
                "secret='" + secret + '\'' +
                ", timestamp=" + timestamp +
                ", openid='" + openid + '\'' +
                ", domain='" + domain + '\'' +
                ", group='" + group + '\'' +
                ", role=" + role +
                ", nickname='" + nickname + '\'' +
                ", signature='" + signature + '\'' +
                ", uniqueCode='" + uniqueCode + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}

class MugedaModel {
    String secret;//签名密匙，由Mugeda平台分配
    Long timestamp;//时间戳
    String openid;//用户的唯一ID，最小长度为13位
    String domain;//平台识别码,由Mugeda平台分配
    String group;//组名称或机构名称
    Integer role;//角色表标示, 1代表超级管理员，2代表组管理员，3代表设计师
    String nickname;//用户昵称, 最大长度为20
    String signature;//参数签名

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}