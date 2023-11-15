package gov.cabinetoffice.gap.spotlight.publisher.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Map;

public class RestService {

    public static final String BACKEND_API_URL = System.getenv("BACKEND_API_URL");
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static final Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class,
                    (JsonDeserializer<Instant>) (json, type, jsonDeserializationContext) -> OffsetDateTime
                            .parse(json.getAsJsonPrimitive().getAsString()).toInstant())
            .create();

    private static final Logger logger = LoggerFactory.getLogger(RestService.class);

    private static final String LAMBDA_AUTHORIZATION_SECRET = System.getenv("LAMBDA_AUTHORIZATION_SECRET");

    public static <T> T sendGetRequest(OkHttpClient restClient, Map<String, String> params, String endpoint, Class<T> clazz) throws Exception {

        HttpUrl.Builder httpBuilder = HttpUrl.get(BACKEND_API_URL + endpoint).newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }

        final Request request = defaultRequestBuilder().url(httpBuilder.build()).build();

        try (Response response = restClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                logger.info("Successfully fetched from {}", endpoint);
                return gson.fromJson(response.body().string(), clazz);
            } else {
                throw new RuntimeException();
            }
        }
    }

    public static <T> T sendPostRequest(OkHttpClient restClient, T requestBodyDto, String endpoint) throws Exception {
        RequestBody requestBody;
        if (requestBodyDto != null) {
            requestBody = RequestBody.create(gson.toJson(requestBodyDto), JSON);
        } else {
            requestBody = RequestBody.create("", JSON);
        }
        return executePost(restClient, requestBody, endpoint);
    }

    public static <T> T executePost(OkHttpClient restClient, RequestBody body, String endpoint) throws Exception {
        final Request request = defaultRequestBuilder().url(BACKEND_API_URL + endpoint).post(body).build();

        try (Response response = restClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                logger.info("Successfully posted to {}", endpoint);
                return response.body() != null ? (T) response.body() : null;
            } else {
                throw new RuntimeException("Error occured while posting to " + endpoint);
            }
        }
    }


    public static <T> void sendPatchRequest(OkHttpClient restClient, T requestBodyDTO, String endpoint) throws Exception {

        RequestBody requestBody;
        if (requestBodyDTO != null) {
            requestBody = RequestBody.create(gson.toJson(requestBodyDTO), JSON);
        } else {
            requestBody = RequestBody.create("", JSON);
        }

        final Request request = defaultRequestBuilder().url(BACKEND_API_URL + endpoint).patch(requestBody).build();

        try (Response response = restClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                logger.info("Successfully patched to {}", endpoint);
            }
            else {
                throw new RuntimeException("Error occured while patching to " + endpoint);
            }
        }
    }

    /**
     * Adds LAMBDA_AUTHORIZATION_SECRET as an Authorization header to every outbound REST call
     */
    public static Request.Builder defaultRequestBuilder() {
        return new Request.Builder().addHeader("Authorization", LAMBDA_AUTHORIZATION_SECRET);
    }

}
