package cn.hu516;

import com.fr.decision.fun.impl.AbstractGlobalRequestFilterProvider;
import com.fr.decision.webservice.bean.authentication.LoginClientBean;
import com.fr.decision.webservice.login.LogInOutResultInfo;
import com.fr.decision.webservice.utils.DecisionServiceConstants;
import com.fr.decision.webservice.v10.login.LoginService;
import com.fr.decision.webservice.v10.login.TokenResource;
import com.fr.decision.webservice.v10.login.event.LogInOutEvent;
import com.fr.event.EventDispatcher;
import com.fr.intelli.record.Focus;
import com.fr.log.FineLoggerFactory;
import com.fr.record.analyzer.EnableMetrics;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 秃破天际
 * @version 10.0
 * Created by 秃破天际 on 2021-05-12
 **/
@EnableMetrics
public class LoginFilter extends AbstractGlobalRequestFilterProvider {
    @Override
    public String filterName() {
        return "login by username";
    }

    @Override
    public String[] urlPatterns() {
        return new String[]{"/*"};
    }

    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) {
        try{
            checkLogin(req,res);
        }catch(Exception e){
            FineLoggerFactory.getLogger().error(e,"LoginFilter[Login By Username]: {}",e.getMessage());
        }

        try {
            chain.doFilter(req,res);
        }catch (Exception e){
            FineLoggerFactory.getLogger().error(e,e.getMessage());
        }
    }

    protected String getUsername( HttpServletRequest req )throws Exception{
        return WebUtils.getHTTPRequestParameter(req,"username");
    }

    private void checkLogin(HttpServletRequest req, HttpServletResponse res )throws Exception{
        String username = getUsername(req);
        try{
            LoginClientBean client = LoginService.getInstance().loginStatusValid(req, TokenResource.COOKIE);
            String crt_username = client.getUsername();
            if( StringUtils.equals(username, crt_username) ){
                return;
            }
        }catch (Exception e){

        }
        login(req,res,username);
    }

    @Focus(id="cn.hu516.finereport.supos.sso",text = "supOS-OAuth2单点登录插件")
    protected void login( HttpServletRequest req, HttpServletResponse res ,String username )throws Exception{
        String token = LoginService.getInstance().login(req,res,username);
        req.setAttribute( DecisionServiceConstants.FINE_AUTH_TOKEN_NAME, token );
        EventDispatcher.fire(LogInOutEvent.LOGIN, new LogInOutResultInfo(req, res, username, true));
    }
}
