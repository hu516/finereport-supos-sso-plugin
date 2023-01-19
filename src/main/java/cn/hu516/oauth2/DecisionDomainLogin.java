package cn.hu516.oauth2;

import cn.hu516.util.SkipException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 秃破天际
 * @version 10.0
 * Created by 秃破天际 on 2021-05-12
 * 拦截平台入口，进行OAuth2单点
 **/
public class DecisionDomainLogin extends OAuth2Login {
    @Override
    public String filterName() {
        return "DecisionDomainLogin";
    }

    @Override
    public String[] urlPatterns() {
        return new String[]{
                "/decision"
        };
    }

    @Override
    protected boolean accept(HttpServletRequest req) throws SkipException {
        return true;
    }
}
