/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CacheServiceImpl
 * Author:   俊哥
 * Date:     2019/12/26 15:18
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;
import pers.jun.service.CacheService;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/12/26
 * @since 1.0.0
 */
@Service
public class CacheServiceImpl implements CacheService {

    private Cache<String,Object> commonCache = null;

    /**
     * springbean会在bean加载的时候先执行此方法
     */
    @PostConstruct
    public void init() {
        commonCache = CacheBuilder.newBuilder()
                // 设置缓存初始容量大小
                .initialCapacity(10)
                // 设置缓存最大容量
                .maximumSize(100)
                // 设置缓存在写入后60s过期
                .expireAfterWrite(60, TimeUnit.SECONDS).build();
    }

    @Override
    public void setCommonCache(String key, Object value) {
        commonCache.put(key,value);
    }

    @Override
    public Object getFromCache(String key) {
        // 如果对应的key不存在会返回null
        return commonCache.getIfPresent(key);
    }
}