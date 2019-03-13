package com.founder.xy.api.param;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.sys.SysConfig;
import com.founder.e5.sys.SysConfigReader;
import com.founder.xy.commons.InfoHelper;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/api/param")
public class ParamConfigController {
	@RequestMapping(value = "paramConfig.do")
    public void getParamConfig(HttpServletResponse response) throws E5Exception {
		JSONObject obj = new JSONObject();
        SysConfigReader sysReader = (SysConfigReader) Context.getBean(SysConfigReader.class);
        SysConfig[] sysConfigs = sysReader.getAppSysConfigs(0);
        for(SysConfig sysConfig: sysConfigs){
        	if("配置".equals(sysConfig.getProject()) 
        			&& sysConfig.getValue() != null 
        			&& !"-1".equals(sysConfig.getValue().trim())
        			&& !"".equals(sysConfig.getValue().trim()))
        		obj.put(sysConfig.getItem(), sysConfig.getValue().trim());
        }
        obj.put("WEBSITE_URL", InfoHelper.getConfig("互动", "外网资源地址"));
        InfoHelper.outputJson(obj.toString(), response);
    }
}
