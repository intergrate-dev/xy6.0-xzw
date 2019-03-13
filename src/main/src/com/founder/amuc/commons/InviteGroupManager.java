package com.founder.amuc.commons;

import com.founder.e5.cat.Category;
import com.founder.e5.commons.ArrayUtils;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.site.SiteUserManager;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wenkx on 2017/1/18.
 * 分组管理类
 * 提供分组权限相关的方法
 */
@Service
public class InviteGroupManager {
    @Autowired
    private SiteUserManager userManager;



    public JSONArray getJSONArr(HttpServletRequest request, int siteID) {
        JSONArray groupsJSON = new JSONArray();
        //@wenkx 增加分组权限
        getOneJSONArr(groupsJSON,request, CatTypes.CAT_SPECIAL,siteID);
        getOneJSONArr(groupsJSON,request,CatTypes.CAT_TEMPLATE,siteID);
        getOneJSONArr(groupsJSON,request,CatTypes.CAT_PHOTO,siteID);
        return groupsJSON;
    }

    private void getOneJSONArr(JSONArray groupsJSON, HttpServletRequest request, CatTypes cat, int siteID) {
        Map<String, String> json = new HashMap<>();
        json.put("id", String.valueOf(cat.typeID()));
        json.put("name", cat.typeName());
        json.put("pid", "0");
        groupsJSON.add(json);
        Category[] catGroups = InfoHelper.getCatGroups(request,cat.typeID(),siteID);
        for (Category group :catGroups) {
            json = new HashMap<>();
            json.put("id", String.valueOf(group.getCatID()));
            json.put("name", InfoHelper.filter4Json(group.getCatName()));
            json.put("pid",String.valueOf(cat.typeID()));
            groupsJSON.add(json);
        }
    }



    public Category[] getGroupsWithPower(HttpServletRequest request, int siteID, Category[] allGroups) {

        if( !"是".equals(InfoHelper.getConfig("其它", "是否启用分组权限管理")))
            return allGroups;
        int userID = ProcHelper.getUserID(request);
        int userRelLibID = LibHelper.getUserRelLibID(request);
        int[] result = new int[0];
        try {
            result = StringUtils.getIntArray (userManager.getRelated(userRelLibID, userID, siteID, 8));
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        Category[] temp = new Category[allGroups.length];
        int i =0;
        for(Category group:allGroups){
            if (ArrayUtils.contains(result,group.getCatID())){
                temp[i++] = group;
            }
        }
        Category[] groupsWithPower = new Category[i];
        i=0;
        for(Category group:temp){
            if(group!=null)
                groupsWithPower[i++] = group;
        }
        return groupsWithPower;

    }


    /**
     * 返回有权限的分组ID
     *
     * @param request     the request
     * @param siteID      the site id
     * @param allGroupsID the all groups id
     * @return the int [ ]
     */
    public int[] getGroupsIDWithPower(HttpServletRequest request, int siteID, int[] allGroupsID) {

        if( !"是".equals(InfoHelper.getConfig("其它", "是否启用分组权限管理")))
            return allGroupsID;
        int userID = ProcHelper.getUserID(request);
        int userRelLibID = LibHelper.getUserRelLibID(request);
        int[] result = new int[0];
        try {
            result = StringUtils.getIntArray (userManager.getRelated(userRelLibID, userID, siteID, 8));
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        int[] temp = new int[allGroupsID.length];
        int i =0;
        for(int groupID:allGroupsID){
            if (ArrayUtils.contains(result,groupID)){
                temp[i++] = groupID;
            }
        }
        int[] groupsIDWithPower = new int[i];
        i=0;
        for(int groupID:temp){
            if(groupID!=0)
                groupsIDWithPower[i++] = groupID;
        }
        return groupsIDWithPower;

    }

    /**
     * 输入一个分组ID 判断用户是否有权限
     *
     * @param request  the request
     * @param siteID   the site id
     * @param GroupsID the groups id
     * @return the boolean
     */
    public boolean hasGroupPower(HttpServletRequest request, int siteID, int GroupsID) {

        if( !"是".equals(InfoHelper.getConfig("其它", "是否启用分组权限管理")))
            return true;
        int[] allGroupsID = {GroupsID};
        if(getGroupsIDWithPower(request,siteID,allGroupsID).length>0)
            return true;
        return false;

    }
}
