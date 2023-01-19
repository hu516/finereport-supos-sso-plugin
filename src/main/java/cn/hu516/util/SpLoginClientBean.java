package cn.hu516.util;

import com.fr.decision.webservice.bean.authentication.LoginClientBean;

import java.util.HashSet;
import java.util.Set;

/**
 * @author 秃破天际
 * @version 10.0
 * Created by 秃破天际 on 2021-05-12
 * 登录客户端信息的扩展，用于绑定额外的凭证
 **/
public class SpLoginClientBean extends LoginClientBean {
    private Set<String> tag = new HashSet<String>();

    public boolean contain( String tag ){
        return -1 != tag.indexOf(tag);
    }

    public void setTag(String tag) {
        this.tag.add(tag);
    }

    public static SpLoginClientBean copy( LoginClientBean bean, String tag ){
        SpLoginClientBean rt = new SpLoginClientBean();
        BaseUtils.copy(bean,rt);
        rt.setTag(tag);
        return rt;
    }

    @Override
    public SpLoginClientBean clone()throws CloneNotSupportedException{
        SpLoginClientBean rt = (SpLoginClientBean)super.clone();
        for( String item : tag ){
            rt.setTag(item);
        }
        return rt;
    }
}
