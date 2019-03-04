package com.yd.spring.springBoot2Demo.libs.wechatPublic;

import com.yd.spring.springBoot2Demo.libs.redis.Redis;
import com.yd.spring.springBoot2Demo.libs.springReqClient.SpringReqClient;
import com.yd.spring.springBoot2Demo.libs.timerManager.TimerCallback;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;

import static com.yd.spring.springBoot2Demo.model.globalClasses.StaticClass.timer;

public class WechatPublic {
	private Logger logger = LoggerFactory.getLogger(WechatPublic.class);
	private Redis  redis;

	protected String  name;
	public    String  appid;
	public    String  secret;
	private   String  timeoutId;
	private   Boolean destoryed = false;

	WechatPublic(String name, String appid, String secret, Redis redis) {
		this.name = name;
		this.appid = appid;
		this.secret = secret;
		this.redis = redis;

		getWechatPublicTimeout();
		logger.info(getWechatPublicInfo() + " SERVICE INITIALIZED");
	}

	private String getWechatPublicInfo() {
		return "WechatPublic name=" + name + " appid=" + appid;
	}

	private void initWechatPublicToken() {
		logger.debug(getWechatPublicInfo() + " initWechatPublicToken START");
		try {
			requestWechatGetAccessToken();
			requestWechatGetJsapiTicket();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug(getWechatPublicInfo() + " initWechatPublicToken DONE");
	}

	private void requestWechatGetAccessToken() throws Exception {
		HashMap params = new HashMap();
		params.put("appid", appid);
		params.put("secret", secret);
		params.put("grant_type", "client_credential");
		HashMap accessTokenMap = SpringReqClient.getForJson("https://api.weixin.qq.com/cgi-bin/token", params);
		if (accessTokenMap.get("access_token") == null) {
			throw new IllegalArgumentException("AccessToken GET FAILED: " + accessTokenMap.get("errmsg"));
		}

		String accessToken           = (String) accessTokenMap.get("access_token");
		Long   accessTokenExpireTime = ((Integer) accessTokenMap.get("expires_in")).longValue();
		redis.set("wechatAccessToken_" + appid, accessToken, accessTokenExpireTime);
		logger.debug(getWechatPublicInfo() + " requestWechatGetAccessToken SUCCESS: " + accessToken);
	}

	public String getAccessToken() {
		return redis.get("wechatAccessToken_" + appid);
	}

	private Long getAccessTokenTtl() {
		return redis.ttl("wechatAccessToken_" + appid);
	}

	private void requestWechatGetJsapiTicket() throws Exception {
		HashMap params = new HashMap();
		params.put("access_token", getAccessToken());
		params.put("type", "jsapi");
		HashMap jsapiTicketMap = SpringReqClient.getForJson("https://api.weixin.qq.com/cgi-bin/ticket/getticket", params);
		if (jsapiTicketMap.get("ticket") == null) {
			throw new IllegalArgumentException("JsapiTicket GET FAILED: " + jsapiTicketMap.get("errmsg"));
		}
		String jsapiTicket           = (String) jsapiTicketMap.get("ticket");
		Long   jsapiTicketExpireTime = ((Integer) jsapiTicketMap.get("expires_in")).longValue();
		redis.set("wechatJsapiTicket_" + appid, jsapiTicket, jsapiTicketExpireTime);
		logger.debug(getWechatPublicInfo() + " requestWechatGetJsapiTicket SUCCESS: " + jsapiTicket);
	}

	public String getJsapiTicket() {
		return redis.get("wechatJsapiTicket_" + appid);
	}

	private Long getJsapiTicketTtl() {
		return redis.ttl("wechatJsapiTicket_" + appid);
	}

	private void getWechatPublicTimeout() {
		if (destoryed) {
			return;
		}

		logger.debug(getWechatPublicInfo() + " getWechatPublicTimeout START");

		Long minExpireTime;

		Long jsapiTicketExpireTime = getJsapiTicketTtl();
		Long accessTokenExpireTime = getAccessTokenTtl();

		minExpireTime = jsapiTicketExpireTime;
		if (accessTokenExpireTime < jsapiTicketExpireTime) {
			minExpireTime = accessTokenExpireTime;
		}

		if (minExpireTime < 1440) {
			initWechatPublicToken();
		}

		TimerCallback callback = new TimerCallback() {
			@Override
			public void run() {
				getWechatPublicTimeout();
			}
		};

		timeoutId = timer.setTimeout(callback, 600000L);
	}

	public void sendTemplateMsg(String openId, String templateId, HashMap data) throws Exception {
		HashMap params = new HashMap();

		params.put("touser", openId);
		params.put("template_id", templateId);
		params.put("data", data);

		HashMap results = SpringReqClient.postForJson("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + getAccessToken(), params);
		if ((int) results.get("errcode") != 0) {
			throw new IllegalArgumentException((String) results.get("errmsg"));
		}
	}

	public HashMap generateJsSdkSignature(String url) {
		HashMap<String, String> result = new HashMap();

		result.put("appId", appid);
		result.put("nonceStr", generateRandomStr());

		Long timestamp = new Date().getTime();
		timestamp = (long) Math.round(timestamp / 1000);
		result.put("timestamp", Long.toString(timestamp));

		result.put("url", url);
		result.put("signature", calcSignature(getJsapiTicket(), result.get("nonceStr"), result.get("timestamp"), url));

		return result;
	}

	private String calcSignature(String jsapiTicket, String nonceStr, String timestamp, String url) {
		String str = "jsapi_ticket=" + jsapiTicket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url;

		return getSha1(str);
	}

	public static String getSha1(String str) {

		char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f'};
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
			mdTemp.update(str.getBytes(StandardCharsets.UTF_8));
			byte[] md  = mdTemp.digest();
			int    j   = md.length;
			char[] buf = new char[j * 2];
			int    k   = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
				buf[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(buf);
		} catch (Exception e) {
			return null;
		}
	}

	private String generateRandomStr() {
		return RandomStringUtils.randomAlphanumeric(16);
	}

	public void desroy() {
		timer.clearTimeout(timeoutId);
		destoryed = true;
	}
}
