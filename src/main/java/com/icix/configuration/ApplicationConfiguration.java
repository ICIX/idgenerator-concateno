package com.icix.configuration;

import com.icix.model.IdGeneratorImpl;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.IOException;

@Configuration
public class ApplicationConfiguration {

    private Logger logger = Logger.getLogger(ApplicationConfiguration.class);

    @Bean
    @Scope("singleton")
    public IdGeneratorImpl idGenerator(MemcachedClient memcachedClient){
        return new IdGeneratorImpl(memcachedClient);
    }

    @Bean
    public MemcachedClient memcachedClient() {

        try {
            AuthDescriptor ad = new AuthDescriptor(new String[]{"PLAIN"},
                    new PlainCallbackHandler(System.getenv("MEMCACHEDCLOUD_USERNAME"), System.getenv("MEMCACHEDCLOUD_PASSWORD")));

            MemcachedClient mc =  new MemcachedClient(
                    new ConnectionFactoryBuilder()
                            .setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
                            .setAuthDescriptor(ad).build(),
                    AddrUtil.getAddresses(System.getenv("MEMCACHEDCLOUD_SERVERS")));

            logger.info("Memcached Client initialized successfully.");

            return mc;
        } catch (IOException ex) {
            logger.error("Memcached Client could not be initialized.", ex);
        }

        return null;
    }
}
