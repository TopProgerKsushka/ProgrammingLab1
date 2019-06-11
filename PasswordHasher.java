import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {
    static String generateMD2(String string) throws NoSuchAlgorithmException {
        byte[] byteHash = MessageDigest.getInstance("MD2").digest(string.getBytes());
        BigInteger bigHash = new BigInteger(1, byteHash);
        String stringHash = bigHash.toString(16);
        while (stringHash.length() < 32)
            stringHash = "0" + stringHash;
        return stringHash;
    }
}
