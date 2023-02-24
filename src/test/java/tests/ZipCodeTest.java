package tests;

import org.apache.http.HttpResponse;
import org.core.Helper;
import org.core.client.Client;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class ZipCodeTest {

    private static final String ZIP_CODE = "/zip-codes";
    private static final String ZIP_CODE_EXPAND = "/zip-codes/expand";

    @Test
    public void getAllZipCodesTest() {
        HttpResponse response = Client.doGet(ZIP_CODE);

        Assertions.assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void postZipCodesTest() throws IOException {
        HttpResponse response = Client.doPost(ZIP_CODE_EXPAND, "[ \"88888\"]");
        HttpResponse getResponseAfterPost = Client.doGet(ZIP_CODE);
        List<String> zipCodesListAfterPost = Helper.getZipCodeValues(getResponseAfterPost);

        Assertions.assertEquals(201, response.getStatusLine().getStatusCode());
        Assertions.assertTrue(zipCodesListAfterPost.contains("88888"));
    }

    @Test
    public void postDuplicateZipCodesInListTest() throws IOException {
        HttpResponse response = Client.doPost(ZIP_CODE_EXPAND, "[ \"21345\", \"21345\" ]");
        HttpResponse getResponseAfterPost = Client.doGet(ZIP_CODE);
        List<String> zipCodesListAfterPost = Helper.getZipCodeValues(getResponseAfterPost);
        List<String> duplicatesList = Helper.getDuplicates(zipCodesListAfterPost);

        Assertions.assertNotEquals(201, response.getStatusLine().getStatusCode());
        Assertions.assertTrue(duplicatesList.isEmpty());
    }

    @Test
    public void postDuplicateZipCodesInDBTest() throws IOException {

        HttpResponse getResponse = Client.doGet(ZIP_CODE);
        List<String> zipCodeslist = Helper.getZipCodeValues(getResponse);
        HttpResponse response = Client.doPost(ZIP_CODE_EXPAND, "[ \"" + zipCodeslist.get(0) + "\"]");

        HttpResponse getResponseAfterPost = Client.doGet(ZIP_CODE);
        List<String> zipCodesListAfterPost = Helper.getZipCodeValues(getResponseAfterPost);
        List<String> duplicatesList = Helper.getDuplicates(zipCodesListAfterPost);

        Assertions.assertNotEquals(201, response.getStatusLine().getStatusCode());
        Assertions.assertTrue(duplicatesList.isEmpty());
    }
}
