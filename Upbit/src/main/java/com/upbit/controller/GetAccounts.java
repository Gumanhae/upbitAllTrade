package com.upbit.controller;

import java.io.IOException;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@RestController
public class GetAccounts {

	@GetMapping("/getAccounts")
	@ResponseBody
    public String getAccounts() {
        String accessKey = ""; // 어세스키 입력
        String secretKey = ""; // 시크릿키 입력
        String serverUrl = "https://api.upbit.com";

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String jwtToken = JWT.create()
                .withClaim("access_key", accessKey)
                .withClaim("nonce", UUID.randomUUID().toString())
                .sign(algorithm);

        String authenticationToken = "Bearer " + jwtToken;
        System.out.println("토큰 발급성공 : " + authenticationToken);
        
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(serverUrl + "/v1/accounts");
            request.setHeader("Content-Type", "application/json");
            request.addHeader("Authorization", authenticationToken);

            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();

//            System.out.println(EntityUtils.toString(entity, "UTF-8"));
            
            return EntityUtils.toString(entity, "UTF-8");
//            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}