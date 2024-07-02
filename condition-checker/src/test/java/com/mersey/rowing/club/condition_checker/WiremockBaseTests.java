package com.mersey.rowing.club.condition_checker;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.mersey.rowing.club.condition_checker.mockSetup.WireMockConfiguration;
import com.mersey.rowing.club.condition_checker.mockSetup.WireMockSetup;
import com.mersey.rowing.club.condition_checker.model.openweatherapi.OpenWeatherResponse;
import com.mersey.rowing.club.condition_checker.utils.TestUtils;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = { "open-weather-api.key=test","open-weather-api.baseUrl=http://localhost:5050" })
@Import(WireMockConfiguration.class)
@Slf4j
public class WiremockBaseTests {

    protected static TestUtils testUtils = new TestUtils();

    protected static OpenWeatherResponse expectedGenericOpenWeatherResponse;

    protected static String url;
    protected static String expectedResponseBody;

    @BeforeAll
    static void setUpBeforeClass() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.baseURI = Context.BASE_URL;
        WireMockSetup mockSetup = new WireMockSetup();
        WireMock wireMock = new WireMock("localhost", 5050);

        url = Context.BASE_URL + "/timemachine?lat=53.39293&lon=-2.98532&dt=" + Context.TEST_EPOCH_TIME + "&appid=" + Context.DUMMY_API_KEY;

        log.info("<<<<< SETTING UP WIREMOCK MAPPINGS >>>>>");
        MappingBuilder mappingBuilder = mockSetup.setupGenericOpenWeatherMapping();
        expectedResponseBody = testUtils.getOpenWeatherResponseAsString(null);
        wireMock.register(mappingBuilder);
        log.info("<<<<< WIREMOCK MAPPINGS COMPLETE SETUP >>>>>");

        expectedGenericOpenWeatherResponse = testUtils.getOpenWeatherResponse(1718653615);
    }

    @AfterAll
    static void tearDownAfterClass() {
        log.info("<<<<< CLEARING WIREMOCK MAPPINGS >>>>>");
        WireMock wireMock = new WireMock("localhost", 5050);
        wireMock.resetMappings();
        log.info("<<<<< WIREMOCK MAPPINGS CLEARED >>>>>");
    }
}