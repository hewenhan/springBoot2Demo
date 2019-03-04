package com.yd.spring.wechatCenter.libs.springReqClient;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpringReqClient {
	public static <T> HashMap getForJson(String url, HashMap<String, T> params) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

		UriComponentsBuilder builder = buildUrl(url, params);

		HttpEntity<?> entity       = new HttpEntity<>(headers);
		RestTemplate  restTemplate = new RestTemplate();
		ResponseEntity<HashMap> result = restTemplate.exchange(
				builder.toUriString(),
				HttpMethod.GET,
				entity, HashMap.class);

		return result.getBody();
	}

	public static <T> HashMap postForJson(String url, HashMap<String, T> params) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		UriComponentsBuilder builder = buildUrl(url);

		HttpEntity<?> entity       = new HttpEntity<>(params, headers);
		RestTemplate  restTemplate = new RestTemplate();
		ResponseEntity<HashMap> result = restTemplate.exchange(
				builder.toUriString(),
				HttpMethod.POST,
				entity, HashMap.class);

		return result.getBody();
	}

	private static UriComponentsBuilder buildUrl(String url) throws Exception {
		UriComponentsBuilder builder = preBuildUrl(url);
		return builder;
	}

	private static <T> UriComponentsBuilder buildUrl(String url, HashMap<String, T> params) throws Exception {
		UriComponentsBuilder builder = preBuildUrl(url);
		for (String name : params.keySet()) {
			T value = params.get(name);
			builder.queryParam(name, value);
		}
		return builder;
	}

	private static UriComponentsBuilder preBuildUrl(String url) throws Exception {
		URL    parsedUrl = new URL(url);
		String port      = "";
		if (parsedUrl.getPort() != -1) {
			port = ":" + parsedUrl.getPort();
		}
		String               parsedUrlStr = parsedUrl.getProtocol() + "://" + parsedUrl.getHost() + port + parsedUrl.getPath();
		UriComponentsBuilder builder      = UriComponentsBuilder.fromHttpUrl(parsedUrlStr);

		Map<String, String[]> queryMap = parseUrlQueryString(parsedUrl.getQuery());

		for (String name : queryMap.keySet()) {
			String[] valueArr = queryMap.get(name);
			name = name.replaceAll("[\\[\\d\\]]", "");
			for (String value : valueArr) {
				builder.queryParam(name, value);
			}
		}

		return builder;
	}

	private static Map<String, String[]> parseUrlQueryString(String s) {
		if (s == null) return new HashMap<String, String[]>(0);
		// In map1 we use strings and ArrayLists to collect the parameter values.
		HashMap<String, Object> map1 = new HashMap<String, Object>();
		int                     p    = 0;
		while (p < s.length()) {
			int p0 = p;
			while (p < s.length() && s.charAt(p) != '=' && s.charAt(p) != '&') p++;
			String name = urlDecode(s.substring(p0, p));
			if (p < s.length() && s.charAt(p) == '=') p++;
			p0 = p;
			while (p < s.length() && s.charAt(p) != '&') p++;
			String value = urlDecode(s.substring(p0, p));
			if (p < s.length() && s.charAt(p) == '&') p++;
			Object x = map1.get(name);
			if (x == null) {
				// The first value of each name is added directly as a string to the map.
				map1.put(name, value);
			} else if (x instanceof String) {
				// For multiple values, we use an ArrayList.
				ArrayList<String> a = new ArrayList<String>();
				a.add((String) x);
				a.add(value);
				map1.put(name, a);
			} else {
				@SuppressWarnings("unchecked")
				ArrayList<String> a = (ArrayList<String>) x;
				a.add(value);
			}
		}
		// Copy map1 to map2. Map2 uses string arrays to store the parameter values.
		HashMap<String, String[]> map2 = new HashMap<String, String[]>(map1.size());
		for (Map.Entry<String, Object> e : map1.entrySet()) {
			String   name = e.getKey();
			Object   x    = e.getValue();
			String[] v;
			if (x instanceof String) {
				v = new String[]{(String) x};
			} else {
				@SuppressWarnings("unchecked")
				ArrayList<String> a = (ArrayList<String>) x;
				v = new String[a.size()];
				v = a.toArray(v);
			}
			map2.put(name, v);
		}
		return map2;
	}

	private static String urlDecode(String s) {
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Error in urlDecode.", e);
		}
	}
}
