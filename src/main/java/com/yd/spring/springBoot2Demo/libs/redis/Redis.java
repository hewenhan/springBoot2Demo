package com.yd.spring.springBoot2Demo.libs.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

@Component
public class Redis {

	private String    host;
	private String    password;
	private int       port;
	private int       db;
	private JedisPool jedisPool;

	Redis(
			@Value("${redis.host}") String host,
			@Value("${redis.password}") String password,
			@Value("${redis.port}") int port,
			@Value("${redis.db}") int db
	) {
		this.host = host;
		this.password = password;
		this.port = port;
		this.db = db;

		this.jedisPool = new JedisPool(
				buildPoolConfig(),
				this.host,
				this.port,
				Protocol.DEFAULT_TIMEOUT,
				this.password,
				this.db);
		System.out.println("REDIS INITIALIZED");
	}

	public String get(String key) {
		return this.exec("GET", new Object[]{key});
	}

	public String set(String key, String value) {
		return this.exec("SET", new Object[]{key, value});
	}

	public String set(String key, String value, Long expireTime) {
		String result = this.set(key, value);
		this.expire(key, expireTime);
		return result;
	}

	public Long del(String... keys) {
		return this.exec("DEL", keys);
	}

	public Set<String> keys(String keyPattern) {
		return this.exec("KEYS", new Object[]{keyPattern});
	}

	public Long expire(String key, Long expireTime) {
		return this.exec("EXPIRE", new Object[]{key, expireTime});
	}

	public Long ttl(String key) {
		return this.exec("TTL", new Object[]{key});
	}

	public Long hset(String key, String field, String value) {
		return this.exec("HSET", new Object[]{key, field, value});
	}

	public Long hsetMap(String key, Map<String, String> map) {
		for (String k : map.keySet()) {
			this.exec("HSET", new Object[]{key, k, map.get(k)});
		}
		return 1L;
	}

	public String hget(String key, String field) {
		return this.exec("HGET", new Object[]{key, field});
	}

	public Map<String, String> hgetall(String key) {
		return this.exec("HGETALL", new Object[]{key});
	}

	public Long hdel(String key, String... fields) {
		Object[] args = new Object[fields.length + 1];
		args[0] = key;
		for (int i = 0; i < fields.length; i++) {
			args[i + 1] = fields[i];
		}
		return this.exec("HDEL", args);
	}

	public Jedis getConnection() {
		try {
			return jedisPool.getResource();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("REDIS GET CONNECTION ERROR");
		}
	}

	public <T> T exec(String operation, Object[] args) {
		Jedis conn = null;
		try {
			conn = jedisPool.getResource();
			return operationSwitch(conn, operation, args);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	private <T> T operationSwitch(Jedis conn, String operation, Object[] args) {
		operation = operation.toUpperCase();

		switch (operation) {
			case "GET":

				return (T) conn.get(args[0].toString());
			case "SET":

				return (T) conn.set(args[0].toString(), args[1].toString());
			case "DEL":

				String[] keysArr = Arrays.copyOf(args, args.length, String[].class);
				return (T) conn.del(keysArr);
			case "KEYS":

				return (T) conn.keys(args[0].toString());
			case "EXPIRE":

				return (T) conn.expire(args[0].toString(), Integer.parseInt(args[1].toString()));
			case "TTL":

				return (T) conn.ttl(args[0].toString());

			case "HSET":

				return (T) conn.hset(args[0].toString(), args[1].toString(), args[2].toString());
			case "HGET":

				return (T) conn.hget(args[0].toString(), args[1].toString());
			case "HGETALL":

				return (T) conn.hgetAll(args[0].toString());
			case "HDEL":

				String[] fieldsArr = Arrays.copyOfRange(args, 1, args.length, String[].class);
				return (T) conn.hdel(args[0].toString(), fieldsArr);
			case "ZADD":

				return (T) conn.zadd(args[0].toString(), Long.parseLong(args[1].toString()), args[2].toString());
			case "ZREM":

				String[] members = Arrays.copyOfRange(args, 1, args.length, String[].class);
				return (T) conn.zrem(args[0].toString(), members);
			case "ZRANK":

				return (T) conn.zrank(args[0].toString(), args[1].toString());
			case "ZRANGE":

				if (args.length == 2) {
					return (T) conn.zrange(args[0].toString(), Long.parseLong(args[1].toString()), Long.parseLong(args[2].toString()));
				} else if (args.length == 3) {
					return (T) conn.zrangeByScoreWithScores(args[0].toString(), Long.parseLong(args[1].toString()), Long.parseLong(args[2].toString()));
				}
				throw new IllegalArgumentException("ZRANGE PARAMS ERROR");
			case "ZRANGEBYSCORE":

				if (args.length == 2) {
					return (T) conn.zrangeByScore(args[0].toString(), Long.parseLong(args[1].toString()), Long.parseLong(args[2].toString()));
				} else if (args.length == 3) {
					return (T) conn.zrangeByScoreWithScores(args[0].toString(), Long.parseLong(args[1].toString()), Long.parseLong(args[2].toString()));
				}
				throw new IllegalArgumentException("ZRANGEBYSCORE PARAMS ERROR");
			case "ZREVRANGE":

				if (args.length == 2) {
					return (T) conn.zrevrange(args[0].toString(), Long.parseLong(args[1].toString()), Long.parseLong(args[2].toString()));
				} else if (args.length == 3) {
					return (T) conn.zrevrangeWithScores(args[0].toString(), Long.parseLong(args[1].toString()), Long.parseLong(args[2].toString()));
				}
				throw new IllegalArgumentException("ZRANGE PARAMS ERROR");
			case "ZREVRANGEBYSCORE":

				if (args.length == 2) {
					return (T) conn.zrevrangeByScore(args[0].toString(), Long.parseLong(args[1].toString()), Long.parseLong(args[2].toString()));
				} else if (args.length == 3) {
					return (T) conn.zrevrangeByScoreWithScores(args[0].toString(), Long.parseLong(args[1].toString()), Long.parseLong(args[2].toString()));
				}
				throw new IllegalArgumentException("ZRANGEBYSCORE PARAMS ERROR");
			case "ZSCORE":

				return (T) conn.zscore(args[0].toString(), args[1].toString());
			default:

				throw new IllegalArgumentException("COMMAND ERROR");
		}
	}

	private JedisPoolConfig buildPoolConfig() {
		final JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(128);
		poolConfig.setMaxIdle(128);
		poolConfig.setMinIdle(16);
		return poolConfig;
	}
}
