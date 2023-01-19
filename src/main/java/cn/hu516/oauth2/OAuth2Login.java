package cn.hu516.oauth2;

import cn.hu516.util.*;
import com.fr.json.JSONObject;
import com.fr.log.FineLoggerFactory;
import com.fr.stable.StringUtils;
import com.fr.third.jgroups.util.UUID;
import com.fr.third.org.apache.http.HttpHeaders;
import com.fr.third.springframework.http.HttpMethod;
import com.fr.web.utils.WebUtils;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 秃破天际
 * @version 10.0
 * Created by 秃破天际 on 2021-05-12
 **/
public abstract class OAuth2Login extends LoginProvider implements ReLoader<TimeoutObject> {


    /**
     * 只有配置了supOS登录的相关参数，才会生效单点
     *
     * @param req
     * @return
     * @throws Exception
     */
    @Override
    protected boolean isEffective(HttpServletRequest req) throws Exception {
        String domain = SuposConfig.getInstance().getDomain();
        String appid = SuposConfig.getInstance().getappid();
        String secret = SuposConfig.getInstance().getSecret();
        return StringUtils.isNotEmpty(appid) || StringUtils.isNotEmpty(secret) || StringUtils.isNotEmpty(domain);
    }


    /**
     * supOS的token是有有效期的，我们不应该频繁的通过企业ID和密钥这种持久凭证，去交换它，否则容易产生安全隐患
     *
     * @param old
     * @return
     * @throws Exception
     */
    @Override
    public TimeoutObject reload(TimeoutObject old) throws Exception {
        //todo 还没理解
//        FineLoggerFactory.getLogger().info("TimeoutObject:" + old.toString());
//        String domain = SuposConfig.getInstance().getDomain();
//        String token = WebUtils.getHTTPRequestParameter(req, "code");
//        FineLoggerFactory.getLogger().info("token:" + token);
//        AkskService akskService = new AkskService();
//        String baseUrl = SuposConfig.getInstance().getDomain();
//        String apiPath = "/open-api/auth/v2/oauth2/token";
//        String jsonBody = "{\n" +
//                "  \"grantType\": \"authorization_code\",\n" +
//                "  \"code\": \"" + token + "\",\n" +
//                "  \"logoutUri\": \"string\"\n" +
//                "}";
//        Map<String, String> headerParams = new HashMap<>();
//        headerParams.put(HttpHeaders.CONTENT_TYPE, "application/json");
//        String response = akskService.doHttpMethod(baseUrl, apiPath, HttpMethod.POST, new JSONObject(jsonBody), headerParams);
//        JSONObject resJo = new JSONObject(response);
//        if (0 != resJo.optInt("errcode")) {
//            throw new Exception(resJo.optString("errmsg"));
//        }
//
//        if (null == old) {
//            old = new TimeoutObject();
//        }
//        old.setObject(resJo.optString("accessToken"));
//        old.setTimeout(System.currentTimeMillis() + 30 * 1000);
        return old;
    }

    private TimeoutObjectHolder holder = null;

    @Override
    public void init(FilterConfig config) {
        try {
            //access_token的有效加载器，如果我们不怕安全隐患，也不担心性能，可以每次登录都重新获取token
            holder = TimeoutObjectHolder.init(StringUtils.EMPTY, -1, this, OAuth2Login.class);
        } catch (Exception e) {
            FineLoggerFactory.getLogger().error(e, e.getMessage());
        }
    }

    @Override
    protected String getTag(HttpServletRequest req) throws Exception {
        //OAuth2最终登录凭证实际上就是这个code参数
        String code = WebUtils.getHTTPRequestParameter(req, "code");
        if (StringUtils.isEmpty(code)) {
            //如果没有code我们就重定向到supOS去获取凭证
            throw new RedirectException(initGetCodeUrl(req));
        }
        return code;
    }

    @Override
    protected String getUsername(HttpServletRequest req, String tag) throws Exception {
        //拿到凭证后调用supOS的验证code的请求获取用户信息进行登录
        String token = WebUtils.getHTTPRequestParameter(req, "code");
        FineLoggerFactory.getLogger().info("token:" + token);
        AkskService akskService = new AkskService();
        String baseUrl = SuposConfig.getInstance().getDomain();
        String apiPath = "/open-api/auth/v2/oauth2/token";
        String jsonBody = "{\n" +
                "  \"grantType\": \"authorization_code\",\n" +
                "  \"code\": \"" + token + "\",\n" +
                "  \"logoutUri\": \"string\"\n" +
                "}";
        Map<String, String> headerParams = new HashMap<>();
        headerParams.put(HttpHeaders.CONTENT_TYPE, "application/json");
        String response = akskService.doHttpMethod(baseUrl, apiPath, HttpMethod.POST, new JSONObject(jsonBody), headerParams);
        JSONObject resJo = new JSONObject(response);
        if (resJo.isEmpty()) {
            throw new Exception("调用" + apiPath + "失败");
        }
        return resJo.optString("username");
    }

    private String initGetCodeUrl(HttpServletRequest req) throws Exception {
        String crt_url = WebUtils.getOriginalURL(req);
        FineLoggerFactory.getLogger().info("crt_url:" + crt_url);
        crt_url = URLEncoder.encode(crt_url, "UTF-8");
        String uuid = UUID.randomUUID().toString();
        return SuposConfig.getInstance().getDomain() + "/inter-api/auth/v1/oauth2/authorize" +
                "?redirectUri=" + crt_url + "&state=" + uuid + "&responseType=code";
    }

}
