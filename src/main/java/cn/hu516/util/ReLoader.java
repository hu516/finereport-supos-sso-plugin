package cn.hu516.util;

/**
 * 数据重加载接口
 * @param <T>
 */
public interface ReLoader<T> {
    T reload(T old)throws Exception;
}
