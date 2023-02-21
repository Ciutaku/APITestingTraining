package tests;

import org.core.client.AuthClient;
import org.core.enums.AccessType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthTest {

    @Test
    public void getWriteTokenTest() {
        String writeToken = AuthClient.getToken(AccessType.WRITE);
        Assertions.assertFalse(writeToken.isEmpty());
    }

    @Test
    public void getReadTokenTest() {
        String readToken = AuthClient.getToken(AccessType.READ);
        Assertions.assertFalse(readToken.isEmpty());
    }
}
