package com.yd.spring.springBoot2Demo.libs.wechatPublic;

import com.yd.spring.springBoot2Demo.libs.mysql.Crud;
import com.yd.spring.springBoot2Demo.libs.redis.Redis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Component
public class WechatPublicGroup {

	private Logger    logger = LoggerFactory.getLogger(WechatPublicGroup.class);
	private Crud      mysql;
	private Redis     redis;
	private HashMap[] wechatPublicList;

	private HashMap<String, HashMap> wechatPublicMapByAppid = new HashMap<>();

	WechatPublicGroup(@Autowired Crud mysql, @Autowired Redis redis) {
		this.mysql = mysql;
		this.redis = redis;

		try {
			this.initWechatPublicList();
			logger.info("WechatPublicGroup Component INITIALIZED");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initWechatPublicList() throws Exception {
		this.wechatPublicList = mysql.selectQuery("SELECT * FROM uu_wehchat_center.wechat_public_account");
		for (HashMap wechatPublicRow : this.wechatPublicList) {

			HashMap wechatPublicInfo = new HashMap();
			wechatPublicInfo.put("wechatPublic", wechatPublicRow);

			String name   = (String) wechatPublicRow.get("name");
			String appid  = (String) wechatPublicRow.get("appid");
			String secret = (String) wechatPublicRow.get("secret");

			WechatPublic wechatPublic = new WechatPublic(name, appid, secret, this.redis);
			wechatPublicInfo.put("wechatPublicObject", wechatPublic);

			this.wechatPublicMapByAppid.put(appid, wechatPublicInfo);
		}
	}

	public WechatPublic getWecatPublic(String appId) {
		return (WechatPublic) wechatPublicMapByAppid.get(appId).get("wechatPublicObject");
	}

	private String getRefundTemplateId(String appId) {
		HashMap<String, String> wechatPublic = (HashMap) wechatPublicMapByAppid.get(appId).get("wechatPublic");
		return wechatPublic.get("refund_template_id");
	}

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年 MM月 dd日 HH:mm:ss");

	public void sendRefundTemplateMsg(
			String appId,
			String openId,
			String nickname,
			String orderNumber,
			String payTime,
			String amount,
			String number,
			String deviceId,
			String lastPoint
	) throws Exception {
		HashMap<String, String> first    = new HashMap<>();
		HashMap<String, String> keyword1 = new HashMap<>();
		HashMap<String, String> keyword2 = new HashMap<>();
		HashMap<String, String> keyword3 = new HashMap<>();
		HashMap<String, String> keyword4 = new HashMap<>();
		HashMap<String, String> remark   = new HashMap<>();

		first.put("value", "");
		keyword1.put("value", nickname);
		keyword2.put("value", "退币通知");
		SimpleDateFormat payTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		keyword3.put("value", "您于\n" +
				dateFormat.format(payTimeFormat.parse(payTime)) + "\n" +
				"在机台:" + deviceId + " 消费的订单\n\n" +
				orderNumber + "\n\n" +
				amount + "元" + number + "次 发生异常\n\n" +
				"系统已自动将您消费的友币于\n" +
				dateFormat.format(new Date()) + "\n" +
				"退还到账户内\n\n" +
				"当前账户余额" + lastPoint + "\n\n" +
				"可再次扫码消费哟～\n");
		keyword4.put("value", "可在公众号打字和客服反馈哦～");
		remark.put("value", "");


		HashMap<String, HashMap> data = new HashMap<>();
		data.put("first", first);
		data.put("keyword1", keyword1);
		data.put("keyword2", keyword2);
		data.put("keyword3", keyword3);
		data.put("keyword4", keyword4);
		data.put("remark", remark);

		String templateId = getRefundTemplateId(appId);

		getWecatPublic(appId).sendTemplateMsg(openId, templateId, data);
	}
}
