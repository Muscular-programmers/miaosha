/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: RedisConfig
 * Author:   俊哥
 * Date:     2019/12/25 22:26
 * Description: redis配置类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import pers.jun.serialize.JodaDateTimeJsonDeSerializer;
import pers.jun.serialize.JodaDateTimeJsonSerializer;

/**
 * 〈依靠这个类重新改造redisTemplate，源码为RedisAutoConfiguration.class〉<br>
 * 〈redis配置类〉
 *
 * @author 俊哥
 * @create 2019/12/25
 * @since 1.0.0
 */
@Component
public class RedisConfig {

    /**
     * 覆盖RedisAutoConfiguration中的redisTemplate
     */
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 解决key的默认序列化方式
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);

        // 解决value的序列化方式
        // objectMapper，SimpleModule
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(DateTime.class, new JodaDateTimeJsonSerializer());
        simpleModule.addDeserializer(DateTime.class, new JodaDateTimeJsonDeSerializer());

        // 添加此配置，可以将redis中纯json反序列化为对应的类，否则报错（java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to pers.jun.service.model.ItemModel）
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        objectMapper.registerModule(simpleModule);

        Jackson2JsonRedisSerializer<Object> jsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        jsonRedisSerializer.setObjectMapper(objectMapper);
        redisTemplate.setValueSerializer(jsonRedisSerializer);

        return redisTemplate;
    }

}