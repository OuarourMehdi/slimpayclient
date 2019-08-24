package com.exemple.service;

import com.exemple.exception.SlimpayClientException;
import com.exemple.model.*;
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

import static com.exemple.model.SlimpayCreateOrderRequest.Item.*;
import static com.exemple.model.SlimpayCreateOrderRequest.SEPA_PAYMENT_SCHEMA;
import static com.exemple.model.SlimpayCreateOrderRequest.Signatory.MR;

public class SlimpayRestApiClient {

    private static final String SLIMPAY_USER_ID = "democreditor01";

    private static final String SLIMPAY_PASSWORD = "demosecret01";

    private static final String RAKUTEN_CREDITOR_REFERENCE = "democreditor";

    private static final String REL_NAMESPACE = "https://api.slimpay.net/alps#";

    static final Rel CREATE_ORDER_REL = new CustomRel(REL_NAMESPACE + "create-orders");

    static final Rel ORDER_USER_APPROVAL_REL = new CustomRel(SlimpayRestApiClient.REL_NAMESPACE + "user-approval");

    static final Rel SEARCH_SUBSCRIBERS_REL = new CustomRel(REL_NAMESPACE + "search-subscribers");

    static final Rel GET_MANDATE_REL = new CustomRel(REL_NAMESPACE + "get-mandate");

    static final Rel GET_BANK_ACCOUNT_REL = new CustomRel(REL_NAMESPACE + "get-bank-account");

    static final Rel SUBSCRIBERS_REL = new CustomRel("subscribers");

    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'k:mm:ss.SSSX");

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final HapiClient hapiClient;

