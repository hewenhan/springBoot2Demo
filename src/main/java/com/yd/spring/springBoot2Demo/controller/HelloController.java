package com.yd.spring.springBoot2Demo.controller;

import com.yd.spring.springBoot2Demo.libs.mysql.Crud;
import com.yd.spring.springBoot2Demo.libs.mysql.UpdateResult;
import com.yd.spring.springBoot2Demo.libs.redis.Redis;
import com.yd.spring.springBoot2Demo.libs.springReqClient.SpringReqClient;
import com.yd.spring.springBoot2Demo.model.result.Res;
import com.yd.spring.springBoot2Demo.model.result.ResBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/hello")
public class HelloController<T> {

	@Autowired
	private Crud  mysql;
	@Autowired
	private Redis redis;

	HelloController() {

	}

	@RequestMapping("/getString")
	public String getString(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "Hello World";
	}

	@RequestMapping("/helloWorld")
	public ResBody helloWorld(@RequestParam(value = "name", defaultValue = "World") String name) {
		return Res.success("Hello World");
	}

	@RequestMapping("/resultSuccess")
	public ResBody resultSuccess(@RequestParam(value = "name", defaultValue = "World") String name) {
		HashMap res = new HashMap<>();
		res.put("name", name);
		return Res.success(res);
	}

	@RequestMapping("/resultFailed")
	public ResBody resultFailed(@RequestParam(value = "name", defaultValue = "World") String name) {
		return Res.error("ERROR");
	}

	@RequestMapping("/mysqlSelectQuery")
	public ResBody mysqlSelectQuery() throws Exception {
		HashMap[] rows = mysql.selectQuery("SELECT * FROM test.users");
		return Res.success(rows);
	}

	@RequestMapping("/mysqlSelectQueryFailed")
	public ResBody mysqlSelectQueryFailed() throws Exception {
		HashMap[] rows = mysql.selectQuery("SELECT * FROM test.usersaa");
		return Res.success(rows);
	}

	@RequestMapping("/mysqlInsertQuery")
	public ResBody mysqlInsertQuery() throws Exception {
		UpdateResult results = mysql.updateQuery("INSERT INTO `test`.`users` (`username`, `password`) VALUES ('a', 'b')");
		return Res.success(results);
	}

	@RequestMapping("/mysqlUpdateQuery")
	public ResBody mysqlUpdateQuery() throws Exception {
		UpdateResult results = mysql.updateQuery("UPDATE `test`.`users` SET `status`='1' WHERE `status`='0'");
		return Res.success(results);
	}

	@RequestMapping("/mysqlDeleteQuery")
	public ResBody mysqlDeleteQuery() throws Exception {
		UpdateResult results = mysql.updateQuery("DELETE FROM `test`.`users` WHERE `username`='a'");
		return Res.success(results);
	}

	@RequestMapping("/redisSet")
	public ResBody redisSet() throws Exception {
		String results = redis.set("foo", "bar");
		return Res.success(results);
	}

	@RequestMapping("/redisSetExpire")
	public ResBody redisSetExpire() throws Exception {
		String results = redis.set("foo", "bar", 10L);
		return Res.success(results);
	}

	@RequestMapping("/redisGet")
	public ResBody redisGet() {
		String results = redis.get("foo");
		return Res.success(results);
	}

	@RequestMapping("/redisDel")
	public ResBody redisDel() {
		Long results = redis.del("foo");
		return Res.success(results);
	}

	@RequestMapping("/redisKeys")
	public ResBody redisKeys() {
		Set<String> results = redis.keys("*");
		return Res.success(results);
	}

	@RequestMapping("/redisHsetMap")
	public ResBody redisHsetMap() {
		HashMap map = new HashMap();
		map.put("hello", "world");
		map.put("a", "1");
		map.put("b", "2");
		Long results = redis.hsetMap("foo", map);
		return Res.success(results);
	}

	@RequestMapping("/redisHgetall")
	public ResBody redisHgetall() {
		Map<String, String> results = redis.hgetall("foo");
		return Res.success(results);
	}

	@RequestMapping("/redisHdel")
	public ResBody redisHdel() {
		Long results = redis.hdel("foo", "a", "b");
		return Res.success(results);
	}

	@RequestMapping("/getAnotherApi")
	public ResBody getAnotherApi() throws Exception {
		String                  url  = "http://localhost:8080/mysqlSelectQuery";
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("get", "getHaha");

		HashMap res = SpringReqClient.getForJson(url, data);

		return Res.success(res);
	}

	@RequestMapping("/postAnotherApi")
	public ResBody postAnotherApi() throws Exception {
		String url = "http://localhost:3001/api/test?a=1&b=2&arr[]=1&arr[]=2";

		HashMap<String, String> data = new HashMap<String, String>();
		data.put("bbb", "222");

		HashMap resNode = SpringReqClient.postForJson(url, data);


		url = "http://localhost:8080/getParams?a=1&b=2&arr[]=1&arr[]=2";

		HashMap resJava = SpringReqClient.postForJson(url, data);

		HashMap result = new HashMap();
		result.put("resNode", resNode);
		result.put("resJava", resJava);

		return Res.success(result);
	}

	@RequestMapping("/getParams")
	public ResBody getParams(@RequestParam HashMap getParams, @RequestBody HashMap postParams) {
		HashMap resConetent = new HashMap();
		resConetent.put("getParams", getParams);
		resConetent.put("postParams", postParams);
		return Res.success();
	}
}
