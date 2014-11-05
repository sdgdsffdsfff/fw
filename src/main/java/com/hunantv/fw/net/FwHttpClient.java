package com.hunantv.fw.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class FwHttpClient {

	public static FwHttpResponse get(String url) throws Exception {
		return get(url, null);
	}

	public static FwHttpResponse get(String url, Map<String, Object> params) throws Exception {
		return get(url, null, -1);
	}

	public static FwHttpResponse get(String url, Map<String, Object> params, int connTimeoutSec) throws Exception {
		URLParser urlParser = new URLParser(buildURL(url));
		urlParser.addQuery(params);

		HttpGet httpGet = new HttpGet(urlParser.getFullUrl());
		if (connTimeoutSec > 0) {
			RequestConfig config = RequestConfig.custom().setSocketTimeout(connTimeoutSec * 1000)
			        .setConnectTimeout(connTimeoutSec * 1000).build();
			httpGet.setConfig(config);
		}
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response = client.execute(httpGet);
		try {
			return new FwHttpResponse(response.getStatusLine().getStatusCode(), getContent(response));
		} finally {
			response.close();
		}
	}

	public static FwHttpResponse post(String url) throws Exception {
		return post(url, null);
	}

	public static FwHttpResponse post(String url, Map<String, Object> params) throws Exception {
		return post(url, null, -1);
	}

	public static FwHttpResponse post(String url, Map<String, Object> params, int connTimeoutSec) throws Exception {

		URLParser urlParser = new URLParser(buildURL(url));
		HttpPost httpPost = new HttpPost(urlParser.getFullUrl());

		if (connTimeoutSec > 0) {
			RequestConfig config = RequestConfig.custom().setSocketTimeout(connTimeoutSec * 1000)
			        .setConnectTimeout(connTimeoutSec * 1000).build();
			httpPost.setConfig(config);
		}
		if (null != params) {
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			for (Iterator<String> iter = params.keySet().iterator(); iter.hasNext();) {
				String key = iter.next();
				Object obj = params.get(key);
				String value = "";
				if (null != obj)
					value = obj.toString();

				formparams.add(new BasicNameValuePair(key, value));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
			httpPost.setEntity(entity);
		}
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response = client.execute(httpPost);
		try {
			return new FwHttpResponse(response.getStatusLine().getStatusCode(), getContent(response));
		} finally {
			response.close();
		}
	}

	private static URL buildURL(String url) throws MalformedURLException {
		URL u = null;
		try {
			u = new URL(url);
		} catch (java.net.MalformedURLException ex) {
			u = new URL("http://" + url);
		}
		return u;
	}

	private static String getContent(HttpResponse res) throws Exception {
		StringBuilder strb = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));

		strb = new StringBuilder();
		String line = null;
		while (null != (line = rd.readLine())) {
			strb.append(line).append("\n");
		}
		return strb.toString();
	}

	// public static void main(String[] args) throws Exception {
	// FwHttpResponse res = FwHttpClient.get("localhost:3333/user/list");
	// System.out.println(res.body);
	// System.out.println(res.code);
	// System.out.println("***************************");
	//
	// System.out.println(res.body);
	// System.out.println(res.code);
	// }
}
