package io.neow3j.example.springboot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.wallet.Wallet;

/**
 * Neow3jConfig
 */
@Configuration
public class Neow3jConfig {

    @Bean
    public Neow3j neow3j() {
        return Neow3j.build(new HttpService("http://localhost:30333"));
    }

    @Bean
    public Wallet wallet() {
        return Wallet.createGenericWallet();
    }

}