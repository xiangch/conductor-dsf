package cn.com.do1.conductor.client.discovery;

import cn.com.do1.conductor.client.discovery.feign.TaskFeignClient;
import com.netflix.conductor.client.exception.ConductorClientException;
import com.netflix.conductor.common.run.ExternalStorageLocation;
import com.netflix.conductor.common.utils.ExternalPayloadStorage;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

public class PayloadStorage implements ExternalPayloadStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayloadStorage.class);
    private TaskFeignClient feign;

    public  PayloadStorage(TaskFeignClient feign){
        this.feign =feign;
    }

    @Override
    public ExternalStorageLocation getLocation(
        Operation operation, PayloadType payloadType, String path) {
        String uri;
        switch (payloadType) {
            case WORKFLOW_INPUT:
            case WORKFLOW_OUTPUT:
                uri = "workflow";
                break;
            case TASK_INPUT:
            case TASK_OUTPUT:
                uri = "tasks";
                break;
            default:
                throw new ConductorClientException(
                    String.format(
                        "Invalid payload type: %s for operation: %s",
                        payloadType.toString(), operation.toString()));
        }
        return feign.externalstoragelocation(path,operation.toString(),payloadType.toString());
    }

    @Override
    public void upload(String uri, InputStream payload, long payloadSize) {
        HttpURLConnection connection = null;
        try {
            URL url = new URI(uri).toURL();

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");

            try (BufferedOutputStream bufferedOutputStream =
                     new BufferedOutputStream(connection.getOutputStream())) {
                long count = IOUtils.copy(payload, bufferedOutputStream);
                bufferedOutputStream.flush();
                // Check the HTTP response code
                int responseCode = connection.getResponseCode();
                if (Response.Status.fromStatusCode(responseCode).getFamily()
                    != Response.Status.Family.SUCCESSFUL) {
                    String errorMsg =
                        String.format("Unable to upload. Response code: %d", responseCode);
                    LOGGER.error(errorMsg);
                    throw new ConductorClientException(errorMsg);
                }
                LOGGER.debug(
                    "Uploaded {} bytes to uri: {}, with HTTP response code: {}",
                    count,
                    uri,
                    responseCode);
            }
        } catch (URISyntaxException | MalformedURLException e) {
            String errorMsg = String.format("Invalid path specified: %s", uri);
            LOGGER.error(errorMsg, e);
            throw new ConductorClientException(errorMsg, e);
        } catch (IOException e) {
            String errorMsg = String.format("Error uploading to path: %s", uri);
            LOGGER.error(errorMsg, e);
            throw new ConductorClientException(errorMsg, e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (payload != null) {
                    payload.close();
                }
            } catch (IOException e) {
                LOGGER.warn("Unable to close inputstream when uploading to uri: {}", uri);
            }
        }
    }

    @Override
    public InputStream download(String uri) {
        HttpURLConnection connection = null;
        String errorMsg;
        try {
            URL url = new URI(uri).toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(false);

            // Check the HTTP response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                LOGGER.debug(
                    "Download completed with HTTP response code: {}",
                    connection.getResponseCode());
                return org.apache.commons.io.IOUtils.toBufferedInputStream(
                    connection.getInputStream());
            }
            errorMsg = String.format("Unable to download. Response code: %d", responseCode);
            LOGGER.error(errorMsg);
            throw new ConductorClientException(errorMsg);
        } catch (URISyntaxException | MalformedURLException e) {
            errorMsg = String.format("Invalid uri specified: %s", uri);
            LOGGER.error(errorMsg, e);
            throw new ConductorClientException(errorMsg, e);
        } catch (IOException e) {
            errorMsg = String.format("Error downloading from uri: %s", uri);
            LOGGER.error(errorMsg, e);
            throw new ConductorClientException(errorMsg, e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
