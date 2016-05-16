package com.boyuanitsm.fortsdk.client;

import com.boyuanitsm.fortsdk.config.SecurityConfiguration;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A security client. packaging fort http interface.
 *
 * @author zhanghua on 5/16/16.
 */
public class SecurityClient {

    private static final String API_ALL_SECURITY_RESOURCE_ENTITIES = "/api/security-resource-entities";

    private static final String API_LOGIN_SECURITY_SERVER = "/api/authentication";


    CloseableHttpClient httpClient = HttpClients.createDefault();

    private void loginSecurityServer(String appKey, String secret) throws IOException {
        HttpPost post = new HttpPost(SecurityConfiguration.BASE_SERVER_HOST + API_LOGIN_SECURITY_SERVER);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("j_username", appKey));
        formparams.add(new BasicNameValuePair("j_password", appKey));
        formparams.add(new BasicNameValuePair("remember-me", "true"));
        formparams.add(new BasicNameValuePair("submit", "Login"));

        try {
            post.setEntity(new UrlEncodedFormEntity(formparams, "UTF-8"));
            CloseableHttpResponse response = httpClient.execute(post);
            System.out.println(Arrays.toString(response.getAllHeaders()));
            HttpEntity entity = response.getEntity();
            System.out.println(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SecurityClient client = new SecurityClient();
        try {
            client.loginSecurityServer(SecurityConfiguration.APP_KEY, SecurityConfiguration.APP_SECRET);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
