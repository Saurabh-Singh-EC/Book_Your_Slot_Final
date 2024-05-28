package com.codeWithSrb.BookYourSlot.Service;

import com.codeWithSrb.BookYourSlot.Model.Gadgets;
import com.codeWithSrb.BookYourSlot.Model.RequestType;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.InvalidParameterException;
import java.time.Duration;
import java.util.Arrays;

/**
 * it's a service class which accepts uri and request type and executes the request along with printing
 * the response code and response body
 */
@Service
public class RestConsumerService {

    private static final String BODY = "";

    public RestConsumerService() {
    }

    public void executeRestConsumer(String uri, RequestType requestType) throws URISyntaxException, IOException, InterruptedException {

        switch(requestType) {
            case GET:
                executeGetRequest(uri);
                break;
            case POST:
                executePostRequest(uri, BODY);
                break;
            case PUT:
                executePutRequest();
                break;
            case DELETE:
                executeDeleteRequest();
                break;
            default:
                throw new InvalidParameterException(String.format("Invalid Request type %s", requestType));
        }
    }

    public void executeGetRequest(String uri) throws URISyntaxException, IOException, InterruptedException {

        HttpResponse<String> response;
        HttpClient client = HttpClient.newBuilder()
                .authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("postman", "password".toCharArray());
                    }
                })
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .GET()
                .version(HttpClient.Version.HTTP_2)
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(10))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Gson gson = new Gson();
        Gadgets[] gadgets = gson.fromJson(response.body(), Gadgets[].class);

        System.out.println("response code: " + response);
        Arrays.stream(gadgets).forEach(gadget -> {
            System.out.println(gadget.toString());
        });
    }

    public void executePostRequest(String uri, String requestBody) throws URISyntaxException, IOException, InterruptedException {

        HttpResponse<String> response;

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .version(HttpClient.Version.HTTP_2)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type","application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofMinutes(1))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Post request sent successfully, response code :" + response.statusCode());

        Gson gson = new Gson();
        Gadgets gadget = gson.fromJson(response.body(), Gadgets.class);
        System.out.println(gadget.toString());
    }

    public void executeDeleteRequest() {
        //TODO: execute delete request needs to be implemented
    }

    public void executePutRequest() {
        //TODO: execute put request needs to be implemented
    }
}
