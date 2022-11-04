package com.example.java;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.*;
import com.google.gson.Gson;

import javax.annotation.PostConstruct;

@RestController
public class DemoController {
    String base_url = "";
    String token = "";
    int cdi = 0;
    int actor_id = 0;

    @PostConstruct
    public void init() throws IOException {
        File file = ResourceUtils.getFile("classpath:static/config.json");
        InputStream is = new FileInputStream(file);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> credentials = mapper.readValue(is,Map.class);

        base_url = (String)credentials.get("base_url_swagger");
        token = (String)credentials.get("token");
        cdi = (int)credentials.get("cdi");
        actor_id = (int)credentials.get("actor_id");

    }

/**
 * @param c_id
 * @return
 * @throws IOException
 */
// [GET] Check a contract's status
    @GetMapping("/status")
    public String getStatus(@RequestParam(value = "id") String c_id) throws IOException {
        HttpGet request = new HttpGet(base_url + "/contracts/" + c_id + "/transaction/");
        request.addHeader("j_token", token);
        request.addHeader("accept", "application/json");

        String result = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request);) {
            HttpEntity entity = response.getEntity();
            if (entity != null)  {
                result = EntityUtils.toString(entity);
            }
            else {
                result = "Error - contract not found";
            }
        }
        return result;
    }

    @GetMapping("/createcontractallinone")
    public String mynewtest() throws IOException {
        String url = base_url + "/contracts/allinone?start=true";

        HttpPost request = new HttpPost(url);
        request.addHeader("accept", "application/json");
        request.addHeader("j_token", token);
        //request.addHeader("Content-Type", "application/json");
        //request.addHeader("Content-Type", "multipart/form");

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        String recip1 = "{\"civility\": \"MONSIEUR\",\"firstname\": \"Khalil\",\"lastname\": \"BHS\",\"address_1\": \"Rue de test\",\"address_2\": \"\",\"postal_code\": \"20156\",\"city\": \"\",\"cell_phone\": \"0021651353584\",\"email\": \"k.salah@oodrive.com\",\"signature_mode\": 10}";
        String recip2 = "{\"civility\": \"MONSIEUR\",\"firstname\": \"Olivier\",\"lastname\": \"Mathieu\",\"cell_phone\": \"0021651353584\",\"email\": \"omathieu+3254@calindasoftware.com\",\"signature_mode\": 10}";
        builder.addTextBody("contract", "{\"name\": \"contract.pdf\",\"contract_definition_id\":" + cdi + ",\"vendor_email\": \"fo@calindasoftware.com\",\"message_title\": \"Votre document pour signature\",\"message_body\": \"Vous êtes signataire du contrat ci-après\",\"keep_on_move\": false, \"auto_close\": 1}");
        builder.addTextBody("recipients", "{\"data\": [" + recip1 + "," + recip2 + "]}");
        File file = ResourceUtils.getFile("classpath:static/pacte-new.pdf");
        InputStream allinone = new FileInputStream(file);
        builder.addBinaryBody("pdfparts", allinone, ContentType.create("application/pdf"), file.getName());

        HttpEntity multipart = builder.build();
        request.setEntity(multipart);

        String result = "";
            
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse httpResponse = httpClient.execute(request);) {
                HttpEntity response_entity = httpResponse.getEntity();
                if (response_entity != null) {
                    result = EntityUtils.toString(response_entity);
                }
                else {
                    result = "{\"Error\":\"An error occured\"}";
                }
            }
        return result;
    }
//[POST] Create a temporary token
/**
 * @param cid
 * @param recipientid 
 * @return
 * @throws ParseException
 * @throws IOException
 */
    @GetMapping(value="/generatetoken")
    public String temptoken(@RequestParam String cid, @RequestParam String recipientid) throws ParseException, IOException {
        String url = "https://api.sellandsign.com/api/v4/contracts/" + cid + "/transaction/temporarytoken";

        HttpPost request = new HttpPost(url);
        request.addHeader("accept", "application/json");
        request.addHeader("j_token", token);
        request.addHeader("Content-Type", "application/json");
        
        HashMap<String, Object> mypayload = new HashMap<String, Object>();
        mypayload.put("actor_id", actor_id);
        mypayload.put("contract_definition_id", cdi);
        mypayload.put("recipient_id", Integer.parseInt(recipientid));
        mypayload.put("validity_duration", 1667170800);
        var gson = new Gson();
        String jpayload = gson.toJson(mypayload);
        StringEntity se = new StringEntity(jpayload, "UTF-8");
        request.setEntity(se);

        String result = "";
            
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse httpResponse = httpClient.execute(request);) {
                HttpEntity response_entity = httpResponse.getEntity();
                if (response_entity != null) {
                    result = EntityUtils.toString(response_entity);
                }
                else {
                    result = "{\"Error\":\"An error occured\"}";
                }
            }
        return result;
    }
}
