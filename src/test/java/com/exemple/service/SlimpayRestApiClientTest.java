package com.exemple.service;

import com.slimpay.hapiclient.http.HapiClient;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SlimpayRestApiClientTest {

    @Mock
    private HapiClient hapiClient;

    private SlimpayRestApiClient slimpayRestApiClient;

    @Before
    public void setUp() {
        slimpayRestApiClient = new SlimpayRestApiClient(hapiClient);
    }

    @Test
    public void createSignMandateOrder() {
        Assume.assumeFalse("Test not implemented", false);
    }

    @Test
    public void createAmendMandateOrder() {
        Assume.assumeFalse("Test not implemented", false);
    }

    @Test
    public void getMandate() {
        Assume.assumeFalse("Test not implemented", false);
    }

    @Test
    public void hasActiveMandate() {
        Assume.assumeFalse("Test not implemented", false);
    }
}