package com.example.java;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


@RestController
public class DemoController {
    //String base_url = "https://luhman.calindasoftware.com/api/v4";
    String base_url = "https://api.sellandsign.com/api/v4";
    String token = "5699057|a1TmrUbPfBhv1P3CMqSoi4H5m3c/wIK45StECmBRuKI=";
    long cdi = 34422;
    long actor_id = 1209262;
   

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

// [GET] List all recipients
    @GetMapping("/test2")
    public String simpleTest() throws ClientProtocolException, IOException {
        String url = "https://api.sellandsign.com/api/v4/recipients";

        HttpGet request = new HttpGet(url);
        request.addHeader("accept", "application/json");
        request.addHeader("j_token", token);

        String result = "";
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse httpResponse = httpClient.execute(request);) {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity);
                }
                else {
                    result = "{\"Error\":\"An error occured\"}";
                }
             }

        return result;
    }

    //[POST] Create a contract step by step
@GetMapping("/createcontract")
public String khalilTest() throws IOException {
    String url = "https://api.sellandsign.com/api/v4/contracts";

    HttpPost request = new HttpPost(url);
    request.addHeader("accept", "application/json");
    request.addHeader("j_token", token);
    request.addHeader("Content-Type", "application/json");

    HashMap<String, Object> payload = new HashMap<String, Object>();
    payload.put("name", "khalilcontract.pdf");
    payload.put("contract_definition_id", 34422);
    payload.put("vendor_email", "fo@calindasoftware.com");
    payload.put("auto_close", 1);
    var gson = new Gson();
    String jpayload = gson.toJson(payload);

    //String payload = "{\"name\": \"khalilcontract.pdf\",\"contract_definition_id\": 34422,\"vendor_email\": \"fo@calindasoftware.com\",\"message_title\": \"L'Agence Telecom: Votre document pour signature\",\"message_body\": \"Vous êtes signataire du contrat ci-après\",\"keep_on_move\": false, \"auto_close\": 1}";
    StringEntity se = new StringEntity(jpayload, "UTF-8");
    request.setEntity(se);
    //MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    /* builder.addTextBody("contract", "{\"name\": \"khalilcontract.pdf\",\"contract_definition_id\": 34422,\"vendor_email\": \"test-l-agence-telecom-sandbox-fo@calindasoftware.com\",\"message_title\": \"L'Agence Telecom: Votre document pour signature\",\"message_body\": \"Vous êtes signataire du contrat ci-après\\nCordialement\",\"keep_on_move\": false, \"perimeters\", \"auto_close\": 1}");
    File file = new File("/Users/k.salah/Desktop/1/khalilcontract.pdf");
    FileBody fileBody = new FileBody(file);
    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
    builder.addPart("khalilcontract.pdf", fileBody);
    HttpEntity entity = builder.build();
    request.setEntity(entity); */
  
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
    
    int i = 0;
    Map<String, Object> jresult = gson.fromJson(result, Map.class);
    Double cid = (Double)jresult.get("contract_id");
    int icid = cid.intValue();
    System.out.println(String.valueOf(icid));

;
    return result;
    
}
//[POST] Add a document to a created contract

