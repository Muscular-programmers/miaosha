/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: JodaDataTimeJsonSerializer
 * Author:   俊哥
 * Date:     2019/12/25 22:55
 * Description: DateTime序列化
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;

import java.io.IOException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈DateTime序列化〉
 *
 * @author 俊哥
 * @create 2019/12/25
 * @since 1.0.0
 */
public class JodaDateTimeJsonSerializer extends JsonSerializer<DateTime> {


    @Override
    public void serialize(DateTime dateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(dateTime.toString("yyyy-MM-dd HH:mm:ss"));
    }
}