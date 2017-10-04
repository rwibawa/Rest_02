package com.navisow.android.rest_02;

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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                String url = "https://desolate-badlands-25220.herokuapp.com/oauth/token";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

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

                String bearerToken = token.getToken_type() + " " + token.getAccess_token();
                headers = new HttpHeaders();
                headers.set("Authorization", bearerToken);

                formEncodedRequest = new HttpEntity(headers);
                url = "https://desolate-badlands-25220.herokuapp.com/greeting";
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                        .queryParam("name", name); // URLEncoder.encode(name, "UTF-8")

//                Greeting greeting = restTemplate.getForObject(url, Greeting.class, header);
                ResponseEntity<Greeting> response = restTemplate.exchange(
                        builder.build().encode().toUri(),
                        HttpMethod.GET, formEncodedRequest, Greeting.class);

                return response.getBody();

            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Greeting greeting) {
            TextView greetingIdText = (TextView) findViewById(R.id.id_value);
            TextView greetingContentText = (TextView) findViewById(R.id.content_value);
            greetingIdText.setText(String.valueOf(greeting.getId()));
            greetingContentText.setText(greeting.getContent());
        }

    }
}
