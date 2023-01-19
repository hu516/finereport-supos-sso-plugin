package cn.hu516.util;

import com.fr.invoke.Reflect;

import java.util.Map;
import java.util.Set;

/**
 * @author 秃破天际
 * @version 10.0
 * Created by 秃破天际 on 2021-05-12
 **/
public class BaseUtils {
    public static <T> void copy( T from, T to ){
        Set<Map.Entry<String, Reflect>> entries = Reflect.on(from).fields().entrySet();
        for( Map.Entry<String, Reflect> entry : entries ){
            Reflect.on(to).set( entry.getKey(), entry.getValue().get() );
        }
    }
}
