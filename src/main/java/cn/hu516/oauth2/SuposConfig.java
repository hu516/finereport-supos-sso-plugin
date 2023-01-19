package cn.hu516.oauth2;

import com.fr.config.*;
import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.stable.StringUtils;

/**
 * @author hu516
 * @version 10.0
 * Created by hu516 on 2023-01-19
 * supOS单点需要的配置
 **/
@Visualization(category = "supOS Login Config")
public class SuposConfig extends DefaultConfiguration {
    private static volatile SuposConfig instance = null;

    public static SuposConfig getInstance() {
        if (instance == null) {
            instance = ConfigContext.getConfigInstance(SuposConfig.class);
        }
        return instance;
    }

    @Identifier(value = "appid", name = "APP ID", description = "APP ID", status = Status.SHOW)
    private Conf<String> appid = Holders.simple(StringUtils.EMPTY);

    @Identifier(value = "secret", name = "SECRET", description = "SECRET", status = Status.SHOW)
    private Conf<String> secret = Holders.simple(StringUtils.EMPTY);

    @Identifier(value = "domain", name = "DOMAIN", description = "supOS域名", status = Status.SHOW)
    private Conf<String> domain = Holders.simple(StringUtils.EMPTY);

    public String getappid() {
        return appid.get();
    }

    public void setappid( String  appid) {
        this.appid.set(appid);
    }

    public String getSecret() {
        return secret.get();
    }

    public void setSecret( String secret) {
        this.secret.set(secret);
    }

    public String getDomain() {
        return domain.get();
    }

    public void setDomain(Conf<String> domain) {
        this.domain = domain;
    }
}
