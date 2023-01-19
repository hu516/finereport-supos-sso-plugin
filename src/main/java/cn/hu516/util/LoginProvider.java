package cn.hu516.util;

import com.fr.decision.config.FSConfig;
import com.fr.decision.fun.impl.AbstractGlobalRequestFilterProvider;
import com.fr.decision.webservice.bean.authentication.LoginClientBean;
import com.fr.decision.webservice.login.LogInOutResultInfo;
import com.fr.decision.webservice.utils.DecisionServiceConstants;
import com.fr.decision.webservice.utils.DecisionStatusService;
import com.fr.decision.webservice.v10.login.LoginService;
import com.fr.decision.webservice.v10.login.TokenResource;
import com.fr.decision.webservice.v10.login.event.LogInOutEvent;
import com.fr.event.EventDispatcher;
import com.fr.log.FineLoggerFactory;
import com.fr.stable.StringUtils;
import com.fr.store.Converter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 秃破天际
 * @version 10.0
 * Created by 秃破天际 on 2021-05-12
 **/
public abstract class LoginProvider extends AbstractGlobalRequestFilterProvider {

    /**
     * 登录功能是否正式开启了，因为有的登录可能要先在平台配置某些内容或者做一些初始化的准备之后才会生效，
     * 这里提供接口进行检测
     * @param req
     * @return 返回true表示单点功能正式开始生效
     * @throws Exception
     */
    protected abstract boolean isEffective(HttpServletRequest req)throws Exception;

    /**
     * 除了URI之外的待登录请求的匹配
     * @param req
     * @return 返回true就表示这个请求要进行登录检测
     * @throws SkipException
     */
    protected abstract boolean accept(HttpServletRequest req)throws SkipException;

    /**
     * 从请求中获取登录的凭证
     * @param req
     * @return
     * @throws Exception
     */
    protected abstract String getTag( HttpServletRequest req )throws Exception;

    /**
     * 验证凭证获取要登录的用户名
     * @param req
     * @param tag
     * @return
     * @throws Exception
     */
    protected abstract String getUsername( HttpServletRequest req,String tag )throws Exception;



    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) {
        FineLoggerFactory.getLogger().info("oauth2");
        try{
            if( !isEffective(req) ){
                throw new SkipException();
            }
            if( !accept(req) ){
                throw new SkipException();
            }
            String tag = getTag(req);
            LoginClientBean client = getLoginClientBean(req);
            assertNoSameTag( client, tag );
            String username = getUsername( req, tag );
            login(req,res,client,username,tag);
        }catch (RedirectException e){
            sendRedirect( res, e.getUrl() );
            return;
        }catch (SkipException e){

        }catch(Exception e){
            FineLoggerFactory.getLogger().error(e,"LoginFilter[Login By Username]: {}",e.getMessage());
        }
        try {
            chain.doFilter(req,res);
        }catch (Exception e){
            FineLoggerFactory.getLogger().error(e,e.getMessage());
        }
    }

    /**
     * 最终登录用户的方法，登录后需要把凭证跟登录的用户绑定在一起
     * @param req
     * @param res
     * @param client
     * @param username
     * @param tag
     * @throws Exception
     */
    private void login( HttpServletRequest req, HttpServletResponse res ,LoginClientBean client, String username,String tag )throws Exception{
        //如果当前没有已登陆的用户，或者已登录的用户与将要登录的用户不一致，则重新登录
        if( null == client || !StringUtils.equals( client.getUsername(), username ) ){
            String token = LoginService.getInstance().login(req,res,username);
            req.setAttribute( DecisionServiceConstants.FINE_AUTH_TOKEN_NAME, token );
            EventDispatcher.fire(LogInOutEvent.LOGIN, new LogInOutResultInfo(req, res, username, true));
            client = DecisionStatusService.loginStatusService().get(token);
        }
        //把当前凭证保存到状态服务器中
        client = SpLoginClientBean.copy( client, tag );
        DecisionStatusService.loginStatusService().put(client.getToken(),client,new Converter<LoginClientBean>() {
            @Override
            public String[] createAlias(LoginClientBean client) {
                return new String[]{ client.getUsername() };
            }
        }, FSConfig.getInstance().getLoginConfig().getLoginTimeout());
    }

    /**
     * 判断凭证如果已经在当前有效的登录有效期内绑定了用户，则不再重复登陆
     * @param client
     * @param tag
     * @throws SkipException
     */
    private void assertNoSameTag( LoginClientBean client ,String tag)throws SkipException{
        //如果返回的凭证是空，表示强制认证，不用比较是否是已经绑定登录的凭证
        if( StringUtils.isEmpty(tag) ){
            return;
        }
        if( null == client || !(client instanceof SpLoginClientBean) ){
            return;
        }
        //如果当前的凭证跟状态服务器内绑定当前登录信息的凭证一致，则不用重复登录
        if( ((SpLoginClientBean)client).contain( tag ) ){
            throw new SkipException();
        }
    }

    /**
     * 获取当前已经登陆的有效客户端信息
     * @param req
     * @return
     */
    private LoginClientBean getLoginClientBean(HttpServletRequest req){
        try{
            return LoginService.getInstance().loginStatusValid(req, TokenResource.COOKIE);
        }catch (Exception e){

        }
        return null;
    }

    /**
     * 重定向
     * @param res
     * @param url
     */
    private void sendRedirect( HttpServletResponse res ,String url){
        try {
            res.sendRedirect( url );
        }catch (Exception e){
            FineLoggerFactory.getLogger().error(e,e.getMessage());
        }
    }

}
