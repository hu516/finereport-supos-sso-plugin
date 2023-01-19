package cn.hu516.oauth2;

import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;
import cn.hu516.util.SkipException;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 秃破天际
 * @version 10.0
 * Created by 秃破天际 on 2021-05-12
 * 拦截报表请求入口，进行OAuth2单点
 **/
public class ReportDomainLogin extends OAuth2Login {
    @Override
    public String filterName() {
        return "ReportDomainLogin";
    }

    @Override
    public String[] urlPatterns() {
        return new String[]{
                "/decision/view/form",
                "/decision/view/report",
        };
    }

    @Override
    protected boolean accept( HttpServletRequest req ) throws SkipException {
        String viewlet = WebUtils.getHTTPRequestParameter(req,"viewlet");
        return StringUtils.isNotEmpty(viewlet);
    }
}
