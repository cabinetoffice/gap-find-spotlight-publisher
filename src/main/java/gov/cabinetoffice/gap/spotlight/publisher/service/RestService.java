package gov.cabinetoffice.gap.spotlight.publisher.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import gov.cabinetoffice.gap.spotlight.publisher.exceptions.SpotlightPublisherHttpException;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Base64;
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
    private static final String PUBLIC_KEY = System.getenv("PUBLIC_KEY");


    private RestService() {
        throw new IllegalStateException("Utility class");
    }

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
                logger.info("Error occurred while getting  {} with error {}, and body {}", endpoint, response.code(), response.body());
                throw new SpotlightPublisherHttpException();
            }
        }
    }

    public static <T> T sendPostRequest(OkHttpClient restClient, T requestBodyDto, String endpoint, Class<T> clazz) throws Exception {
        RequestBody requestBody;
        if (requestBodyDto != null) {
            requestBody = RequestBody.create(gson.toJson(requestBodyDto), JSON);
        } else {
            requestBody = RequestBody.create("", JSON);
        }
        logger.info("Sending post request to {}", endpoint);

        return executePost(restClient, requestBody, endpoint, clazz);
    }

    public static <T> T executePost(OkHttpClient restClient, RequestBody body, String endpoint, Class<T> clazz) throws Exception {
        final Request request = defaultRequestBuilder().url(BACKEND_API_URL + endpoint).post(body).build();

        try (Response response = restClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                logger.info("Successfully posted to {}", endpoint);

                // you can only make one call to body.string before the stream is closed so don't refactor this out...
                final String bodyString = response.body().string();
                logger.info("body {}", bodyString);

                if (bodyString == null) {
                    return null;
                }

                return gson.fromJson(bodyString, clazz);
            } else {
                logger.info("Error occurred while posting  {} with error {}, and body {}", endpoint, response.code(), response.body());
                throw new SpotlightPublisherHttpException("Error occurred while posting to " + endpoint);
            }
        } catch (Exception e) {
            logger.error("Error occurred", e);
            throw e;
        }
    }


    public static <T> T sendPatchRequest(OkHttpClient restClient, T requestBodyDTO, String endpoint, Class<T> clazz) throws Exception {

        RequestBody requestBody;
        if (requestBodyDTO != null) {
            requestBody = RequestBody.create(gson.toJson(requestBodyDTO), JSON);
        } else {
            requestBody = RequestBody.create("", JSON);
        }

        logger.info("Sending patch request to {}", endpoint);

        final Request request = defaultRequestBuilder().url(BACKEND_API_URL + endpoint).patch(requestBody).build();

        try (Response response = restClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                logger.info("Successfully patched to {}", endpoint);

                // you can only make one call to body.string before the stream is closed so don't refactor this out...
                final String bodyString = response.body().string();
                logger.info("body {}", bodyString);

                if (bodyString == null) {
                    return null;
                }

                return gson.fromJson(bodyString, clazz);
            } else {
                logger.info("Error occurred while patching  {} with error {}, and body {}", endpoint, response.code(), response.body());

                throw new SpotlightPublisherHttpException("Error occurred while patching to " + endpoint);
            }
        } catch (Exception e) {
            logger.error("Error occurred", e);
            throw e;
        }
    }

    /**
     * Adds encrypted LAMBDA_AUTHORIZATION_SECRET as an Authorization header to every outbound REST call
     */
    public static Request.Builder defaultRequestBuilder() {
        final String encryptedSecret = encrypt(LAMBDA_AUTHORIZATION_SECRET, PUBLIC_KEY);
        logger.info("Secret successfully encrypted");

        return new Request.Builder().addHeader("Authorization", encryptedSecret);
    }

    public static String encrypt(String secret, String publicKey) {

        try {
            final byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
            final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            final PublicKey rsaPublicKey = keyFactory.generatePublic(keySpec);
            final Cipher encryptCipher = Cipher.getInstance("RSA");

            encryptCipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
            final byte[] cipherText = encryptCipher.doFinal(secret.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(cipherText);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while encrypting the secret " + e);
        }
    }


}
