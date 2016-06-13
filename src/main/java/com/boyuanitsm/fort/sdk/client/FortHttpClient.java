package com.boyuanitsm.fort.sdk.client;

import com.alibaba.fastjson.JSON;
import com.boyuanitsm.fort.sdk.config.Constants;
import com.boyuanitsm.fort.sdk.config.FortConfiguration;
import com.boyuanitsm.fort.sdk.exception.FortCrudException;
import com.boyuanitsm.fort.sdk.exception.FortNoValidException;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Apache http client.
 *
 * @author zhanghua on 5/16/16.
 */
@Component
public class FortHttpClient {

    private final Logger log = LoggerFactory.getLogger(FortHttpClient.class);

    private String baseUrl;

    private CloseableHttpClient httpClient;
    private HttpClientContext context;
    private RequestConfig requestConfig;
    private CookieStore cookieStore;

    @Autowired
    FortHttpClient(FortConfiguration configuration) {
        this.baseUrl = configuration.getApp().getServerBase();
        httpClient = HttpClients.createDefault();
        context = HttpClientContext.create();
        cookieStore = new BasicCookieStore();
        context.setCookieStore(cookieStore);
        requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Constants.HTTP_TIME_OUT)
                .setConnectTimeout(Constants.HTTP_TIME_OUT)
                .setSocketTimeout(Constants.HTTP_TIME_OUT)
                .build();
    }

    CookieStore loginFortSecurityServer(String url, BasicNameValuePair... pairs) throws FortCrudException {
        postForm(url, pairs);
        return cookieStore;
    }

    /**
     * send post request Content-Type: application/x-www-form-urlencoded
     *
     * @param url   post url
     * @param pairs form params
     * @return response content
     * @throws FortCrudException
     */
    String postForm(String url, BasicNameValuePair... pairs) throws FortCrudException {
        StringBuffer fullPostUrl = new StringBuffer().append(baseUrl).append(url);
        HttpPost post = new HttpPost(fullPostUrl.toString());
        // set form entity
        List<NameValuePair> formParams = new ArrayList<NameValuePair>();
        formParams.addAll(Arrays.asList(pairs));
        formParams.add(new BasicNameValuePair("_csrf", getCookieValue("CSRF-TOKEN")));
        try {
            post.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new FortCrudException(e);
        }
        return send(post);
    }

    /**
     * send post request Content-Type: application/json
     *
     * @param url  post url
     * @param json json
     * @return response content
     * @throws FortCrudException
     */
    String postJson(String url, JSON json) throws FortCrudException {
        StringBuffer fullPostUrl = new StringBuffer().append(baseUrl).append(url);
        HttpPost post = new HttpPost(fullPostUrl.toString());
        // set json string entity
        try {
            post.setEntity(new StringEntity(json.toJSONString()));
        } catch (UnsupportedEncodingException e) {
            throw new FortCrudException(e);
        }
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json;charset=UTF-8");
        post.setHeader("X-CSRF-TOKEN", getCookieValue("CSRF-TOKEN"));

        return send(post);
    }

    /**
     * send post request Content-Type: application/json
     *
     * @param url post url
     * @param obj obj
     * @return response content
     * @throws FortCrudException
     */
    String postJson(String url, Object obj) throws FortCrudException {
        JSON json = (JSON) JSON.toJSON(obj);
        return postJson(url, json);
    }

    /**
     * send put request Content-Type: application/json
     *
     * @param url  put url
     * @param json json
     * @return response content
     * @throws FortCrudException
     */
    String putJson(String url, JSON json) throws FortCrudException {
        StringBuffer fullPostUrl = new StringBuffer().append(baseUrl).append(url);
        HttpPut put = new HttpPut(fullPostUrl.toString());
        // set json string entity
        try {
            put.setEntity(new StringEntity(json.toJSONString()));
        } catch (UnsupportedEncodingException e) {
            throw new FortCrudException(e);
        }
        put.setHeader("Accept", "application/json");
        put.setHeader("Content-type", "application/json;charset=UTF-8");
        return send(put);
    }

    /**
     * send put request Content-Type: application/json
     *
     * @param url put url
     * @param obj obj
     * @return response content
     * @throws FortCrudException
     */
    String putJson(String url, Object obj) throws FortCrudException {
        JSON json = (JSON) JSON.toJSON(obj);
        return putJson(url, json);
    }

    /**
     * send get request
     *
     * @param url   request url
     * @param pairs url params
     * @return response content
     * @throws FortCrudException
     */
    String get(String url, BasicNameValuePair... pairs) throws FortCrudException {
        String urlParams = URLEncodedUtils.format(Arrays.asList(pairs), "UTF-8");
        if (!url.endsWith("?")) {
            url += "?";
        }
        StringBuffer fullGetUrl = new StringBuffer().append(baseUrl).append(url).append(urlParams);

        HttpGet get = new HttpGet(fullGetUrl.toString());
        return send(get);
    }

    String delete(String url) throws FortCrudException {
        StringBuffer fullDeleteUrl = new StringBuffer().append(baseUrl).append(url);
        HttpDelete delete = new HttpDelete(fullDeleteUrl.toString());
        return send(delete);
    }

    /**
     * send http request
     *
     * @param request http request base
     * @return response content
     * @throws FortCrudException
     */
    private String send(HttpRequestBase request) throws FortCrudException {
        // Make sure cookie headers are written
        RequestAddCookies addCookies = new RequestAddCookies();
        CloseableHttpResponse response = null;
        try {
            request.setConfig(requestConfig);
            addCookies.process(request, context);
            // send request
            response = httpClient.execute(request, context);
            // validate http status code
            isSuccess(response, null);
            // get response content
            return EntityUtils.toString(response.getEntity());
        } catch (SocketTimeoutException e) {
            // socket time out , reconnection...
            log.warn("socket time out!", e.getMessage());
            return null;
        } catch (HttpException | IOException e) {
            throw new FortCrudException(e);
        } finally {
            // close response
            try {
                response.close();
            } catch (IOException e) {
                log.warn("close http response error!", e.getMessage());
            }
        }
    }

    /**
     * get cookie value from cookie store
     *
     * @param cookieName the cookie name
     * @return if not found return null else cookie value
     */
    private String getCookieValue(String cookieName) {
        String value = null;
        for (Cookie cookie : cookieStore.getCookies()) {
            if (cookie.getName().equals(cookieName)) {
                value = cookie.getValue();
                break;
            }
        }
        return value;
    }

    /**
     * is request success
     *
     * @param response http response
     * @param errMsg   error message
     * @throws FortCrudException
     */
    private void isSuccess(CloseableHttpResponse response, String errMsg) throws FortCrudException {
        if (errMsg == null)
            errMsg = "";

        int statusCode = response.getStatusLine().getStatusCode();

        switch (statusCode) {
            case 200:
            case 201:
                break;
            case 400:
                String fortAppError = response.getFirstHeader("X-fortApp-error").getValue();
                throw new FortNoValidException(fortAppError);
            default:
                throw new FortCrudException(String.format("http code is no ok! is %s. %s", statusCode, errMsg));
        }
    }
}
