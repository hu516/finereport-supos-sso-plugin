package cn.hu516.util;

/**
 * @author 秃破天际
 * @version 10.0
 * Created by 秃破天际 on 2021-05-12
 * 一个用于存放会过期的对象的封装
 **/
public class TimeoutObject {

    private Object object;
    private long timeout;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public boolean isTimeout(){
        return System.currentTimeMillis()>timeout;
    }
}
