package com.founder.xy.wx;

import com.founder.e5.context.E5Exception;

import net.sf.json.JSONObject;

/**
 * 微信服务器返回错误码的解析
 */
public class WeixinErrorParser {

	public static void parseMessageAndThrow(String jsonStr) throws E5Exception{
		JSONObject jsonObj = JSONObject.fromObject(jsonStr);
		if (jsonObj.containsKey("errcode")&&jsonObj.getInt("errcode")!=0) {
			int errorCode = jsonObj.getInt("errcode");
			
			switch (errorCode) {
			case -1:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:系统繁忙，此时请开发者稍候再试");
			case 0:
				break;
			case 40001:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:获取access_token时AppSecret错误，或者access_token无效。请开发者认真比对AppSecret的正确性，或查看是否正在为恰当的公众号调用接口");
			case 40002:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的凭证类型");
			case 40003:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的OpenID，请开发者确认OpenID（该用户）是否已关注公众号，或是否是其他公众号的OpenID");
			case 40004:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的媒体文件类型");
			case 40005:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的文件类型");
			case 40006:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的文件大小");
			case 40007:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的媒体文件id");
			case 40008:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的消息类型");
			case 40009:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的图片文件大小");
			case 40010:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的语音文件大小");
			case 40011:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的视频文件大小");
			case 40012:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的缩略图文件大小");
			case 40013:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的AppID，请开发者检查AppID的正确性，避免异常字符，注意大小写");
			case 40014:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的access_token，请开发者认真比对access_token的有效性（如是否过期），或查看是否正在为恰当的公众号调用接口");
			case 40015:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的菜单类型");
			case 40016:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的按钮个数");
			case 40017:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的按钮个数");
			case 40018:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的按钮名字长度");
			case 40019:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的按钮KEY长度");
			case 40020:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的按钮URL长度");
			case 40021:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的菜单版本号");
			case 40022:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的子菜单级数");
			case 40023:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的子菜单按钮个数");
			case 40024:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的子菜单按钮类型");
			case 40025:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的子菜单按钮名字长度");
			case 40026:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的子菜单按钮KEY长度");
			case 40027:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的子菜单按钮URL长度");
			case 40028:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的自定义菜单使用用户");
			case 40029:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的oauth_code");
			case 40030:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的refresh_token");
			case 40031:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的openid列表");
			case 40032:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的openid列表长度");
			case 40033:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的请求字符，不能包含\\uxxxx格式的字符");
			case 40035:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的参数");
			case 40038:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的请求格式");
			case 40039:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的URL长度");
			
			case 40050:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的分组id");
			case 40051:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:分组名字不合法");
			case 40117:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:分组名字不合法");
			case 40118:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:media_id大小不合法");
			case 40119:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:button类型错误");
			case 40120:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:button类型错误");
			case 40121:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不合法的media_id类型");
			case 40132:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:微信号不合法");
			case 40137:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不支持的图片格式");
			case 41001:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:缺少access_token参数");
			case 41002:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:缺少appid参数");
			case 41003:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:缺少refresh_token参数");
			case 41004:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:缺少secret参数");
			case 41005:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:缺少多媒体文件数据");
			case 41006:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:缺少media_id参数");
			case 41007:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:缺少子菜单数据");
			case 41008:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:缺少oauth code");
			case 41009:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:缺少openid");
			case 42001:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:access_token超时，请检查access_token的有效期，请参考基础支持-获取access_token中，对access_token的详细机制说明");
			case 42002:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:refresh_token超时");
			case 42003:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:oauth_code超时");
			case 43001:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:需要GET请求");
			case 43002:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:需要POST请求");
			case 43003:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:需要HTTPS请求");
			case 43004:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:需要接收者关注");
			case 43005:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:需要好友关系");
			case 44001:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:多媒体文件为空");
			case 44002:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:POST的数据包为空");
			case 44003:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:图文消息内容为空");
			case 44004:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:文本消息内容为空");
			case 45001:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:多媒体文件大小超过限制");
			case 45002:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:消息内容超过限制");
			case 45003:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:标题字段超过限制");
			case 45004:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:描述字段超过限制");
			case 45005:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:链接字段超过限制");
			case 45006:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:图片链接字段超过限制");
			case 45007:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:语音播放时间超过限制");
			case 45008:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:图文消息超过限制");
			case 45009:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:接口调用超过限制");
			case 45010:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:创建菜单个数超过限制");
			case 45015:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:回复时间超过限制");
			case 45016:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:系统分组，不允许修改");
			case 45017:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:分组名字过长");
			case 45018:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:分组数量超过上限");
			case 46001:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不存在媒体数据");
			case 46002:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不存在的菜单版本");
			case 46003:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不存在的菜单数据");
			case 46004:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:不存在的用户");
			case 47001:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:解析JSON/XML内容错误");
			case 48001:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:api功能未授权，请确认公众号已获得该接口，可以在公众平台官网-开发者中心页中查看接口权限");
			case 50001:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:用户未授权该api");
			case 50002:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:用户受限，可能是违规后接口被封禁");
			case 61451:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:参数错误(invalid parameter)");
			case 61452:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:无效客服账号(invalid kf_account)");
			case 61453:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:客服帐号已存在(kf_account exsited)");
			case 61454:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:客服帐号名长度超过限制(仅允许10个英文字符，不包括@及@后的公众号的微信号)(invalid kf_acount length)");
			case 61455:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:客服帐号名包含非法字符(仅允许英文+数字)(illegal character in kf_account)");
			case 61456:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:客服帐号个数超过限制(10个客服账号)(kf_account count exceeded)");
			case 61457:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:无效头像文件类型(invalid file type)");
			case 61450:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:系统错误(system error)");
			case 61500:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:日期格式错误");
			case 61501:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:日期范围错误");
			case 9001001:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:POST数据参数不合法");
			case 9001002:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:远端服务不可用");
			case 9001003:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:Ticket不合法");
			case 9001004:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:获取摇周边用户信息失败");
			case 9001005:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:获取商户信息失败");
			case 9001006:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:获取OpenID失败");
			case 9001007:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:上传文件缺失");
			case 9001008:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:上传素材的文件类型不合法");
			case 9001009:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:上传素材的文件尺寸不合法");
			case 9001010:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:上传失败");
			case 9001020:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:帐号不合法");
			case 9001021:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:已有设备激活率低于50%，不能新增设备");
			case 9001022:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:设备申请数不合法，必须为大于0的数字");
			case 9001023:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:已存在审核中的设备ID申请");
			case 9001024:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:一次查询设备ID数量不能超过50");
			case 9001025:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:设备ID不合法");
			case 9001026:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:页面ID不合法");
			case 9001027:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:页面参数不合法");
			case 9001028:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:一次删除页面ID数量不能超过10");
			case 9001029:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:页面已应用在设备中，请先解除应用关系再删除");
			case 9001030:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:次查询页面ID数量不能超过50");
			case 9001031:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:时间区间不合法");
			case 9001032:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:保存设备与页面的绑定关系参数错误");
			case 9001033:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:门店ID不合法");
			case 9001034:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:设备备注信息过长");
			case 9001035:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:设备申请参数不合法");
			case 9001036:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:查询起始值begin不合法");
				

			default:
				throw new E5Exception("微信服务器返回错误值！errcode："
						+ errorCode + ",errmsg:"+jsonObj.getString("errmsg"));
			}
		}
	}
}
