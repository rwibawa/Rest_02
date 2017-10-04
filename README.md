# Rest_02

This is an Android app to consume a REST API.

## 1. Back-end REST API
Get the [back-end](https://github.com/rwibawa/spring-boot-07).
Run it, and make sure it's accessible from the **android device's browser**!

## 2. Add the dependencies in the app's '_build.gradle_'.
```groovy
apply plugin: 'com.android.application'

android {
    packagingOptions {
        exclude 'META-INF/ASL2.0'
        exclude 'META-INF/LICENSE'
        exclude 'META-INF/license.txt'
        exclude 'META-INF/NOTICE'
        exclude 'META-INF/notice.txt'
    }
}

dependencies {
    compile 'org.springframework.android:spring-android-rest-template:1.0.1.RELEASE'
    compile 'com.fasterxml.jackson.core:jackson-databind:2.3.2'
}
```

## 3. Add the user permission to access the internet in '_AndroidManifest.xml'.
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.navisow.android.rest_02">

    <uses-permission android:name="android.permission.INTERNET" />

</manifest>
```

## 4. Create an AsyncTask in '_MainActivity.java_'
```java
    private class HttpRequestTask extends AsyncTask<Void, Void, Greeting> {
        private final String name;

        public HttpRequestTask(String name) {
            this.name = name;
        }

        @Override
        protected Greeting doInBackground(Void... params) {
            try {
                final String url = "http://192.168.1.107:8080/greeting?name=" + name;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Greeting greeting = restTemplate.getForObject(url, Greeting.class);
                return greeting;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Greeting greeting) {
            TextView greetingIdText = (TextView) findViewById(R.id.id_value);
            TextView greetingContentText = (TextView) findViewById(R.id.content_value);
            greetingIdText.setText(greeting.getId());
            greetingContentText.setText(greeting.getContent());
        }

    }
```

## 5. Add a Button, and create a handler for event _onClick_.
```java
    // Button Greeting - onClick handler
    public void getGreeting(View button) {
        EditText nameEditText = (EditText) findViewById(R.id.editText_name);
        String name = nameEditText.getText().toString();

        new HttpRequestTask(name).execute();
    }
```

## 6. Clean up before build
```bash
$ rm -rf .gradle ./build ./app/build
```

## 7. Oauth2
Refer to [spring-boot-07](https://github.com/rwibawa/spring-boot-07) as the back-end services.

The `lombok.config` is the configuration file for _lombok_ plugin.

## 8. Gradle
[Gradle guide](https://github.com/codepath/android_guides/wiki/Getting-Started-with-Gradle#upgrading-gradle)

### Check dependencies
```bash
$ ./gradlew androidDependencies
```

### Check info
```bash
$ ./gradlew -v

------------------------------------------------------------
Gradle 3.3
------------------------------------------------------------

Build time:   2017-01-03 15:31:04 UTC
Revision:     075893a3d0798c0c1f322899b41ceca82e4e134b

Groovy:       2.4.7
Ant:          Apache Ant(TM) version 1.9.6 compiled on June 29 2015
JVM:          1.8.0_101 (Oracle Corporation 25.101-b13)
OS:           Mac OS X 10.12.6 x86_64
```

### Build APK
```bash
$ ./gradlew assemble

$ ./gradlew clean
$ ./gradlew build
```

### Speeding up Gradle Builds, set it in `gradle.properties`:
```
#Enable daemon
org.gradle.daemon=true

# Try and findout the best heap size for your project build.
org.gradle.jvmargs=-Xmx2048m -XX:MaxPermSize=512m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8

# Modularise your project and enable parallel build
org.gradle.parallel=true

# Enable configure on demand.
org.gradle.configureondemand=true
```