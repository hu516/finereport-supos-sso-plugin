package cn.hu516.util;


import com.fr.third.org.apache.commons.lang3.StringUtils;
import com.fr.third.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class SignRequest {
    /**
     * ak
     */
    private String key;
    /**
     * sk
     */
    private String secret;
    private HttpMethod httpMethod;
    /**
     * apiBaseURL + apiPath
     */
    private String url;
    /**
     * 加签的URL
     */
    private String signUrl;
    /**
     * body体
     */
    private String body;
    /**
     * 自定义header
     */
    private Map<String, String> headers = new HashMap<>();
    /**
     * 查询参数
     */
    private String queryString;

    public SignRequest() {
    }

    public String getUrl() {
        return this.url;
    }

    /**
     * 设置请求地址url, 签名url,查询字符串
     * @param apiBaseURL api的域名或者ip + port
     * @param apiPath api的请求路径 如:/openapi/users/v1?page=1&per_page=2
     * 示例返回：
     * 请求url：http://kong:8000/openapi/instance/v1/findByName?name=default
     * 加签用SignUrl：/api/openapi/instance/v1/findByName
     * QueryString:name=default
     */
    public void setUrl(String apiBaseURL,String apiPath){
        this.url = apiBaseURL + apiPath;
        if (StringUtils.contains(apiPath,"?")){
            String[] str = apiPath.split("\\?");
            setSignUrl(str[0]);
            setQueryString(str[1]);
        } else {
            setSignUrl(apiPath);
        }
    }

    public String getKey() {
        return this.key;
    }

    public String getSecrect() {
        return this.secret;
    }


    public String getBody() {
        return this.body;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }


    public void setAppKey(String appKey) throws Exception {
        if (null != appKey && !appKey.trim().isEmpty()) {
            this.key = appKey;
        } else {
            throw new Exception("appKey can not be empty");
        }
    }

    public void setAppSecret(String appSecret) throws Exception {
        if (null != appSecret && !appSecret.trim().isEmpty()) {
            this.secret = appSecret;
        } else {
            throw new Exception("appSecrect can not be empty");
        }
    }

    public void setBody(String body) {
        this.body = body;
    }



    public void addHeader(String name, String value) {
        if (null != name && !name.trim().isEmpty()) {
            this.headers.put(name, value);
        }
    }


    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getSignUrl() {
        return signUrl;
    }

    public void setSignUrl(String signUrl) {
        this.signUrl = signUrl;
    }
}