@GetMapping(value="/adddocument")
public String addcontract(@RequestParam String cid) throws ParseException, IOException {
    String url = "https://api.sellandsign.com/api/v4/contracts/" + cid + "/documents/";

    HttpPost request = new HttpPost(url);
    request.addHeader("accept", "application/json");
    request.addHeader("j_token", token);
    
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    
    File file = ResourceUtils.getFile("classpath:static/contract.pdf");
    InputStream addcontract = new FileInputStream(file);
    builder.addBinaryBody("file", addcontract, ContentType.create("application/pdf"), file.getName());
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
    
    HashMap<String, Object> mypayload = new HashMap<String, Object>();
    mypayload.put("actor_id", 1209262);
    mypayload.put("contract_definition_id", 34422);
    mypayload.put("recipient_id", recipientid);
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


@GetMapping("/createcontractallinone")
public String mynewtest() throws IOException {
    String url = "https://api.sellandsign.com/api/v4/contracts/allinone?start=true";

    HttpPost request = new HttpPost(url);
    request.addHeader("accept", "application/json");
    request.addHeader("j_token", token);
    //request.addHeader("Content-Type", "application/json");
    //request.addHeader("Content-Type", "multipart/form");

    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.addTextBody("contract", "{\"name\": \"contract.pdf\",\"contract_definition_id\": 34422,\"vendor_email\": \"fo@calindasoftware.com\",\"message_title\": \"Votre document pour signature\",\"message_body\": \"Vous êtes signataire du contrat ci-après\",\"keep_on_move\": false, \"auto_close\": 1}");
    builder.addTextBody("recipients", "{\"data\": [{\"civility\": \"MONSIEUR\",\"firstname\": \"Khalil\",\"lastname\": \"BHS\",\"address_1\": \"Rue de test\",\"address_2\": \"\",\"postal_code\": \"20156\",\"city\": \"\",\"cell_phone\": \"0021651353584\",\"email\": \"k.salah@oodrive.com\",\"signature_mode\": 11}]}");
    File file = ResourceUtils.getFile("classpath:static/contract.pdf");
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
    //File file = ResourceUtils.getFile("/Users/k.salah/Desktop/1/khalilcontract.pdf");
    //InputStream is = new FileInputStream(file);
    //builder.addBinaryBody("pdfparts", is, ContentType.create("application/pdf"), file.getName());
//    private String Test() throws IOException {
//        HttpPost httppost = new HttpPost(base_url+"/contracts/allinone?start=false");
//        httppost.addHeader("Accept", "application/json");
//        httppost.addHeader("j_token", "");
//        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//        builder.addTextBody("contract", "{\"name\": \"encore_un_essai.pdf\",\"contract_definition_id\": 6064,\"vendor_email\": \"test-l-agence-telecom-sandbox-fo@calindasoftware.com\",\"message_title\": \"L'Agence Telecom: Votre document pour signature\",\"message_body\": \"Vous êtes signataire du contrat ci-après.\\nMerci de bien vouloir le signer électroniquement en cliquant sur le lien ci-dessous.\\nCordialement\",\"keep_on_move\": false, \"perimeters\": [\"Pack Téléphonie Intégré\"], \"auto_close\": 1}");
//        builder.addTextBody("recipients", "{\"data\": ["
//                + "{\"civility\": \"MONSIEUR\", \"firstname\": \"Laurent\", \"lastname\": \"DEBRUYNE\", \"email\": \"omathieu+001@calindasoftware.com\",\"cell_phone\":\"0606060606\",\"signature_mode\": 3, \"perimeters\": [\"Pack Téléphonie Intégré\"], \"transport_mode\": 1}"
//                + ", {\"civility\": \"MONSIEUR\", \"firstname\": \"Laurent\", \"lastname\": \"DEBRUYNE\", \"email\": \"omathieu+002@calindasoftware.com\",\"cell_phone\":\"0606060606\",\"signature_mode\": 7, \"perimeters\": [\"Pack Téléphonie Intégré\"], \"transport_mode\": 1}"
//                + "]}");
//        File file = ResourceUtils.getFile("classpath:static/012345678-Thibault World Company-PTI.pdf");
//        //File file = ResourceUtils.getFile("classpath:static/smartpacte2.pdf");
//        InputStream is = new FileInputStream(file);
//        builder.addBinaryBody("pdfparts", is, ContentType.create("application/pdf"), file.getName());
//
////        File file2 = ResourceUtils.getFile("classpath:static/pacte-new.pdf");
////        InputStream is2 = new FileInputStream(file2);
////        builder.addBinaryBody("pdfparts", is2, ContentType.create("application/pdf"), file.getName());
//
//        HttpEntity multipart = builder.build();
//        httppost.setEntity(multipart);
//
//        String result = "";
//        try (CloseableHttpClient httpClient = HttpClients.createDefault();
//        CloseableHttpResponse response = httpClient.execute(httppost);) {
//            HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                result = EntityUtils.toString(entity);
//            }
//            else {
//                result = response.toString();
//            }
//        }
//        return result;
//    }

//    @GetMapping("/create-contract")
//    public String createContract() {
//        Contract contract = new Contract("demo.pdf", cdi, "admin@calindasoftware.com", "Your demo contract", "You can now sign your demo contract", 1);
//        List<Recipient> recipients = new ArrayList<Recipient>();
//        recipients.add(new Recipient("MONSIEUR", "Olivier", "MATHIEU", "47 rue Mazenod", "0662370619", "omathieu+641@calindasoftware.com", 10));
//        recipients.add(new Recipient("MADAME", "Ada", "Lov'lace", "", "0662370619", "omathieu+642@calindasoftware.com", 10));
//
//        Gson gson = new Gson();
//        String j_contract = gson.toJson(contract);
//        String j_recipients = gson.toJson(recipients);
//
//
//
//    }
}
