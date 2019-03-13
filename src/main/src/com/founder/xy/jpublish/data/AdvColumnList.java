package com.founder.xy.jpublish.data;

import java.util.List;

/**子栏目
 * Created by Wenkx on 2017/3/6.
 */
public class AdvColumnList {
    private int libID;
    private long id;
    private String name;
    private String keyword;
    private String description; // 描述
    private int channel;
    private String casIDs; //栏目的级联ID
    private String url; //栏目url
    private String urlPad; //栏目urlPad
    //Column column;
    private List<PubArticle> articles;
    /*public Column getColumn() {
        return column;
    }
    public void setColumn(Column column) {
        this.column = column;
    }
*/
    public List<PubArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<PubArticle> articles) {
        this.articles = articles;
    }


    public int getLibID() {
        return libID;
    }

    public void setLibID(int libID) {
        this.libID = libID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getCasIDs() {
        return casIDs;
    }

    public void setCasIDs(String casIDs) {
        this.casIDs = casIDs;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlPad() {
        return urlPad;
    }

    public void setUrlPad(String urlPad) {
        this.urlPad = urlPad;
    }
}
