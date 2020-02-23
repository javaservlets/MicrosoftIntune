package com.example.microsoft;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import org.forgerock.util.encode.Base64;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.sun.identity.idm.AMIdentityRepository.debug;

public class UserInfo {
    String access_token;

    // we'll start by getting a short lived access token, since it'll be needed later when we getStatus on the device's compliancy
    public UserInfo(String server, String scope, String usr, String pwd, String client_id, String client_secret) { // only setting vals n via the constructor to make scratch testing as 'real' as possible
        this.access_token=getToken(server, scope, usr, pwd, client_id, client_secret); // THIS IS NOT CORRECT SO GENERATE REAL TOKEN
    }

    public String getToken(String token_url, String scope, String usr, String pwd, String client_id, String client_secret) { //
        HttpPost http_post=null;
        String cook="";
        try {
            HttpClient httpclient=HttpClients.createDefault();
            http_post=new HttpPost(token_url);
            http_post.setHeader("Accept", "application/json");
            http_post.setHeader("Accept", "*/*");
            http_post.setHeader("Cache-Control", "no-cache");
            http_post.setHeader("Host", "login.microsoftonline.com");
            http_post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            http_post.setHeader("Connection", "keep-alive");
//            http_post.setHeader("Accept-Encoding", "gzip, deflate");
//            http_post.setHeader("Content-Length", "275");
//            http_post.setHeader("Cookie", "x-ms-gateway-slice=prod; stsservicecookie=ests; fpc=AtHZFoCUNT9JpXi2C8G9irlG0RR2AQAAAHOtV9UOAAAA");

            List<NameValuePair> params=new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("scope", scope));
            params.add(new BasicNameValuePair("username", usr));
            params.add(new BasicNameValuePair("password", pwd));
            params.add(new BasicNameValuePair("client_id", client_id));
            params.add(new BasicNameValuePair("client_secret", client_secret));

            params.add(new BasicNameValuePair("grant_type", "password"));
            params.add(new BasicNameValuePair("Content-Type", "application/x-www-form-urlencoded"));

            http_post.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response=httpclient.execute(http_post);
            HttpEntity responseEntity=response.getEntity();

            if (responseEntity != null) {
                //log("userInfo.getToken: " + EntityUtils.toString(responseEntity));
                cook = stripNoise(EntityUtils.toString(responseEntity), "access_token");
                log(cook);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return cook;
        }
    }

    public String getStatus(String query_url, String device_id) { // we'll query the MS Graph at this endpoint and get back a compliance state
        String compliance_status="";
        URL url=null;
        HttpGet http_get=null;

        try {
            HttpClient httpclient=HttpClients.createDefault();
            http_get=new HttpGet(query_url + device_id);
            http_get.setHeader("Authorization", this.access_token); // latter was retrieved on class instantiation

            HttpResponse response=httpclient.execute(http_get);
            HttpEntity responseEntity=response.getEntity();
            if (responseEntity != null) {
                String entity_str=EntityUtils.toString(responseEntity);
                //log(" userInfo. getStatus: " + entity_str);
                if (entity_str.toLowerCase().contains("compliant")) { //graph api returns complianceState: compliant
                    compliance_status="compliant";
                } else if (entity_str.toLowerCase().contains("notfound")) {
                    //graph api returns complianceState: unknown (StripNoise() (below) might be a more thorough check but this seems to work as is
                    compliance_status="noncompliant";
                } else { // if (entity_str.contains("error")) { // if device is unknown this what MS returns (btw do not check httpStatus since srv can throw diff codes n this situation
                    compliance_status="unknown";
                }
            } else {
                compliance_status="connection error";
            }
            log("compliance state? " + compliance_status);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compliance_status;
    }

    private String stripQuote(String val) {
        return (val.replace("\"", ""));
    }

    private static String stripNoise(String parent, String child) {
        String noise="";
        try {
            JSONObject jobj=new JSONObject(parent);
            Object idtkn=jobj.getString(child);
            noise=idtkn.toString();

            if (noise.startsWith("[")) { // get only 'value' from "["value"]"
                noise=noise.substring(1, noise.length() - 1);
            }
            if (noise.startsWith("\"")) {
                noise=noise.substring(1, noise.length() - 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return noise;
        }
    }


    public static void log(String str) {
        System.out.println("+++  userInfo:   " + str);
        // debug.error("+++      " + str); //rj? should be 'message' instead?
    }
}