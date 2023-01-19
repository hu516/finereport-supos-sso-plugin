package cn.hu516.util;

import com.fr.log.FineLoggerFactory;
import com.fr.store.StateHubManager;
import com.fr.store.StateHubService;

/**
 * @author 秃破天际
 * @version 10.0
 * Created by 秃破天际 on 2021-05-12
 * 一个对于会过期对象的重加载对象的封装
 **/
public class TimeoutObjectHolder {

    private static StateHubService getService(){
        //具体会过期的对象要保存到状态服务器，防止集群情况下冲突
        return StateHubManager.applyForTenantService("SsoTimeoutObjectService");
    }

    private ReLoader loader;
    private String key;

    public TimeoutObjectHolder(ReLoader loader, String key) {
        this.loader = loader;
        this.key = key;
    }

    public static  TimeoutObjectHolder init( Object object, long timeout, ReLoader loader, Class tag )throws Exception{
        if( null == loader ){
            throw new Exception("ReLoader Is Null");
        }
        if( null == tag ){
            throw new Exception("tag Is Null");
        }
        TimeoutObject obj = new TimeoutObject();
        obj.setObject(object);
        obj.setTimeout(timeout);
        getService().put( tag.getName(), obj );
        return new TimeoutObjectHolder( loader, tag.getName() );
    }

    public <T> T getObj(){
        try{
            TimeoutObject obj = getService().get(key);
            if( obj.isTimeout() ){
                TimeoutObject val = (TimeoutObject) loader.reload( obj );
                if( null != val ){
                    obj = val;
                    getService().put( key, obj );
                }else{
                    throw new Exception( "Timeout Object Reload Failed!" );
                }
            }
            return (T) obj.getObject();
        }catch(Exception e){
            FineLoggerFactory.getLogger().error( e, e.getMessage() );
        }
        return null;
    }

}
