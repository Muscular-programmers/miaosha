/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: WebServerConfiguration
 * Author:   俊哥
 * Date:     2019/12/1 12:14
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

/**
 * 〈定制化tomcat开发〉<br>
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/12/1
 * @since 1.0.0
 */
public class WebServerConfiguration implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        //使用工厂类提供给我们的接口定制化tomcat connector
        ((TomcatServletWebServerFactory)factory).addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                Http11NioProtocol handler = (Http11NioProtocol)connector.getProtocolHandler();

                //定制化keepavlie属性，即客户端多少毫秒没有请求则断开连接
                handler.setKeepAliveTimeout(30000);
                //定制化maxkeepalivereqeust属性，即客户端在发送多少个请求后断开连接
                handler.setMaxKeepAliveRequests(10000);
            }
        });
    }
}