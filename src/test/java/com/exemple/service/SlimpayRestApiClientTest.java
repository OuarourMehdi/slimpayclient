package com.exemple.service;

import com.exemple.exception.SlimpayClientException;
import com.exemple.model.SlimpayErrorCode;
import com.exemple.model.SlimpayMandate;
import com.exemple.model.SlimpayOrder;
import com.slimpay.hapiclient.exception.HttpException;
import com.slimpay.hapiclient.hal.Link;
import com.slimpay.hapiclient.hal.Rel;
import com.slimpay.hapiclient.hal.Resource;
import com.slimpay.hapiclient.http.Follow;
import com.slimpay.hapiclient.http.HapiClient;
import com.slimpay.hapiclient.http.Request;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.HttpParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.exemple.service.SlimpayRestApiClient.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SlimpayRestApiClientTest {

    private static final String USER_ID = "123456789";
    private static final String MANDATE_REFERENCE = "SLMP040064860";
    private static final String MANDATE_DATE_SIGNED = "2019-08-23T11:40:13.000+0000";
    private static final String ORDER_ID = "16e54437-c6a8-11e9-a3e7-000000000000";
    private static final String APPROVAL_LINK = "https://checkout.preprod.slimpay.com/userApproval?accessCode=sphpoIFcFXbgts";
    private static final String GET_MANDATE_LINK = "https://api.preprod.slimpay.com/mandates/c5195d81-c59a-11e9-b855-000000000000";
    private static final String GET_BANK_ACCOUNT_LINK = "https://api.preprod.slimpay.com/mandates/c5195d81-c59a-11e9-b855-000000000000/bank-account";
    private static final String BIC = "TESTFRP1XXX";
    private static final String IBAN = "FR7600000000009031747722802";
    private static final String BANK_NAME = "SLIMPAY TEST IBAN PLUS";

    @Mock
    private HapiClient hapiClient;

    private SlimpayRestApiClient slimpayRestApiClient;

    @Before
    public void setUp() {
        slimpayRestApiClient = new SlimpayRestApiClient(hapiClient);
    }

    @Test
    public void givenSlimpayApiReturnSuccessWhenCallCreateSignMandateOrderThenReturnSlimpayOrderWithIdAndRedirectUrl() throws Exception {
        // Given
        Resource createSignMandateOrderResource = createOrderResource();
        when(hapiClient.send(any(Follow.class))).thenReturn(createSignMandateOrderResource);

        // When
        SlimpayOrder slimpayOrder = slimpayRestApiClient.createSignMandateOrder(USER_ID);

        // Then
        assertThat(slimpayOrder).isNotNull();
        assertThat(slimpayOrder.getOrderId()).isEqualTo(ORDER_ID);
        assertThat(slimpayOrder.getRedirectUrl()).isEqualTo(APPROVAL_LINK);
    }

    @Test(expected = SlimpayClientException.class)
    public void givenSlimpayApiThrowRuntimeExceptionWhenCallCreateSignMandateOrderThenReturnSlimpayClientExceptionShouldBeThrown() throws Exception {
        // Given
        when(hapiClient.send(any(Follow.class))).thenThrow(new RuntimeException());

        // When
        slimpayRestApiClient.createSignMandateOrder(USER_ID);

        // Then thrown Slimpay exception
    }

    @Test
    public void givenSlimpayApiThrowHttpExceptionWithResponseBodyWhenCallCreateSignMandateOrderThenReturnSlimpayClientExceptionShouldBeThrown() throws Exception {
        // Given
        Integer httpErrorCode = 400;
        String responseBody = "{\"code\" : 124,\"message\" : \"Invalid familyName property\" }";
        when(hapiClient.send(any(Follow.class))).thenThrow(createHttpException(httpErrorCode, responseBody));

        try {
            // When
            slimpayRestApiClient.createSignMandateOrder(USER_ID);
            fail("SlimpayClientException should be thrown");
        } catch (Exception e) {
            // Then
            assertThat(e).isInstanceOf(SlimpayClientException.class);

            SlimpayClientException sce = (SlimpayClientException) e;
            assertThat(sce.getHttpStatusCode()).isEqualTo(httpErrorCode);
            assertThat(sce.getSlimpayErrorCode()).isEqualTo(SlimpayErrorCode.CODE_124);
        }
    }

    @Test
    public void givenSlimpayApiThrowHttpExceptionWithEmptyResponseBodyWhenCallCreateSignMandateOrderThenReturnSlimpayClientExceptionShouldBeThrown() throws Exception {
        // Given
        Integer httpErrorCode = 500;
        String responseBody = "";
        when(hapiClient.send(any(Follow.class))).thenThrow(createHttpException(httpErrorCode, responseBody));

        try {
            // When
            slimpayRestApiClient.createSignMandateOrder(USER_ID);
            fail("SlimpayClientException should be thrown");
        } catch (Exception e) {
            // Then
            assertThat(e).isInstanceOf(SlimpayClientException.class);

            SlimpayClientException sce = (SlimpayClientException) e;
            assertThat(sce.getHttpStatusCode()).isEqualTo(httpErrorCode);
            assertThat(sce.getSlimpayErrorCode()).isEqualTo(SlimpayErrorCode.UNKNOWN);
        }
    }

    @Test
    public void givenSlimpayApiReturnSuccessWhenCallCreateAmendMandateOrderThenReturnSlimpayOrderWithIdAndRedirectUrl() throws Exception {
        // Given
        Resource createSignMandateOrderResource = createOrderResource();
        when(hapiClient.send(any(Follow.class))).thenReturn(createSignMandateOrderResource);

        // When
        SlimpayOrder slimpayOrder = slimpayRestApiClient.createAmendMandateOrder(USER_ID, MANDATE_REFERENCE);

        // Then
        assertThat(slimpayOrder).isNotNull();
        assertThat(slimpayOrder.getOrderId()).isEqualTo(ORDER_ID);
        assertThat(slimpayOrder.getRedirectUrl()).isEqualTo(APPROVAL_LINK);
    }

    @Test(expected = SlimpayClientException.class)
    public void givenSlimpayApiThrowRuntimeExceptionWhenCallCreateAmendMandateOrderThenReturnSlimpayClientExceptionShouldBeThrown() throws Exception {
        // Given
        when(hapiClient.send(any(Follow.class))).thenThrow(new RuntimeException());

        // When
        slimpayRestApiClient.createAmendMandateOrder(USER_ID, MANDATE_REFERENCE);

        // Then thrown Slimpay exception
    }

    @Test
    public void givenSlimpayApiThrowHttpExceptionWithResponseBodyWhenCallCreateAmendMandateOrderThenReturnSlimpayClientExceptionShouldBeThrown() throws Exception {
        // Given
        Integer httpErrorCode = 400;
        String responseBody = "{\"code\": 199, \"message\": \"Mandate not found\"}";
        when(hapiClient.send(any(Follow.class))).thenThrow(createHttpException(httpErrorCode, responseBody));

        try {
            // When
            slimpayRestApiClient.createSignMandateOrder(USER_ID);
            fail("SlimpayClientException should be thrown");
        } catch (Exception e) {
            // Then
            assertThat(e).isInstanceOf(SlimpayClientException.class);

            SlimpayClientException sce = (SlimpayClientException) e;
            assertThat(sce.getHttpStatusCode()).isEqualTo(httpErrorCode);
            assertThat(sce.getSlimpayErrorCode()).isEqualTo(SlimpayErrorCode.CODE_199);
        }
    }

    @Test
    public void givenSlimpayApiThrowHttpExceptionWithEmptyResponseBodyWhenCallCreateAmendMandateOrderThenReturnSlimpayClientExceptionShouldBeThrown() throws Exception {
        // Given
        Integer httpErrorCode = 500;
        String responseBody = "";
        when(hapiClient.send(any(Follow.class))).thenThrow(createHttpException(httpErrorCode, responseBody));

        try {
            // When
            slimpayRestApiClient.createSignMandateOrder(USER_ID);
            fail("SlimpayClientException should be thrown");
        } catch (Exception e) {
            // Then
            assertThat(e).isInstanceOf(SlimpayClientException.class);

            SlimpayClientException sce = (SlimpayClientException) e;
            assertThat(sce.getHttpStatusCode()).isEqualTo(httpErrorCode);
            assertThat(sce.getSlimpayErrorCode()).isEqualTo(SlimpayErrorCode.UNKNOWN);
        }
    }

    @Test
    public void givenUserHavingSlimpayMandateWhenCallGetMandateThenReturnTheMandate() throws Exception {
        // Given
        //Search subscriber api call
        Resource searchSubscriberResource = createSearchSubscribersResourceWithSubscriber(GET_MANDATE_LINK);
        when(hapiClient.send(any(Follow.class))).thenReturn(searchSubscriberResource);

        Resource mandateResource = createMandateResource();
        Resource bankAccountResource = createBankAccountResource();
        when(hapiClient.send(any(Request.class)))
                //Get mandate api call
                .thenReturn(mandateResource)
                //Get bank account api call
                .thenReturn(bankAccountResource);

        // When
        SlimpayMandate slimpayMandate = slimpayRestApiClient.getMandate(USER_ID);

        // Then
        assertThat(slimpayMandate).isNotNull();
        assertThat(slimpayMandate.getReference()).isEqualTo(MANDATE_REFERENCE);
        assertThat(slimpayMandate.getDateSigned()).isEqualTo(LocalDateTime.parse(MANDATE_DATE_SIGNED, DATE_TIME_FORMATTER));
        assertThat(slimpayMandate.getBic()).isEqualTo(BIC);
        assertThat(slimpayMandate.getIban()).isEqualTo(IBAN);
        assertThat(slimpayMandate.getBankName()).isEqualTo(BANK_NAME);
    }

    @Test
    public void givenUserNotKnownBySlimpayWhenCallGetMandateThenReturnNull() throws Exception {
        // Given
        // Get subscriber api call
        when(hapiClient.send(any(Follow.class))).thenReturn(new Resource.Builder().build());

        // When
        SlimpayMandate slimpayMandate = slimpayRestApiClient.getMandate(USER_ID);

        // Then
        assertThat(slimpayMandate).isNull();
    }

    @Test
    public void givenUserKnownBySlimpayButDoesntHaveMandateWhenCallGetMandateThenReturnNull() throws Exception {
        // Given
        //Get subscriber api call
        when(hapiClient.send(any(Follow.class))).thenReturn(createSearchSubscribersResourceWithSubscriber(null));

        // When
        SlimpayMandate slimpayMandate = slimpayRestApiClient.getMandate(USER_ID);

        // Then
        assertThat(slimpayMandate).isNull();
    }

    @Test(expected = SlimpayClientException.class)
    public void givenSlimpaySearchSubscribersApiThrowRuntimeExceptionWhenCallGetMandateThenThrowSlimpayClientException() throws Exception {
        // Given
        //Search subscriber api call
        when(hapiClient.send(any(Follow.class))).thenThrow(new RuntimeException());

        // When
        slimpayRestApiClient.getMandate(USER_ID);

        // Then throw SlimpayClientException
    }

    @Test(expected = SlimpayClientException.class)
    public void givenSlimpayGetMandateApiThrowRuntimeExceptionWhenCallGetMandateThenThrowSlimpayClientException() throws Exception {
        // Given
        //Search subscriber api call
        Resource searchSubscriberResource = createSearchSubscribersResourceWithSubscriber(GET_MANDATE_LINK);
        when(hapiClient.send(any(Follow.class))).thenReturn(searchSubscriberResource);

        when(hapiClient.send(any(Request.class)))
                //Get mandate api call
                .thenThrow(new RuntimeException());
        // When
        slimpayRestApiClient.getMandate(USER_ID);

        // Then throw SlimpayClientException
    }

    @Test(expected = SlimpayClientException.class)
    public void givenSlimpayGetBankAccountApiThrowRuntimeExceptionWhenCallGetMandateThrowSlimpayClientException() throws Exception {
        // Given
        //Search subscriber api call
        Resource searchSubscriberResource = createSearchSubscribersResourceWithSubscriber(GET_MANDATE_LINK);
        when(hapiClient.send(any(Follow.class))).thenReturn(searchSubscriberResource);

        Resource mandateResource = createMandateResource();
        when(hapiClient.send(any(Request.class)))
                //Get mandate api call
                .thenReturn(mandateResource)
                //Get bank account api call
                .thenThrow(new RuntimeException());

        // When
        slimpayRestApiClient.getMandate(USER_ID);

        // Then throw SlimpayClientException
    }

    @Test
    public void givenUserHavingActiveMandateWhenCallHasActiveMandateThenReturnTrue() throws SlimpayClientException {
        // Given
        SlimpayRestApiClient slimpayRestApiClientSpy = Mockito.spy(slimpayRestApiClient);
        doReturn(new SlimpayMandate().setStatus(SlimpayMandate.Status.ACTIVE)).when(slimpayRestApiClientSpy).getMandate(USER_ID);

        // When
        boolean hasActiveMandate = slimpayRestApiClientSpy.hasActiveMandate(USER_ID);

        // Then
        assertThat(hasActiveMandate).isTrue();
    }

    @Test
    public void givenUserDoesntHavingMandateWhenCallHasActiveMandateThenReturnTrue() throws SlimpayClientException {
        // Given
        SlimpayRestApiClient slimpayRestApiClientSpy = Mockito.spy(slimpayRestApiClient);
        doReturn(null).when(slimpayRestApiClientSpy).getMandate(USER_ID);

        //When
        boolean hasActiveMandate = slimpayRestApiClientSpy.hasActiveMandate(USER_ID);

        //Then
        assertThat(hasActiveMandate).isFalse();
    }

    @Test
    public void givenUserHavingRejectedMandateWhenCallHasActiveMandateThenReturnTrue() throws SlimpayClientException {
        // Given
        SlimpayRestApiClient slimpayRestApiClientSpy = Mockito.spy(slimpayRestApiClient);
        doReturn(new SlimpayMandate().setStatus(SlimpayMandate.Status.REJECTED)).when(slimpayRestApiClientSpy).getMandate(USER_ID);

        //When
        boolean hasActiveMandate = slimpayRestApiClientSpy.hasActiveMandate(USER_ID);

        //Then
        assertThat(hasActiveMandate).isFalse();
    }

    @Test
    public void givenErrorDuringGetUserMandateWhenCallHasActiveMandateThenReturnTrue() throws SlimpayClientException {
        // Given
        SlimpayRestApiClient slimpayRestApiClientSpy = Mockito.spy(slimpayRestApiClient);
        doThrow(new SlimpayClientException(null, null)).when(slimpayRestApiClientSpy).getMandate(USER_ID);

        //When
        boolean hasActiveMandate = slimpayRestApiClientSpy.hasActiveMandate(USER_ID);

        //Then
        assertThat(hasActiveMandate).isFalse();
    }

    @Test
    public void givenSlimpayErrorResponseBodyWithKnownErrorWhenCallParseErrorBodyResponseThenReturnSlimpayErrorCode() {
        // Given
        String responseBody = "{\"code\" : 907,\"message\" : \"Bad gateway\" }";

        // When
        SlimpayErrorCode slimpayErrorCode = slimpayRestApiClient.parseErrorBodyResponse(responseBody);

        // Then
        assertThat(slimpayErrorCode).isEqualTo(SlimpayErrorCode.CODE_907);
    }

    @Test
    public void givenSlimpayErrorResponseBodyWithUnknownErrorWhenCallParseErrorBodyResponseThenReturnSlimpayErrorCode() {
        // Given
        String responseBody = "{\"code\" : 45678,\"message\" : \"Unknown\" }";

        // When
        SlimpayErrorCode slimpayErrorCode = slimpayRestApiClient.parseErrorBodyResponse(responseBody);

        // Then
        assertThat(slimpayErrorCode).isEqualTo(SlimpayErrorCode.UNKNOWN);
    }

    @Test
    public void givenSlimpayErrorResponseBodyIsNullCallParseErrorBodyResponseThenReturnSlimpayErrorCode() {
        // Given
        String responseBody = null;

        // When
        SlimpayErrorCode slimpayErrorCode = slimpayRestApiClient.parseErrorBodyResponse(responseBody);

        // Then
        assertThat(slimpayErrorCode).isEqualTo(SlimpayErrorCode.UNKNOWN);
    }

    private Resource createOrderResource() {
        JsonObject state = Json.createObjectBuilder().add("id", ORDER_ID).build();

        Link userApprovalLink = mock(Link.class);
        when(userApprovalLink.getHref()).thenReturn(APPROVAL_LINK);

        Map<Rel, Object> links = new HashMap<>();
        links.put(ORDER_USER_APPROVAL_REL, userApprovalLink);

        return new Resource.Builder().setState(state)
                .setLinks(links)
                .build();
    }

    private Resource createSearchSubscribersResourceWithSubscriber(String mandateUrl) {
        Resource subscriberResource;
        if(mandateUrl != null) {
            Link getMandateLink = mock(Link.class);
            when(getMandateLink.getHref()).thenReturn(GET_MANDATE_LINK);

            Map<Rel, Object> links = new HashMap<>();
            links.put(GET_MANDATE_REL, getMandateLink);

            subscriberResource = new Resource.Builder()
                    .setLinks(links)
                    .build();
        } else {
            subscriberResource = new Resource.Builder()
                    .build();
        }

        Map<Rel, Object> embeddedResources = new HashMap<>();
        embeddedResources.put(SUBSCRIBERS_REL, Collections.singletonList(subscriberResource));

        return new Resource.Builder()
                .setEmbeddedResources(embeddedResources)
                .build();
    }

    private Resource createMandateResource() {
        JsonObject state = Json.createObjectBuilder()
                .add("reference", MANDATE_REFERENCE)
                .add("dateSigned", MANDATE_DATE_SIGNED)
                .build();

        Link getBankAccountLink = mock(Link.class);
        when(getBankAccountLink.getHref()).thenReturn(GET_BANK_ACCOUNT_LINK);

        Map<Rel, Object> links = new HashMap<>();
        links.put(GET_BANK_ACCOUNT_REL, getBankAccountLink);

        return new Resource.Builder()
                .setState(state)
                .setLinks(links)
                .build();
    }

    private Resource createBankAccountResource() {
        JsonObject state = Json.createObjectBuilder()
                .add("bic", BIC)
                .add("iban", IBAN)
                .add("institutionName", BANK_NAME)
                .build();

        return new Resource.Builder()
                .setState(state)
                .build();
    }

    private HttpException createHttpException(int statusCode, String responseBody) {
        CloseableHttpResponse httpResponse = createHttpResponse(statusCode);
        return new HttpException(null, httpResponse, responseBody);
    }

    private CloseableHttpResponse createHttpResponse(int statusCode) {
        return new CloseableHttpResponse() {
            @Override
            public void close() throws IOException {

            }

            @Override
            public StatusLine getStatusLine() {
                return new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), statusCode, "");
            }

            @Override
            public void setStatusLine(StatusLine statusLine) {

            }

            @Override
            public void setStatusLine(ProtocolVersion protocolVersion, int i) {

            }

            @Override
            public void setStatusLine(ProtocolVersion protocolVersion, int i, String s) {

            }

            @Override
            public void setStatusCode(int i) throws IllegalStateException {

            }

            @Override
            public void setReasonPhrase(String s) throws IllegalStateException {

            }

            @Override
            public HttpEntity getEntity() {
                return null;
            }

            @Override
            public void setEntity(HttpEntity httpEntity) {

            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public void setLocale(Locale locale) {

            }

            @Override
            public ProtocolVersion getProtocolVersion() {
                return null;
            }

            @Override
            public boolean containsHeader(String s) {
                return false;
            }

            @Override
            public Header[] getHeaders(String s) {
                return new Header[0];
            }

            @Override
            public Header getFirstHeader(String s) {
                return null;
            }

            @Override
            public Header getLastHeader(String s) {
                return null;
            }

            @Override
            public Header[] getAllHeaders() {
                return new Header[0];
            }

            @Override
            public void addHeader(Header header) {

            }

            @Override
            public void addHeader(String s, String s1) {

            }

            @Override
            public void setHeader(Header header) {

            }

            @Override
            public void setHeader(String s, String s1) {

            }

            @Override
            public void setHeaders(Header[] headers) {

            }

            @Override
            public void removeHeader(Header header) {

            }

            @Override
            public void removeHeaders(String s) {

            }

            @Override
            public HeaderIterator headerIterator() {
                return null;
            }

            @Override
            public HeaderIterator headerIterator(String s) {
                return null;
            }

            @Override
            public HttpParams getParams() {
                return null;
            }

            @Override
            public void setParams(HttpParams httpParams) {

            }
        };
    }

}