package com.navisow.android.rest_02;

/**
 * Created by ryan on 9/5/17.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccessToken {
    private long expires_in;
    private String access_token;
    private String token_type;
    private String refresh_token;
    private String scope;
}
