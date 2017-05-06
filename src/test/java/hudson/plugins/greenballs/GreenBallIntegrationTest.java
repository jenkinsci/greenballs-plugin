package hudson.plugins.greenballs;

import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.io.IOUtils;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;

import com.gargoylesoftware.htmlunit.WebResponse;

/**
 *
 * @author Asgeir S. Nilsen
 */
public class GreenBallIntegrationTest {

    @Rule
    public JenkinsRule r = new JenkinsRule();

    static String hash(String algorithm, byte[] data) throws NoSuchAlgorithmException {
        byte[] hash = MessageDigest.getInstance(algorithm).digest(data);
        BigInteger bi = new BigInteger(1, hash);
        String result = bi.toString(16);
        if (result.length() % 2 != 0) {
            return "0" + result;
        }

        return result;
    }

    @Test
    public void testGreenBall() throws Exception {
        WebClient webClient = r.createWebClient();
        URL url = webClient.createCrumbedUrl("images/48x48/blue.png");
        WebResponse webResponse = webClient.getPage(url).getWebResponse();
        String digest = hash("SHA-1", IOUtils.toByteArray(webResponse.getContentAsStream()));
        assertEquals("Content does not match expected digest", "67c7cf899449b2cdfbfaff929aef8e192e34e9cc", digest);
        assertTrue("Cache-Control header missing",
                webResponse.getResponseHeaderValue("Cache-Control").contains("s-maxage"));
        assertTrue("Response has expired",
                new Date().before(DateUtil.parseDate(webResponse.getResponseHeaderValue("Expires"))));
    }

}
