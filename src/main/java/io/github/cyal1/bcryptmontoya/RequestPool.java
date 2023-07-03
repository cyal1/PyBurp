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
            BcryptMontoya.http.sendRequest(request);
        });
    }

    public void sendRequest(HttpRequest request, PyFunction pyFunc) {
        executor.execute(() -> {
            HttpRequestResponse httpRequestResponse = BcryptMontoya.http.sendRequest(request);
            PyObject pythonArguments = Py.java2py(httpRequestResponse);
            pyFunc.__call__( pythonArguments );
        });
    }

    public void shutdown() {
        executor.shutdownNow();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                BcryptMontoya.Api.logging().output().println("Some tasks were not terminated.");
            }
        } catch (InterruptedException e) {
            BcryptMontoya.Api.logging().logToError(e.getMessage());
        }
        BcryptMontoya.Api.logging().output().println("RequestPool shutdown");
    }
}
