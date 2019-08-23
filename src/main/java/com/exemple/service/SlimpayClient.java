package com.exemple.service;

import com.exemple.model.SlimpayCreateOrderRequest;
import com.exemple.model.SlimpayMandate;
import com.exemple.model.SlimpayOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slimpay.hapiclient.exception.HttpException;
import com.slimpay.hapiclient.hal.CustomRel;
import com.slimpay.hapiclient.hal.Rel;
import com.slimpay.hapiclient.hal.Resource;
import com.slimpay.hapiclient.http.Follow;
import com.slimpay.hapiclient.http.HapiClient;
import com.slimpay.hapiclient.http.Method;
import com.slimpay.hapiclient.http.Request;
import com.slimpay.hapiclient.http.auth.Oauth2BasicAuthentication;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;

import javax.json.JsonObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class SlimpayClient {

    private static final String SLIMPAY_USER_ID = "democreditor01";

    private static final String SLIMPAY_PASSWORD = "demosecret01";

    private static final String RAKUTEN_CREDITOR_REFERENCE = "democreditor";

    private static final String REL_NAMESPACE = "https://api.slimpay.net/alps#";

    private static final Rel CREATE_ORDER_REL = new CustomRel(REL_NAMESPACE + "create-orders");

    private static final Rel SEARCH_SUBSCRIBERS_REL = new CustomRel(REL_NAMESPACE + "search-subscribers");

    private static final Rel GET_MANDATE_REL = new CustomRel(REL_NAMESPACE + "get-mandate");

    private static final Rel GET_BANK_ACCOUNT_REL = new CustomRel(REL_NAMESPACE + "get-bank-account");

    private static final Rel SUBSCRIBERS_REL = new CustomRel("subscribers");

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'k:mm:ss.SSSX");

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
                .setItems(Collections.singletonList(new SlimpayCreateOrderRequest.Item("sign", new SlimpayCreateOrderRequest.Mandate(signatory))));

        return sendCreateOrderRequest(userId, signMandateRequest);
    }

    private SlimpayOrder sendCreateOrderRequest(String userId, SlimpayCreateOrderRequest amendMandateRequest) throws JsonProcessingException, HttpException {
        HttpEntity request = new ByteArrayEntity(OBJECT_MAPPER.writeValueAsBytes(amendMandateRequest), ContentType.APPLICATION_JSON);

        // Create sign mandate order
        Follow createOrderFollow = new Follow.Builder(CREATE_ORDER_REL)
                .setMethod(Method.POST)
                .setMessageBody(request)
                .build();

        Resource createOrderResource = HAPI_CLIENT.send(createOrderFollow);
        if (createOrderResource == null || createOrderResource.getState() == null || !createOrderResource.getState().containsKey("id")) {
            throw new IllegalStateException("Slimpay sign mandate order response is not complete for use " + userId);
        }

        return new SlimpayOrder()
                .setOrderId(createOrderResource.getState().getString("id"))
                .setRedirectUrl(createOrderResource.getLink(new CustomRel(SlimpayClient.REL_NAMESPACE + "user-approval")).getHref());
    }

    public SlimpayOrder createAmendMandateOrder(String userId, String mandateReference) throws Exception {

        SlimpayCreateOrderRequest amendMandateRequest = new SlimpayCreateOrderRequest()
                .setPaymentScheme("SEPA.DIRECT_DEBIT.CORE")
                .setCreditor(new SlimpayCreateOrderRequest.Creditor(RAKUTEN_CREDITOR_REFERENCE))
                .setSubscriber(new SlimpayCreateOrderRequest.Subscriber(userId))
                .setSuccessUrl("http://www.google.fr")
                .setCancelUrl("http://www.bing.fr")
                .setFailureUrl("http://www.yahoo.fr")
                .setItems(Collections.singletonList(new SlimpayCreateOrderRequest.Item("amendBankAccount", new SlimpayCreateOrderRequest.Mandate(mandateReference))));

        return sendCreateOrderRequest(userId, amendMandateRequest);
    }

    public SlimpayMandate getMandate(String userId) throws Exception {
        String mandateUrl = getMandateUrl(userId);
        if (mandateUrl == null) {
            return null;
        }

        // Get mandate infos
        Resource mandateResource = HAPI_CLIENT.send(new Request.Builder(mandateUrl).setMethod(Method.GET).build());
        if (mandateResource == null) {
            throw new IllegalStateException("Slimpay get mandate response is null for user " + userId);
        }

        // Get mandate bank account infos
        Resource bankAccountResource = null;
        try {
            String bankAccountUrl = mandateResource.getLink(GET_BANK_ACCOUNT_REL).getHref();
            bankAccountResource = HAPI_CLIENT.send(new Request.Builder(bankAccountUrl).setMethod(Method.GET).build());
        } catch (RuntimeException e) {
            // Do nothing
        }

        // Get mandate card alias
        //TODO

        // Create mandate response
        LocalDateTime mandateSignedDate = null;
        try {
            mandateSignedDate = LocalDateTime.parse(getJsonElementValue(mandateResource.getState(), "dateSigned"), DATE_TIME_FORMATTER);
        } catch (RuntimeException re) {
            // Do nothing
        }

        SlimpayMandate slimpayMandate = new SlimpayMandate()
                .setReference(getJsonElementValue(mandateResource.getState(), "reference"))
                .setDateSigned(mandateSignedDate)
                .setStatus(SlimpayMandate.Status.fromCode(getJsonElementValue(mandateResource.getState(), "state")));

        if (bankAccountResource != null) {
            slimpayMandate.setBankName(getJsonElementValue(bankAccountResource.getState(), "institutionName"))
                    .setIban(getJsonElementValue(bankAccountResource.getState(), "iban"))
                    .setBic(getJsonElementValue(bankAccountResource.getState(), "bic"));
        }

        return slimpayMandate;
    }

    private String getMandateUrl(String userId) throws Exception {
        Follow searchSubscriberFollow = new Follow.Builder(SEARCH_SUBSCRIBERS_REL)
                .setMethod(Method.GET)
                .setUrlVariable("creditorReference", RAKUTEN_CREDITOR_REFERENCE)
                .setUrlVariable("reference", userId)
                .setUrlVariable("size", 1)
                .build();

        Resource searchSubscriberResource = HAPI_CLIENT.send(searchSubscriberFollow);
        if (searchSubscriberResource == null) {
            throw new IllegalStateException("Slimpay get subscriber response is null for user  " + userId);
        }

        String subscriberMandateUrl = null;
        if (searchSubscriberResource.getAllEmbeddedResources().containsKey(SUBSCRIBERS_REL)
                && !((List) searchSubscriberResource.getAllEmbeddedResources().get(SUBSCRIBERS_REL)).isEmpty()) {
            try {
                Resource subscribeResource = (Resource) ((List) searchSubscriberResource.getAllEmbeddedResources().get(SUBSCRIBERS_REL)).get(0);
                subscriberMandateUrl = subscribeResource.getLink(GET_MANDATE_REL).getHref();
            } catch (RuntimeException e) {
                // Do nothing
            }
        }

        return subscriberMandateUrl;
    }

    public boolean hasAlreadyActiveMandate(String userId) throws Exception {
        SlimpayMandate slimpayMandate = getMandate(userId);

        return slimpayMandate != null && SlimpayMandate.Status.ACTIVE.equals(slimpayMandate.getStatus());
    }

    private static String getJsonElementValue(JsonObject jsonObject, String elementName) {
        return jsonObject != null && jsonObject.containsKey(elementName) ? jsonObject.getString(elementName) : null;
    }

}
