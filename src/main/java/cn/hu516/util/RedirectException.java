package cn.hu516.util;

/**
 * @author 秃破天际
 * @version 10.0
 * Created by 秃破天际 on 2021-05-12
 * 简单的借用异常来统一处理重定向
 **/
public class RedirectException extends Exception {

    private String url;

    public RedirectException( String url ){
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
