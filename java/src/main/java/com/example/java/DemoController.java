package com.example.java;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.AutoRetryHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


@RestController
public class DemoController {
    //String base_url = "https://luhman.calindasoftware.com/api/v4";
    String base_url = "https://api.sellandsign.com/api/v4";
    String token = "5699057|XW+reJJEMbhs5+hminwwLAGM4PDaFaI5rmKdjhhvYnU=";
    long cdi = 1;
    long actor_id = 11;
    private HttpClient httpclient = HttpClients.createDefault();

    public HttpResponse callPost(String uri, HashMap<String, String> headers, HashMap payload) throws IOException {
        HttpPost request = new HttpPost(uri);
        for (var h : headers.entrySet()) {
            request.setHeader(h.getKey(), h.getValue());
        }

        var gson = new com.google.gson.Gson();
        var content = gson.toJson(payload);
        StringEntity entity = new StringEntity(content);
        request.setEntity(entity);

        HttpResponse response = httpclient.execute(request);

        return  response;
    }

    @GetMapping("/udd")
    public String testUdd() {
        String uri = "https://integration-administratif.iperia.eu/authenticate";
        String username = "team.sellsign";
        String password= "pKuj@t8a";

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("password", password);
        String token = "";

        try {
            HashMap<String, String> req_headers = new HashMap<>();
            req_headers.put("Content-type", "application/json");
            HttpResponse response = callPost(uri, req_headers, payload);
            List<Header> headers = Arrays.stream(response.getHeaders("Set-Cookie"))
                    .filter(cookie -> cookie.toString().startsWith("Set-Cookie: AUTHCOOKIE"))
                    .collect(Collectors.toList());
            String cookie = headers.get(headers.size() -1).toString();
            token = cookie.substring(cookie.indexOf('=') + 1, cookie.indexOf(';'));

            uri = "https://integration-administratif.iperia.eu/api/modulepath/2c9255947ff3d21e01808897d0b97b70/service-contract/documents/callback";
            req_headers.put("Set-Cookie", "AUTHCOOKIE=" + token + ";Path=/; HttpOnly");
            payload = new HashMap<>();
            payload.put("type", "CONTRACT");
            payload.put("contractId", 123456);
            payload.put("date", 789111001);
            payload.put("status", "SENT");
            response = callPost(uri, req_headers, payload);
            int i = 0;
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
        return token;
    }

    @GetMapping("/status")
    public String getStatus(@RequestParam(value = "id") String c_id) throws IOException {
        HttpGet request = new HttpGet(base_url + "/contracts/" + c_id);

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

    @GetMapping("/test")
    public String test() throws IOException {
        HttpPost httppost = new HttpPost(base_url + "/contracts/allinone?start=true");
        httppost.addHeader("Accept", "application/json");
        httppost.addHeader("j_token", token);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        //builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addTextBody("contract", "{\"name\": \"my_first_contract.pdf\",\"contract_definition_id\": 1,\"vendor_email\": \"test-l-agence-telecom-sandbox-fo@calindasoftware.com\",\"message_title\": \"Votre document pour signature\",\"message_body\": \"Vous êtes signataire du contrat ci-après.\\nMerci de bien vouloir le signer électroniquement en cliquant sur le lien ci-dessous.\\nCordialement\",\"keep_on_move\": false, \"perimeters\": [\"pilote\"], \"auto_close\": 1}");
        builder.addTextBody("recipients", "{\"data\": [{\"civility\": \"MONSIEUR\",\"firstname\": \"Laurent\",\"lastname\": \"DEBRUYNE\",\"address_1\": \"16 rue Edouard MANET\",\"address_2\": \"\",\"postal_code\": \"38130\",\"city\": \"Echirolles\",\"cell_phone\": \"0662370619\",\"email\": \"omathieu+756@calindasoftware.com\",\"signature_mode\": 11}]}");

        //Resource resource = new ClassPathResource("classpath:static/smartpacte2.pdf");
        File file = ResourceUtils.getFile("classpath:static/smartpacte2.pdf");
        InputStream is = new FileInputStream(file);
        builder.addBinaryBody("pdfparts", is, ContentType.create("application/pdf"), file.getName());

//        File file2 = ResourceUtils.getFile("classpath:static/pacte-new.pdf");
//        InputStream is2 = new FileInputStream(file2);
//        builder.addBinaryBody("pdfparts", is2, ContentType.create("application/pdf"), file.getName());

        HttpEntity multipart = builder.build();
        httppost.setEntity(multipart);

        String result = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httppost);) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
            else {
                result = response.toString();
            }
        }
        return result;
    }

//    @GetMapping("/test")
//    private String Test() throws IOException {
//        HttpPost httppost = new HttpPost(base_url+"/contracts/allinone?start=false");
//        httppost.addHeader("Accept", "application/json");
//        httppost.addHeader("j_token", "5699057|vzy6QI3hTySBIKMtfFIOyCFdUTr5t1XY0Dt+by5pqW4=");
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