    public SlimpayRestApiClient() {
        hapiClient = new HapiClient.Builder()
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

    SlimpayRestApiClient(HapiClient hapiClient) {
        this.hapiClient = hapiClient;
    }

    public SlimpayOrder createSignMandateOrder(String userId) throws SlimpayClientException {
        SlimpayCreateOrderRequest.BillingAddress billingAddress = new SlimpayCreateOrderRequest.BillingAddress()
                .setStreet1("4 rue de la Rose")
                .setStreet2("Numéro fr poste 122")
                .setCity("Paris")
                .setPostalCode("20200")
                .setCountry("FR");

        SlimpayCreateOrderRequest.Signatory signatory = new SlimpayCreateOrderRequest.Signatory()
                .setBillingAddress(billingAddress)
                .setCompanyName("Test company")
                .setHonorificPrefix(MR)
                .setFamilyName("Ouarour")
                .setGivenName("John")
                .setEmail("ouarour.mehdi@gmail.com")
                .setTelephone("+33612345678");

        SlimpayCreateOrderRequest signMandateRequest = createMandateOrderRequest(userId,
                new SlimpayCreateOrderRequest.Item(SIGN_MANDATE_TYPE, SIGN_ACTION, new SlimpayCreateOrderRequest.Mandate(signatory)));

        return sendCreateOrderRequest(userId, signMandateRequest);
    }

    public SlimpayOrder createAmendMandateOrder(String userId, String mandateReference) throws SlimpayClientException {

        SlimpayCreateOrderRequest amendMandateRequest = createMandateOrderRequest(userId,
                new SlimpayCreateOrderRequest.Item(SIGN_MANDATE_TYPE, AMEND_BANK_ACCOUNT_ACTION, new SlimpayCreateOrderRequest.Mandate(mandateReference)));

        return sendCreateOrderRequest(userId, amendMandateRequest);
    }

    private SlimpayCreateOrderRequest createMandateOrderRequest(String userId, SlimpayCreateOrderRequest.Item orderIterm) {
        return new SlimpayCreateOrderRequest()
                .setPaymentScheme(SEPA_PAYMENT_SCHEMA)
                .setCreditor(new SlimpayCreateOrderRequest.Creditor(RAKUTEN_CREDITOR_REFERENCE))
                .setSubscriber(new SlimpayCreateOrderRequest.Subscriber(userId))
                .setSuccessUrl("http://www.google.fr")
                .setCancelUrl("http://www.bing.fr")
                .setFailureUrl("http://www.yahoo.fr")
                .setItems(Collections.singletonList(orderIterm));
    }

    private SlimpayOrder sendCreateOrderRequest(String userId, SlimpayCreateOrderRequest createOrderRequest) throws SlimpayClientException {
        try {
            HttpEntity request = new ByteArrayEntity(OBJECT_MAPPER.writeValueAsBytes(createOrderRequest), ContentType.APPLICATION_JSON);

            // Create sign mandate order
            Follow createOrderFollow = new Follow.Builder(CREATE_ORDER_REL)
                    .setMethod(Method.POST)
                    .setMessageBody(request)
                    .build();
            Resource createOrderResource = hapiClient.send(createOrderFollow);
            if (createOrderResource == null || createOrderResource.getState() == null || ! createOrderResource.getState().containsKey("id")) {
                throw new IllegalStateException("Slimpay create order response is not complete");
            }

            return new SlimpayOrder()
                    .setOrderId(createOrderResource.getState().getString("id"))
                    .setRedirectUrl(createOrderResource.getLink(ORDER_USER_APPROVAL_REL).getHref());

        } catch (Exception e) {
            throw slimpayClientException("Error when creating Slimpay order for user [" + userId + "]", e);
        }
    }

    public SlimpayMandate getMandate(String userId) throws SlimpayClientException {
        try {
            // Handle case when switch to card payment
            //TODO

            String mandateUrl = getMandateUrl(userId);
            if (mandateUrl == null) {
                return null;
            }

            // Get mandate infos
            Resource mandateResource = hapiClient.send(new Request.Builder(mandateUrl).setMethod(Method.GET).build());
            if (mandateResource == null) {
                throw new IllegalStateException("Slimpay get mandate response is null");
            }

            // Get mandate bank account infos
            String bankAccountUrl = mandateResource.getLink(GET_BANK_ACCOUNT_REL).getHref();
            Resource bankAccountResource = hapiClient.send(new Request.Builder(bankAccountUrl).setMethod(Method.GET).build());

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
        } catch (Exception e) {
            throw slimpayClientException("Error when getting mandate for user [" + userId + "]", e);
        }
    }

    private String getMandateUrl(String userId) throws Exception {
        Follow searchSubscriberFollow = new Follow.Builder(SEARCH_SUBSCRIBERS_REL)
                .setMethod(Method.GET)
                .setUrlVariable("creditorReference", RAKUTEN_CREDITOR_REFERENCE)
                .setUrlVariable("reference", userId)
                .setUrlVariable("size", 1)
                .build();

        Resource searchSubscriberResource = hapiClient.send(searchSubscriberFollow);
        if (searchSubscriberResource == null) {
            throw new IllegalStateException("Slimpay get subscriber response is null");
        }

        String subscriberMandateUrl = null;
        try {
            if (searchSubscriberResource.getAllEmbeddedResources().containsKey(SUBSCRIBERS_REL)
                && !((List) searchSubscriberResource.getAllEmbeddedResources().get(SUBSCRIBERS_REL)).isEmpty()) {
                Resource subscribeResource = (Resource) ((List) searchSubscriberResource.getAllEmbeddedResources().get(SUBSCRIBERS_REL)).get(0);
                subscriberMandateUrl = subscribeResource.getLink(GET_MANDATE_REL).getHref();
            }
        } catch (RuntimeException e) {
            // Do nothing
        }

        return subscriberMandateUrl;
    }

    public boolean hasActiveMandate(String userId) {
        SlimpayMandate slimpayMandate = null;
        try {
            slimpayMandate = getMandate(userId);
        } catch (SlimpayClientException sce) {
            // Log error
            System.out.println(sce);
        }

        return slimpayMandate != null && SlimpayMandate.Status.ACTIVE.equals(slimpayMandate.getStatus());
    }

    SlimpayClientException slimpayClientException(String message, Exception e) throws SlimpayClientException {
        SlimpayClientException sce = new SlimpayClientException(message, e);

        if(e instanceof HttpException) {
            HttpException he = (HttpException) e;
            sce.setHttpStatusCode(he.getStatusCode())
                    .setSlimpayErrorCode(parseErrorBodyResponse(he.getResponseBody()));
        }

        throw sce;
    }

    static String getJsonElementValue(JsonObject jsonObject, String elementName) {
        return jsonObject != null && jsonObject.containsKey(elementName) ? jsonObject.getString(elementName) : null;
    }

    SlimpayErrorCode parseErrorBodyResponse(String body) {
        SlimpayErrorCode slimpayErrorCode = null;
        try {
            SlimpayError slimpayError = OBJECT_MAPPER.readValue(body, SlimpayError.class);
            if(slimpayError != null && slimpayError.getCode() != null) {
                slimpayErrorCode = SlimpayErrorCode.fromCode(slimpayError.getCode());
            }
        } catch (Exception e) {
            // Body doesn't contain error
        }

        return slimpayErrorCode != null ? slimpayErrorCode : SlimpayErrorCode.UNKNOWN;
    }

}
