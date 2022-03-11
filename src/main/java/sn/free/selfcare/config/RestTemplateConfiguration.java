package sn.free.selfcare.config;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;

@Configuration
public class RestTemplateConfiguration {

    @Bean
    @Qualifier("restTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /*@Bean
    @Qualifier("customRestTemplate")
    public RestTemplate customRestTemplate() {

        RestTemplate customRestTemplate = new RestTemplate();
        customRestTemplate.setRequestFactory(new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                if (connection instanceof HttpsURLConnection) {
                    ((HttpsURLConnection) connection).setHostnameVerifier(new NoopHostnameVerifier());
                }
                super.prepareConnection(connection, httpMethod);
            }
        });

        return customRestTemplate;
    }*/
}
