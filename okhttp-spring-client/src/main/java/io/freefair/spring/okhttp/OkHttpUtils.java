package io.freefair.spring.okhttp;

import kotlin.Pair;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;

import java.util.Collection;
import java.util.List;

/**
 @see okhttp3.Headers.Builder
 @see org.springframework.http.HttpHeaders
 */
@UtilityClass
@NullMarked
public class OkHttpUtils {

    public static HttpHeaders toSpringHeaders (@NonNull Headers okhttpHeaders) {
        var springHeaders = new HttpHeaders();

        for (Pair<? extends String, ? extends String> okhttpHeader : okhttpHeaders) {
            springHeaders.add(okhttpHeader.getFirst(), okhttpHeader.getSecond());
        }

        return springHeaders;
    }

    public static Headers toOkHttpHeaders (HttpHeaders springHeaders) {
        var builder = new Headers.Builder();

        springHeaders.forEach((name, values) ->
                values.forEach(value -> builder.add(name, value))
            );

        return builder.build();
    }

    public static boolean nonEmpty (@Nullable CharSequence str) {
        return str != null && str.length() > 0;
    }

    public static boolean nonEmpty (@Nullable Collection<? extends @Nullable Object> items) {
        return items != null && !items.isEmpty();
    }

    public static boolean found (int indexOf) {
        return indexOf >= 0;
    }

    public static int len (@Nullable CharSequence str) {
        return str != null ? str.length() : 0;
    }

    public static int len (@Nullable Collection<? extends @Nullable Object> items) {
        return items != null ? items.size() : 0;
    }

    /**
     Get {@link HttpUrl.Builder} for the pre-built url bypassing {@link HttpUrl#newBuilder()} or {@link HttpUrl#newBuilder(String)} i.e:
        without creating {@link HttpUrl} beforehand.

     That is, there is an url-template, and it is necessary to slightly modify it, e.g.: substitute variables.
     */
    public static HttpUrl.Builder urlBuilder (String startingUrl) {
        return new HttpUrl.Builder().parse$okhttp(null/*@Nullable HttpUrl base*/, startingUrl);
    }

    /**
     Copy of {@link HttpUrl.Builder#toString()}, but without the "extra" <code>/</code> at the end.
     @param addTrailingSlash false without final <code>/</code>; true always with final <code>/</code> including /with/path/
     @see HttpUrl#toString()
     */
    public static String toString (HttpUrl.Builder b, boolean addTrailingSlash) {
        val sb = new StringBuilder(127);
        String scheme = b.getScheme$okhttp();
        if (scheme != null){
            sb.append(scheme).append("://");
        } else {
            sb.append("//");
        }

        String encodedPassword = b.getEncodedPassword$okhttp();
        if (nonEmpty(b.getEncodedUsername$okhttp()) || nonEmpty(encodedPassword)){
            sb.append(b.getEncodedUsername$okhttp());
            if (nonEmpty(encodedPassword)){
                sb.append(':').append(encodedPassword);
            }
            sb.append('@');
        }

        String host = b.getHost$okhttp();
        if (host != null){
            if (found(host.indexOf(':'))){// Host is an IPv6 address.
                sb.append('[').append(host).append(']');
            } else {
                sb.append(host);
            }
        }

        if (b.getPort$okhttp() >= 0 || scheme != null){
            val effectivePort = effectivePort(b.getPort$okhttp(), scheme);
            if (effectivePort != defaultPort(scheme)){
                sb.append(':').append(effectivePort);
            }
        }

        // encodedPathSegments.toPathString(this)
        List<String> encodedPathSegments = b.getEncodedPathSegments$okhttp();
        for (var ps : encodedPathSegments){
            if (nonEmpty(ps)){// избавимся и от финальной / и от // (пустой pathSegment: имеет право на жизнь, но Spring чистит 🤷‍♀️)
                sb.append('/').append(ps);
            }
        }
        if (addTrailingSlash){
            sb.append('/');
        }

        // encodedQueryNamesAndValues!!.toQueryString(this)
        List<String> query = b.getEncodedQueryNamesAndValues$okhttp();
        if (nonEmpty(query)){
            sb.append('?');
            for (var it = query.iterator(); it.hasNext();){
                sb.append(it.next());// key
                String value = it.hasNext() ? it.next() : null;
                if (value != null){
                    sb.append('=').append(value);
                }
                if (it.hasNext()){
                    sb.append('&');// за нами есть ещё кто-то
                }
            }
        }

        if (nonEmpty(b.getEncodedFragment$okhttp())){
            sb.append('#').append(b.getEncodedFragment$okhttp());
        }
        return sb.toString();
    }
    private static int effectivePort (int port, @Nullable String scheme) {
        return port >= 0 || scheme == null ? port
                : defaultPort(scheme);
    }
    private static int defaultPort (String scheme) {
        return switch(scheme){
            case "http","HTTP" -> 80;
            case "https","HTTPS" -> 443;
            default -> -1;
        };
    }

}
