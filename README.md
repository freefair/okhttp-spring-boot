# Spring Boot Starters for [OkHttp](http://square.github.io/okhttp/) 

Latest version: [![](https://jitpack.io/v/io.freefair/okhttp-spring-boot.svg)](https://jitpack.io/#io.freefair/okhttp-spring-boot)

## Available Starters

- `okhttp2-spring-boot-starter`
- `okhttp3-spring-boot-starter`
- `okhttp-spring-boot-starter` (Alias for `okhttp3-spring-boot-starter`)

## Using Gradle
```gradle
dependencies {
    compile "io.freefair.okhttp-spring-boot:okhttp3-spring-boot-starter:$version"
}
```
```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
## Using Maven
```xml
<dependency>
    <groupId>io.freefair.okhttp-spring-boot</groupId>
    <artifactId>okhttp3-spring-boot-starter</artifactId>
    <version>${version}</version>
</dependency>
```
```xml
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
```
