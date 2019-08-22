package com.exemple.service;

import com.exemple.model.SlimpayCreateOrderRequest;
import com.exemple.model.SlimpayOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slimpay.hapiclient.hal.CustomRel;
import com.slimpay.hapiclient.hal.Rel;
import com.slimpay.hapiclient.hal.Resource;
import com.slimpay.hapiclient.http.Follow;
import com.slimpay.hapiclient.http.HapiClient;
import com.slimpay.hapiclient.http.Method;
import com.slimpay.hapiclient.http.auth.Oauth2BasicAuthentication;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;

import java.util.Collections;
import java.util.List;

/**
 * 9fbef606107a605d69c0edbcd8029e5d
 */
public class SlimpayClient {

    private static final String SLIMPAY_USER_ID = "democreditor01";

    private static final String SLIMPAY_PASSWORD = "demosecret01";

    private static final String RAKUTEN_CREDITOR_REFERENCE = "democreditor";

    private static final String REL_NAMESPACE = "https://api.slimpay.net/alps#";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final HapiClient HAPI_CLIENT;
    static {
        HAPI_CLIENT = new HapiClient.Builder()
                .setApiUrl("https://api.preprod.slimpay.com")
                .setProfile("https://api.slimpay.net/alps/v1")
                .setAuthenticationMethod(
                        new Oauth2BasicAuthentication.Builder()
                                .setTokenEndPointUrl("/oauth/token")
                                .setUserid(SLIMPAY_USER_ID)
                                .setPassword(SLIMPAY_PASSWORD)
                                .build()
                )
                .build();
    }

    public SlimpayOrder createSignMandateOrder(String userId) throws Exception {
        SlimpayCreateOrderRequest.BillingAddress billingAddress = new SlimpayCreateOrderRequest.BillingAddress()
                .setStreet1("4 rue de la Rose")
                .setStreet2("Numéro fr poste 122")
                .setCity("Paris")
                .setPostalCode("20200")
                .setCountry("FR");

        SlimpayCreateOrderRequest.Signatory signatory = new SlimpayCreateOrderRequest.Signatory()
                .setBillingAddress(billingAddress)
                .setHonorificPrefix("Mr")
                .setFamilyName("Ouarour")
                .setGivenName("John")
                .setEmail("ouarour.mehdi@gmail.com")
                .setTelephone("+33612345678");

        SlimpayCreateOrderRequest signMandateRequest = new SlimpayCreateOrderRequest()
                .setPaymentScheme("SEPA.DIRECT_DEBIT.CORE")
                .setCreditor(new SlimpayCreateOrderRequest.Creditor(RAKUTEN_CREDITOR_REFERENCE))
                .setSubscriber(new SlimpayCreateOrderRequest.Subscriber(userId))
                .setSuccessUrl("http://www.google.fr")
                .setCancelUrl("http://www.bing.fr")
                .setFailureUrl("http://www.yahoo.fr")
                .setItems(Collections.singletonList(new SlimpayCreateOrderRequest.Item("signMandate", new SlimpayCreateOrderRequest.Mandate(signatory))));

        HttpEntity request = new ByteArrayEntity(OBJECT_MAPPER.writeValueAsBytes(signMandateRequest), ContentType.APPLICATION_JSON);

        // Follow create-orders
        Rel rel = new CustomRel(REL_NAMESPACE + "create-orders");
        Follow follow = new Follow.Builder(rel)
                .setMethod(Method.POST)
                .setMessageBody(request)
                .build();

        Resource resource = HAPI_CLIENT.send(follow);
        if(resource == null) {
            throw new IllegalStateException("Slimpay sign mandate response is null for user " + userId);
        }

        return new SlimpayOrder()
                .setOrderId(resource.getState().getString("id"))
                .setRedirectUrl(resource.getLink(new CustomRel(SlimpayClient.REL_NAMESPACE + "user-approval")).getHref());
    }

    public Object getSubscriberMandateUrl(String userId) throws Exception {
        Rel rel = new CustomRel(REL_NAMESPACE + "search-subscribers");
        Follow follow = new Follow.Builder(rel)
                .setMethod(Method.GET)
                .setUrlVariable("creditorReference", RAKUTEN_CREDITOR_REFERENCE)
                .setUrlVariable("reference", userId)
                .build();

        Resource resource = HAPI_CLIENT.send(follow);
        if(resource == null) {
            throw new IllegalStateException("Slimpay get subscriber response is null for user  " + userId);
        }

        Rel subscribersRel = new CustomRel("subscribers");
        if(! resource.getAllEmbeddedResources().containsKey(subscribersRel) ||
                ((List)resource.getAllEmbeddedResources().get(subscribersRel)).isEmpty()) {
            return null;
        }

        Resource subscribeResource = (Resource) ((List)resource.getAllEmbeddedResources().get(subscribersRel)).get(0);

        String subscriberMandateUrl = null;
        try {
            subscriberMandateUrl = subscribeResource.getLink(new CustomRel(REL_NAMESPACE + "get-mandate")).getHref();
        } catch (RuntimeException e) {
            // Do nothing
        }

        return subscriberMandateUrl;
    }
}
