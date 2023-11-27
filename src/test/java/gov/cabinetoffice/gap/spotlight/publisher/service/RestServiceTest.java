package gov.cabinetoffice.gap.spotlight.publisher.service;

import gov.cabinetoffice.gap.spotlight.publisher.model.SpotlightBatch;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestServiceTest {

    private static final OkHttpClient mockedHttpClient = mock(OkHttpClient.class);
    private final UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
    ArgumentCaptor<Request> httpRequestCaptor = ArgumentCaptor.forClass(Request.class);

    @BeforeEach
    void beforeEach() {
        reset(mockedHttpClient);
    }

    @Nested
    class sendGetRequest {

        @Test
        void shouldSuccessfullyCompleteGetRequest_WithoutParams() throws Exception {

            final Call mockCall = mock(Call.class);
            final Response mockResponse = mock(Response.class);
            final ResponseBody mockResponseBody = mock(ResponseBody.class);
            when(mockedHttpClient.newCall(any())).thenReturn(mockCall);
            when(mockCall.execute()).thenReturn(mockResponse);
            when(mockResponse.isSuccessful()).thenReturn(true);
            when(mockResponse.body()).thenReturn(mockResponseBody);
            when(mockResponseBody.string()).thenReturn("{ \"id\":" + uuid + " }");

            try (MockedStatic<RestService> mockedRestService = mockStatic(RestService.class)) {
                final  SpotlightBatch expectedResponse = SpotlightBatch.builder()
                        .id(uuid)
                        .build();

                mockedRestService.when(() -> RestService.defaultRequestBuilder()).thenCallRealMethod();
                mockedRestService.when(() -> RestService.sendGetRequest(any(), any(), anyString(), any()))
                        .thenCallRealMethod();

                final SpotlightBatch response = RestService.sendGetRequest(
                        mockedHttpClient,
                        null,
                        "/test/url",
                        SpotlightBatch.class);

                verify(mockedHttpClient).newCall(httpRequestCaptor.capture());

                final Request capturedRequest = httpRequestCaptor.getValue();

                assertThat(capturedRequest.method()).isEqualTo("GET");
                assertThat(capturedRequest.url()).hasToString("http://localhost:8080/api/test/url");
                assertThat(response).isEqualTo(expectedResponse);

            }
        }


        @Test
        void shouldSuccessfullyCompleteGetRequest_WithParams() throws Exception {

            final Call mockCall = mock(Call.class);
            final Response mockResponse = mock(Response.class);
            final ResponseBody mockResponseBody = mock(ResponseBody.class);
            when(mockedHttpClient.newCall(any())).thenReturn(mockCall);
            when(mockCall.execute()).thenReturn(mockResponse);
            when(mockResponse.isSuccessful()).thenReturn(true);
            when(mockResponse.body()).thenReturn(mockResponseBody);
            when(mockResponseBody.string()).thenReturn("{ \"id\":" + uuid + " }");

            try (MockedStatic<RestService> mockedRestService = mockStatic(RestService.class)) {
                final  SpotlightBatch expectedResponse = SpotlightBatch.builder()
                        .id(uuid)
                        .build();

                mockedRestService.when(() -> RestService.defaultRequestBuilder()).thenCallRealMethod();
                mockedRestService.when(() -> RestService.sendGetRequest(any(), anyMap(), anyString(), any()))
                        .thenCallRealMethod();

                final Map<String, String> params = Map.of("newParam", "paramValue");

                final SpotlightBatch response = RestService.sendGetRequest(
                        mockedHttpClient,
                        params,
                        "/test/url",
                        SpotlightBatch.class);

                verify(mockedHttpClient).newCall(httpRequestCaptor.capture());

                final Request capturedRequest = httpRequestCaptor.getValue();

                assertThat(capturedRequest.method()).isEqualTo("GET");
                assertThat(capturedRequest.url()).hasToString("http://localhost:8080/api/test/url?newParam=paramValue");
                assertThat(response).isEqualTo(expectedResponse);


            }

        }

        @Test
        void shouldThrowRuntimeExceptionWhenRequestIsUnsuccessful() throws Exception {

            final Call mockCall = mock(Call.class);
            final Response mockResponse = mock(Response.class);
            when(mockedHttpClient.newCall(any())).thenReturn(mockCall);
            when(mockCall.execute()).thenReturn(mockResponse);
            when(mockResponse.isSuccessful()).thenReturn(false);


            try (MockedStatic<RestService> mockedRestService = mockStatic(RestService.class)) {

                mockedRestService.when(() -> RestService.defaultRequestBuilder()).thenCallRealMethod();
                mockedRestService.when(() -> RestService.sendGetRequest(any(), any(), anyString(), any()))
                        .thenCallRealMethod();

                assertThrows(RuntimeException.class,
                        () -> RestService.sendGetRequest(
                                mockedHttpClient,
                                null,
                                "/test/url",
                                SpotlightBatch.class));
            }
        }

    }

    @Nested
    class sendPostRequest {

        @Test
        void shouldSuccessfullyCompletePostRequestWithDTOBody() throws Exception {
            final Call mockCall = mock(Call.class);
            final Response mockResponse = mock(Response.class);
            final ResponseBody mockResponseBody = mock(ResponseBody.class);

            when(mockedHttpClient.newCall(any())).thenReturn(mockCall);
            when(mockCall.execute()).thenReturn(mockResponse);
            when(mockResponse.isSuccessful()).thenReturn(true);
            when(mockResponse.body()).thenReturn(mockResponseBody);
            when(mockResponseBody.string()).thenReturn("{ \"id\":" + uuid + " }");

            try (MockedStatic<RestService> mockedRestService = mockStatic(RestService.class)) {

                final  SpotlightBatch body = SpotlightBatch.builder()
                        .id(uuid)
                        .build();

                mockedRestService.when(() -> RestService.defaultRequestBuilder()).thenCallRealMethod();
                mockedRestService.when(() -> RestService.executePost(any(), any(), anyString(), any())).thenCallRealMethod();
                mockedRestService
                        .when(() -> RestService.sendPostRequest(any(), any(SpotlightBatch.class), anyString(), any()))
                        .thenCallRealMethod();

                RestService.sendPostRequest(mockedHttpClient, body, "/test/url", SpotlightBatch.class);

                verify(mockedHttpClient).newCall(httpRequestCaptor.capture());
                final Request capturedRequest = httpRequestCaptor.getValue();

                final Buffer bufferToReadBody = new Buffer();
                capturedRequest.body().writeTo(bufferToReadBody);

                assertThat(capturedRequest.method()).isEqualTo("POST");
                assertThat(capturedRequest.url()).hasToString("http://localhost:8080/api/test/url");
                assertThat(bufferToReadBody.readUtf8()).isEqualTo("{\"id\":" + "\"" + uuid + "\"" + ",\"version\":1}");

            }
        }

        @Test
        void shouldSuccessfullyCompletePostRequestWithNoBody() throws Exception {
            final Call mockCall = mock(Call.class);
            final Response mockResponse = mock(Response.class);
            final ResponseBody mockResponseBody = mock(ResponseBody.class);

            when(mockedHttpClient.newCall(any())).thenReturn(mockCall);
            when(mockCall.execute()).thenReturn(mockResponse);
            when(mockResponse.isSuccessful()).thenReturn(true);
            when(mockResponse.body()).thenReturn(mockResponseBody);
            when(mockResponseBody.string()).thenReturn(null);

            try (MockedStatic<RestService> mockedRestService = mockStatic(RestService.class)) {

                mockedRestService.when(() -> RestService.defaultRequestBuilder()).thenCallRealMethod();
                mockedRestService.when(() -> RestService.executePost(any(), any(), anyString(), any())).thenCallRealMethod();
                mockedRestService
                        .when(() -> RestService.sendPostRequest(any(), any(), anyString(), any()))
                        .thenCallRealMethod();

                RestService.sendPostRequest(mockedHttpClient, null, "/test/url", null);

                verify(mockedHttpClient).newCall(httpRequestCaptor.capture());
                final Request capturedRequest = httpRequestCaptor.getValue();

                final Buffer bufferToReadBody = new Buffer();
                capturedRequest.body().writeTo(bufferToReadBody);

                assertThat(capturedRequest.method()).isEqualTo("POST");
                assertThat(capturedRequest.url()).hasToString("http://localhost:8080/api/test/url");
                assertThat(bufferToReadBody.readUtf8()).isEqualTo("");

            }
        }

        @Test
        void shouldThrowRuntimeExceptionWhenRequestIsUnsuccessful() throws Exception {
            final Call mockCall = mock(Call.class);
            final Response mockResponse = mock(Response.class);
            when(mockedHttpClient.newCall(any())).thenReturn(mockCall);
            when(mockCall.execute()).thenReturn(mockResponse);
            when(mockResponse.isSuccessful()).thenReturn(false);

            try (MockedStatic<RestService> mockedRestService = mockStatic(RestService.class)) {

                mockedRestService.when(() -> RestService.defaultRequestBuilder()).thenCallRealMethod();
                mockedRestService.when(() -> RestService.executePost(any(), any(), anyString(), any())).thenCallRealMethod();
                mockedRestService.when(() -> RestService.sendPostRequest(any(), any(), anyString(), any())).thenCallRealMethod();

                assertThrows(RuntimeException.class, () -> RestService.sendPostRequest(
                        mockedHttpClient,
                        "MockBodyValue",
                        "/test/url",
                        String.class));

            }
        }

    }
    @Nested
    class sendPatchRequest {

        @Test
        void shouldSuccessfullyCompletePatchRequest() throws Exception {

            final Call mockCall = mock(Call.class);
            final Response mockResponse = mock(Response.class);
            when(mockedHttpClient.newCall(any())).thenReturn(mockCall);
            when(mockCall.execute()).thenReturn(mockResponse);
            when(mockResponse.isSuccessful()).thenReturn(true);

            try (MockedStatic<RestService> mockedRestService = mockStatic(RestService.class)) {
                final  SpotlightBatch body = SpotlightBatch.builder()
                        .id(uuid)
                        .build();

                mockedRestService.when(() -> RestService.defaultRequestBuilder()).thenCallRealMethod();
                mockedRestService.when(() -> RestService.sendPatchRequest(any(), any(), anyString())).thenCallRealMethod();

                RestService.sendPatchRequest(mockedHttpClient, body, "/test/url");

                verify(mockedHttpClient).newCall(httpRequestCaptor.capture());
                final Request capturedRequest = httpRequestCaptor.getValue();

                final Buffer bufferToReadBody = new Buffer();
                capturedRequest.body().writeTo(bufferToReadBody);

                assertThat(capturedRequest.method()).isEqualTo("PATCH");
                assertThat(capturedRequest.url()).hasToString("http://localhost:8080/api/test/url");
                assertThat(bufferToReadBody.readUtf8()).isEqualTo("{\"id\":" + "\"" + uuid + "\"" + ",\"version\":1}");

            }
        }

        @Test
        void shouldSuccessfullyCompletePatchRequestWithNoBody() throws Exception {
            final Call mockCall = mock(Call.class);
            final Response mockResponse = mock(Response.class);
            when(mockedHttpClient.newCall(any())).thenReturn(mockCall);
            when(mockCall.execute()).thenReturn(mockResponse);
            when(mockResponse.isSuccessful()).thenReturn(true);

            try (MockedStatic<RestService> mockedRestService = mockStatic(RestService.class)) {

                mockedRestService.when(() -> RestService.defaultRequestBuilder()).thenCallRealMethod();
                mockedRestService.when(() -> RestService.executePost(any(), any(), anyString(), any())).thenCallRealMethod();
                mockedRestService
                        .when(() -> RestService.sendPatchRequest(any(), any(), anyString()))
                        .thenCallRealMethod();

                RestService.sendPatchRequest(mockedHttpClient, null, "/test/url");

                verify(mockedHttpClient).newCall(httpRequestCaptor.capture());
                final Request capturedRequest = httpRequestCaptor.getValue();

                final Buffer bufferToReadBody = new Buffer();
                capturedRequest.body().writeTo(bufferToReadBody);

                assertThat(capturedRequest.method()).isEqualTo("PATCH");
                assertThat(capturedRequest.url()).hasToString("http://localhost:8080/api/test/url");
                assertThat(bufferToReadBody.readUtf8()).isEmpty();

            }
        }

        @Test
        void shouldThrowRuntimeExceptionWhenRequestIsUnsuccessful() throws Exception {
            final Call mockCall = mock(Call.class);
            final Response mockResponse = mock(Response.class);
            when(mockedHttpClient.newCall(any())).thenReturn(mockCall);
            when(mockCall.execute()).thenReturn(mockResponse);
            when(mockResponse.isSuccessful()).thenReturn(false);

            try (MockedStatic<RestService> mockedRestService = mockStatic(RestService.class)) {

                final SpotlightBatch body = SpotlightBatch.builder().id(uuid).build();

                mockedRestService.when(() -> RestService.defaultRequestBuilder()).thenCallRealMethod();
                mockedRestService.when(() -> RestService.sendPatchRequest(any(), any(), anyString())).thenCallRealMethod();

                assertThrows(RuntimeException.class, () -> RestService.sendPatchRequest(mockedHttpClient, body, "/test/url"));
            }
        }

    }

}


