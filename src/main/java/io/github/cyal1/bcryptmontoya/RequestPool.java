package io.github.cyal1.bcryptmontoya;

import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import org.python.core.Py;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RequestPool {
    private final ExecutorService executor;

    public RequestPool(int nThreads) {
        executor = Executors.newFixedThreadPool(nThreads);
    }

    public void sendRequest(HttpRequest request) {
        executor.execute(() -> {
            BcryptMontoya.Api.http().sendRequest(request);
        });
    }

    public void sendRequest(HttpRequest request, PyFunction pyFunc) {
        executor.execute(() -> {
            HttpRequestResponse httpRequestResponse = BcryptMontoya.Api.http().sendRequest(request);
            PyObject pythonArguments = Py.java2py(httpRequestResponse);
            pyFunc.__call__( pythonArguments );
        });
    }
    public void sendRequest(String url) {
        executor.execute(() -> {
            HttpRequest request = HttpRequest.httpRequestFromUrl(url);
            HttpRequestResponse httpRequestResponse = BcryptMontoya.Api.http().sendRequest(request);
        });
    }
    public void sendRequest(String url, PyFunction pyFunc) {
        executor.execute(() -> {
            HttpRequest request = HttpRequest.httpRequestFromUrl(url);
            HttpRequestResponse httpRequestResponse = BcryptMontoya.Api.http().sendRequest(request);
            PyObject pythonArguments = Py.java2py(httpRequestResponse);
            pyFunc.__call__( pythonArguments );
        });
    }

    public void shutdown() {
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BcryptMontoya.Api.logging().output().println("RequestPool shutdown");
    }
}
