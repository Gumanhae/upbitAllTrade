package com.upbit.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;

@Controller
public class PostOrders {

	@PostMapping("/buyingOrders")
	@ResponseBody
    public void buyingOrders(@RequestBody Object list) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
        String accessKey = ""; // 어세스키 입력
        String secretKey = ""; // 시크릿키 입력
        String serverUrl = "https://api.upbit.com";
        
        LinkedHashMap linkedHashMap = (LinkedHashMap) list;
        
        // 매수가
        String accAvg = linkedHashMap.get("accAvg").toString();
        
        ArrayList<LinkedHashMap<String, String>> coinListArr = (ArrayList<LinkedHashMap<String, String>>) linkedHashMap.get("coinList");
        
        for(int i = 0; i < coinListArr.size(); i++) {
            for(Entry<String, String> elem : coinListArr.get(i).entrySet()) {
                if(elem.getKey()=="market") {
                	
                	// 매수처리
                    HashMap<String, String> params = new HashMap<>();
                    params.put("market", elem.getValue());
                    params.put("side", "bid");
//                    params.put("volume", "0.01");
                    params.put("price", accAvg);
                    params.put("ord_type", "price");

                    ArrayList<String> queryElements = new ArrayList<>();
                    for(Map.Entry<String, String> entity : params.entrySet()) {
                        queryElements.add(entity.getKey() + "=" + entity.getValue());
                    }

                    String queryString = String.join("&", queryElements.toArray(new String[0]));

                    MessageDigest md = MessageDigest.getInstance("SHA-512");
                    md.update(queryString.getBytes("UTF-8"));

                    String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

                    Algorithm algorithm = Algorithm.HMAC256(secretKey);
                    String jwtToken = JWT.create()
                            .withClaim("access_key", accessKey)
                            .withClaim("nonce", UUID.randomUUID().toString())
                            .withClaim("query_hash", queryHash)
                            .withClaim("query_hash_alg", "SHA512")
                            .sign(algorithm);

                    String authenticationToken = "Bearer " + jwtToken;

                    
                    
                    try {
                        HttpClient client = HttpClientBuilder.create().build();
                        HttpPost request = new HttpPost(serverUrl + "/v1/orders");
                        request.setHeader("Content-Type", "application/json");
                        request.addHeader("Authorization", authenticationToken);
                        request.setEntity(new StringEntity(new Gson().toJson(params)));

                        Thread.sleep(120);
                        
                        HttpResponse response = client.execute(request);
                        HttpEntity entity = response.getEntity();

                        System.out.println(EntityUtils.toString(entity, "UTF-8"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                    	e.printStackTrace();
                    }
                }
            }
        }
    }
	
	@PostMapping("/sellingOrders")
	@ResponseBody
	public void sellingOrders(@RequestBody Object list) throws NoSuchAlgorithmException, UnsupportedEncodingException {

		String accessKey = "bg2vC4orUos006skdOdL9Ol0a5qcJv4JPPrQdvjZ";
        String secretKey = "3xNgIPhEFoxF1O9zhiX2QoU9e6vHeRZlufBKEKSZ";
        String serverUrl = "https://api.upbit.com";

        ArrayList<LinkedHashMap<String, String>> coinListArr = (ArrayList<LinkedHashMap<String, String>>) list;
        
        for(int i = 1; i < coinListArr.size(); i++) {
        	
        	HashMap<String, String> params = new HashMap<>();
        	
            for(Entry<String, String> elem : coinListArr.get(i).entrySet()) {
                if(elem.getKey() == "currency") {
                	params.put("market", "KRW-" + elem.getValue());
                } else if(elem.getKey() == "balance") {
                	params.put("volume", elem.getValue());
                }
            }
            
        	// 매도처리
            params.put("side", "ask");
//            params.put("price", "100");
            params.put("ord_type", "market");

            ArrayList<String> queryElements = new ArrayList<>();
            for(Map.Entry<String, String> entity : params.entrySet()) {
                queryElements.add(entity.getKey() + "=" + entity.getValue());
            }

            String queryString = String.join("&", queryElements.toArray(new String[0]));

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(queryString.getBytes("UTF-8"));

            String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            String jwtToken = JWT.create()
                    .withClaim("access_key", accessKey)
                    .withClaim("nonce", UUID.randomUUID().toString())
                    .withClaim("query_hash", queryHash)
                    .withClaim("query_hash_alg", "SHA512")
                    .sign(algorithm);

            String authenticationToken = "Bearer " + jwtToken;

            try {
                HttpClient client = HttpClientBuilder.create().build();
                HttpPost request = new HttpPost(serverUrl + "/v1/orders");
                request.setHeader("Content-Type", "application/json");
                request.addHeader("Authorization", authenticationToken);
                request.setEntity(new StringEntity(new Gson().toJson(params)));

                Thread.sleep(120);
                
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();

                System.out.println(EntityUtils.toString(entity, "UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
            	e.printStackTrace();
            }
        }    
        
        
        
        
        
        

    }
}

