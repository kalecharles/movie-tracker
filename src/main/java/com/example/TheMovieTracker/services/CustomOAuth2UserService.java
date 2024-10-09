package com.example.TheMovieTracker.services;

import com.example.TheMovieTracker.entities.User;
import com.example.TheMovieTracker.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@Service
//@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    @Autowired
    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    @Override
//    //@Transactional
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        OAuth2User oauth2User = super.loadUser(userRequest);
//
//        try {
//
//            System.out.println("Processing OAuth2 user: " + oauth2User.getAttributes());
//            return processOAuth2User(userRequest, oauth2User);
//        } catch (Exception ex) {
//            // Log the exception
//            ex.printStackTrace();
//            throw new OAuth2AuthenticationException(String.valueOf("Email not found from OAuth2 provider," + ex));
//        }
//    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        if (userRequest instanceof OidcUserRequest) {
            // Handle OIDC user separately
            OidcUserService delegate = new OidcUserService();
            OidcUser oidcUser = delegate.loadUser((OidcUserRequest) userRequest);
            return processOidcUser((OidcUserRequest) userRequest, oidcUser);
        } else {
            // Handle OAuth2 user (non-OIDC)
            OAuth2User oauth2User = super.loadUser(userRequest);
            return processOAuth2User(userRequest, oauth2User);
        }
    }


    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oauth2User.getAttributes();

        // Log the provider and attributes for debugging
        System.out.println("Provider: " + provider);
        System.out.println("Attributes: " + attributes);

        String email = getEmail(attributes, provider);
        String name = getName(attributes, provider);
        String providerId = getProviderId(attributes, provider);

        if (email == null || email.isEmpty()) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
        User user = userOptional.orElseGet(User::new);

        user.setEmail(email);
        user.setUsername(name != null ? name : email);
        user.setOauth2Provider(provider);
        user.setOauth2ProviderId(providerId);

        user = userRepository.save(user);

        Map<String, Object> mutableAttributes = new HashMap<>(attributes);
        mutableAttributes.put("id", user.getId());

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                mutableAttributes,
                "email"
        );
    }

    private String getEmail(Map<String, Object> attributes, String provider) {
        if ("google".equals(provider)) {
            return (String) attributes.get("email");
        }
        // Add cases for other providers if needed
        return null;
    }

    private String getName(Map<String, Object> attributes, String provider) {
        if ("google".equals(provider)) {
            return (String) attributes.get("name");
        }
        // Add cases for other providers if needed
        return null;
    }

    private String getProviderId(Map<String, Object> attributes, String provider) {
        if ("google".equals(provider)) {
            return (String) attributes.get("sub");
        }
        // Add cases for other providers if needed
        return null;
    }
//
    public OAuth2User processOidcUser(OidcUserRequest userRequest, OidcUser oidcUser) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oidcUser.getAttributes();

        // Log the provider and attributes for debugging
        System.out.println("Provider: " + provider);
        System.out.println("Attributes: " + attributes);

        String email = getEmail(attributes, provider);
        String name = getName(attributes, provider);
        String providerId = getProviderId(attributes, provider);

        if (email == null || email.isEmpty()) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
        User user = userOptional.orElseGet(User::new);

        user.setEmail(email);
        user.setUsername(name != null ? name : email);
        user.setOauth2Provider(provider);
        user.setOauth2ProviderId(providerId);

        user = userRepository.save(user);

//        Map<String, Object> mutableAttributes = new HashMap<>(attributes);
//        mutableAttributes.put("id", user.getId());

//        return new DefaultOAuth2User(
//                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
//                mutableAttributes,
//                "email"
//        );
        return oidcUser;
    }
}
//http://localhost:8080/oauth2/authorization/google