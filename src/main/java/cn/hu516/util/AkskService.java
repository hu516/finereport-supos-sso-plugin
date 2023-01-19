package cn.hu516.util;

import cn.hu516.oauth2.SuposConfig;
import com.fr.json.JSONObject;
import com.fr.third.org.apache.commons.codec.digest.HmacAlgorithms;
import com.fr.third.org.apache.commons.codec.digest.HmacUtils;
import com.fr.third.org.apache.http.HttpEntity;
import com.fr.third.org.apache.http.HttpHeaders;
import com.fr.third.org.apache.http.HttpResponse;
import com.fr.third.org.apache.http.client.methods.*;
import com.fr.third.org.apache.http.entity.StringEntity;
import com.fr.third.org.apache.http.impl.client.CloseableHttpClient;
import com.fr.third.org.apache.http.impl.client.HttpClients;
import com.fr.third.org.apache.http.util.EntityUtils;
import com.fr.third.springframework.http.HttpMethod;
import com.fr.third.springframework.util.CollectionUtils;
import com.fr.third.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;


public class AkskService {
    /**
     * 通过AK/SK签名 发送HTTP请求调用open api接口
     *
     * @param apiPath  api的请求路径 如:/openapi/users/v1?page=1&per_page=2
     * @param method   HttpMethod
     * @param jsonBody 当method是post put等请求时，所携带的body
     * @return 接口返回
     * @throws Exception e
     */
    public String doHttpMethod(String baseUrl, String apiPath, HttpMethod method, JSONObject jsonBody, Map<String, String> headerMap) throws Exception {
        SignRequest request = new SignRequest();
        request.setUrl(baseUrl, apiPath);
        String appid = SuposConfig.getInstance().getappid();
        String secret = SuposConfig.getInstance().getSecret();

        request.setAppKey(appid);
        request.setAppSecret(secret);
        request.setHttpMethod(method);
        if (!CollectionUtils.isEmpty(headerMap)) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if (StringUtils.isEmpty(request.getHeaders().get(HttpHeaders.CONTENT_TYPE))) {
            request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        }

        if (null != jsonBody) {
            request.setBody(jsonBody.toString());
        }
        HttpRequestBase requestBase = createSignatureRequest(request);
        CloseableHttpClient client = HttpClients.custom().build();
        HttpResponse response = client.execute(requestBase);
        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity resEntity = response.getEntity();
        String result = "";
        if (statusCode == 200 && resEntity != null) {
            result = EntityUtils.toString(resEntity, "UTF-8");
        }
        return result;
    }

    /**
     * 获取AK/SK加签后的签名头
     *
     * @param request 加签参数
     * @return headers
     */
    public Map<String, String> getSignatureHeader(SignRequest request) {
        Map<String, String> headers = request.getHeaders();
        //签名源
        StringBuffer sb = new StringBuffer();
        //HttpMethod
        sb.append(request.getHttpMethod()).append("\n");
        //HTTP URI
        sb.append(request.getSignUrl()).append("\n");
        //HTTPContentType
        sb.append(headers.get(HttpHeaders.CONTENT_TYPE)).append("\n");
        //CanonicalQueryString
        if (!StringUtils.isEmpty(request.getQueryString())) {
            sb.append(request.getQueryString());
        }
        sb.append("\n");
        //CustomHeaders 自定义头  直接换行
        sb.append("\n");
        //log.info(">>>>>>>>>>>>> AK/SK 签名源内容：[{}] <<<<<<<<<<<<<<<<", sb);
        HmacUtils hmacSha256 = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, request.getSecrect());
        String signature = hmacSha256.hmacHex(sb.toString());
        String finalSignature = "Sign " + request.getKey() + "-" + signature;
        if (StringUtils.isEmpty(request.getHeaders().get(HttpHeaders.AUTHORIZATION))) {
            headers.put("Authorization", finalSignature);
        } else {
            headers.put("Authorization", request.getHeaders().get(HttpHeaders.AUTHORIZATION));
        }
        return headers;
    }

    /**
     * 创建一个具有AKSK签名的HTTP CLIENT请求
     *
     * @param request 加签参数
     * @return HttpRequestBase
     */
    private HttpRequestBase createSignatureRequest(SignRequest request) {
        HttpRequestBase httpRequest;
        StringEntity entity;
        HttpMethod httpMethod = request.getHttpMethod();
        String content = request.getBody();
        String url = request.getUrl();
        if (httpMethod == HttpMethod.POST) {
            HttpPost postMethod = new HttpPost(url);
            if (!StringUtils.isEmpty(content)) {
                entity = new StringEntity(content, StandardCharsets.UTF_8);
                postMethod.setEntity(entity);
            }
            httpRequest = postMethod;
        } else if (httpMethod == HttpMethod.PUT) {
            HttpPut putMethod = new HttpPut(url);
            httpRequest = putMethod;
            if (!StringUtils.isEmpty(content)) {
                entity = new StringEntity(content, StandardCharsets.UTF_8);
                putMethod.setEntity(entity);
            }
        } else if (httpMethod == HttpMethod.PATCH) {
            HttpPatch patchMethod = new HttpPatch(url);
            httpRequest = patchMethod;
            if (!StringUtils.isEmpty(content)) {
                entity = new StringEntity(content, StandardCharsets.UTF_8);
                patchMethod.setEntity(entity);
            }
        } else if (httpMethod == HttpMethod.GET) {
            httpRequest = new HttpGet(url);
        } else if (httpMethod == HttpMethod.DELETE) {
            httpRequest = new HttpDelete(url);
        } else if (httpMethod == HttpMethod.OPTIONS) {
            httpRequest = new HttpOptions(url);
        } else {
            if (httpMethod != HttpMethod.HEAD) {
                throw new RuntimeException("Unknown HTTP method name: " + httpMethod);
            }
            httpRequest = new HttpHead(url);
        }
        //获取签名头
        Map<String, String> headers = getSignatureHeader(request);
        Iterator<String> iterator = headers.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            httpRequest.addHeader(key, headers.get(key));
        }
        return httpRequest;
    }


}
