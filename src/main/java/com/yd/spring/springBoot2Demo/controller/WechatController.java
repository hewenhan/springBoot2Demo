package com.yd.spring.springBoot2Demo.controller;

import com.yd.spring.springBoot2Demo.libs.redis.Redis;
import com.yd.spring.springBoot2Demo.libs.wechatPublic.WechatPublic;
import com.yd.spring.springBoot2Demo.libs.wechatPublic.WechatPublicGroup;
import com.yd.spring.springBoot2Demo.model.result.Res;
import com.yd.spring.springBoot2Demo.model.result.ResBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class WechatController {
	@Autowired
	private Redis             redis;
	@Autowired
	private WechatPublicGroup wechatPublicGroup;

	@RequestMapping("/getAccessToken")
	public ResBody getAccessToken(@RequestParam(value = "appId") String appId) {
		WechatPublic wechatPublic = wechatPublicGroup.getWecatPublic(appId);
		return Res.success(wechatPublic.getAccessToken());
	}

	@RequestMapping("/getJsSdkSignature")
	public ResBody getJsSdkSignature(@RequestParam(value = "appId") String appId, @RequestHeader(value = "referer") String referer) {
		WechatPublic wechatPublic = wechatPublicGroup.getWecatPublic(appId);
		return Res.success(wechatPublic.generateJsSdkSignature(referer));
	}

	@RequestMapping("/refundCoinNotify")
	public ResBody refundCoinNotify(
			@RequestParam(value = "appId") String appId,
			@RequestParam(value = "openId") String openId,
			@RequestParam(value = "nickname") String nickname,
			@RequestParam(value = "orderNumber") String orderNumber,
			@RequestParam(value = "payTime") String payTime,
			@RequestParam(value = "amount") String amount,
			@RequestParam(value = "number") String number,
			@RequestParam(value = "deviceNickname") String deviceId,
			@RequestParam(value = "lastPoint") String lastPoint
	) throws Exception {
		wechatPublicGroup.sendRefundTemplateMsg(appId, openId, nickname, orderNumber, payTime, amount, number, deviceId, lastPoint);
		return Res.success();
	}
}
