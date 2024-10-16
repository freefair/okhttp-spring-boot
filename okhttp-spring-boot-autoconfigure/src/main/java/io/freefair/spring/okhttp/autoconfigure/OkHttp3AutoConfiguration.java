package io.freefair.spring.okhttp.autoconfigure;

import io.freefair.spring.okhttp.ApplicationInterceptor;
import io.freefair.spring.okhttp.NetworkInterceptor;
import io.freefair.spring.okhttp.OkHttp3Configurer;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.FileSystemUtils;

import javax.net.ssl.HostnameVerifier;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author Lars Grefer
 */
@Slf4j
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@AutoConfiguration
@ConditionalOnClass(OkHttpClient.class)
@EnableConfigurationProperties(OkHttpProperties.class)
public class OkHttp3AutoConfiguration {

    @Autowired
    private OkHttpProperties okHttpProperties;

    @Autowired
    private ObjectProvider<OkHttp3Configurer> configurers;

    @Autowired
    @ApplicationInterceptor
    private ObjectProvider<Interceptor> applicationInterceptors;

    @Autowired
    @NetworkInterceptor
    private ObjectProvider<Interceptor> networkInterceptors;

    private File tempDirCache = null;

    @Bean
    @ConditionalOnMissingBean
    public OkHttpClient okHttp3Client(
            ObjectProvider<Cache> cache,
            ObjectProvider<CookieJar> cookieJar,
            ObjectProvider<Dns> dns,
            ObjectProvider<HostnameVerifier> hostnameVerifier,
            ObjectProvider<CertificatePinner> certificatePinner,
            ConnectionPool connectionPool,
            ObjectProvider<EventListener> eventListener
    ) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        cache.ifUnique(builder::cache);

        eventListener.ifUnique(builder::eventListener);

        builder.connectTimeout(okHttpProperties.getConnectTimeout());
        builder.readTimeout(okHttpProperties.getReadTimeout());
        builder.writeTimeout(okHttpProperties.getWriteTimeout());
        builder.pingInterval(okHttpProperties.getPingInterval());

        cookieJar.ifUnique(builder::cookieJar);

        dns.ifUnique(builder::dns);

        hostnameVerifier.ifUnique(builder::hostnameVerifier);
        certificatePinner.ifUnique(builder::certificatePinner);

        builder.connectionPool(connectionPool);

        builder.followRedirects(okHttpProperties.isFollowRedirects());
        builder.followSslRedirects(okHttpProperties.isFollowSslRedirects());
        builder.retryOnConnectionFailure(okHttpProperties.isRetryOnConnectionFailure());

        applicationInterceptors.forEach(builder::addInterceptor);

        networkInterceptors.forEach(builder::addNetworkInterceptor);

        configurers.forEach(configurer -> configurer.configure(builder));

        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConnectionPool okHttp3ConnectionPool() {
        int maxIdleConnections = okHttpProperties.getConnectionPool().getMaxIdleConnections();
        Duration keepAliveDuration = okHttpProperties.getConnectionPool().getKeepAliveDuration();
        return new ConnectionPool(maxIdleConnections, keepAliveDuration.toNanos(), TimeUnit.NANOSECONDS);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "okhttp.cache.enabled", havingValue = "true", matchIfMissing = true)
    public Cache okHttp3Cache() throws IOException {
        File directory = okHttpProperties.getCache().getDirectory();
        if (directory == null) {
            tempDirCache = Files.createTempDirectory("okhttp-cache").toFile();
            directory = tempDirCache;
        }
        return new Cache(directory, okHttpProperties.getCache().getMaxSize().toBytes());
    }

    @PreDestroy
    public void deleteTempCache() {
        if (tempDirCache != null) {
            log.debug("Deleting the temporary OkHttp Cache at {}", tempDirCache.getAbsolutePath());
            try {
                FileSystemUtils.deleteRecursively(tempDirCache);
            } catch (Exception e) {
                log.warn("Failed to delete the temporary OkHttp Cache at {}", tempDirCache.getAbsolutePath());
            }
        }
    }
}
