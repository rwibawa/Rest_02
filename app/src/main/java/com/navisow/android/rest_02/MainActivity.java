package com.navisow.android.rest_02;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
    }

    // Button Greeting - onClick handler
    public void getGreeting(View button) {
        EditText nameEditText = (EditText) findViewById(R.id.editText_name);
        String name = nameEditText.getText().toString();

        new HttpRequestTask(name).execute();
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, Greeting> {
        private final String name;

        public HttpRequestTask(String name) {
            this.name = name;
        }

        @Override
        protected Greeting doInBackground(Void... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

                AccessToken token = getAccessToken(restTemplate);
                Greeting greeting = getGreeting(restTemplate, token);

                return greeting;

            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        private Greeting getGreeting(RestTemplate restTemplate, AccessToken token) {
            String bearerToken = token.getToken_type() + " " + token.getAccess_token();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", bearerToken);

            HttpEntity formEncodedRequest = new HttpEntity(headers);
            String url = "https://desolate-badlands-25220.herokuapp.com/greeting";
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("name", name); // URLEncoder.encode(name, "UTF-8")

//                Greeting greeting = restTemplate.getForObject(url, Greeting.class, header);
            ResponseEntity<Greeting> response = restTemplate.exchange(
                    builder.build().encode().toUri(),
                    HttpMethod.GET, formEncodedRequest, Greeting.class);

            return response.getBody();
        }

        private AccessToken getAccessToken(RestTemplate restTemplate) {
            String url = "https://desolate-badlands-25220.herokuapp.com/oauth/token";

            Context context = getBaseContext();
            String tokenName = getString(R.string.access_token);
            SharedPreferences sharedPreferences =
                    context.getSharedPreferences(tokenName, context.MODE_PRIVATE);

            boolean isTokenAvailable = sharedPreferences.getBoolean(tokenName, false);
            if (isTokenAvailable) {
                long expires_in = sharedPreferences.getLong("expires_in", 0L);
                String access_token = sharedPreferences.getString("access_token", null);
                String token_type = sharedPreferences.getString("token_type", null);
                String refresh_token = sharedPreferences.getString("refresh_token", null);
                String scope = sharedPreferences.getString("scope", null);
                AccessToken token =
                        new AccessToken(expires_in,access_token,token_type,refresh_token,scope);

                return token;
            }

            HttpHeaders headers = new HttpHeaders();
//                String basicToken = new String(Base64.encodedBase64(("acme:acmesecret").getBytes()));
            headers.set("Authorization", "Basic YWNtZTphY21lc2VjcmV0");
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("grant_type", "password");
            form.add("username", "rwibawa");
            form.add("password", "Ch@ng3M3!");

            HttpEntity formEncodedRequest = new HttpEntity(form, headers);

            // get token
            AccessToken token = restTemplate.postForObject(url, formEncodedRequest,
                    AccessToken.class, new Object[0]);

            // save the access token in the sharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("expires_in", token.getExpires_in());
            editor.putString("access_token", token.getAccess_token());
            editor.putString("token_type", token.getToken_type());
            editor.putString("refresh_token", token.getRefresh_token());
            editor.putString("scope", token.getScope());
            editor.putBoolean(tokenName, true);
            editor.commit();

            return token;
        }

        @Override
        protected void onPostExecute(Greeting greeting) {
            TextView greetingIdText = (TextView) findViewById(R.id.id_value);
            TextView greetingContentText = (TextView) findViewById(R.id.content_value);
            greetingIdText.setText(String.valueOf(greeting.getId()));
            greetingContentText.setText(greeting.getContent());

            try {
                Context context = getBaseContext();
                File file = File.createTempFile("greetings.txt", null, context.getCacheDir());
                FileOutputStream stream = new FileOutputStream(file, true);
                stream.write(greeting.getContent().getBytes());
                stream.close();
            } catch (IOException e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
        }

    }
}
