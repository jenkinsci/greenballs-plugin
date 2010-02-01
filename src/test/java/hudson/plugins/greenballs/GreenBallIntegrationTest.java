package hudson.plugins.greenballs;

import java.net.URL;
import java.util.Date;

import org.apache.commons.httpclient.util.DateUtil;
import org.jvnet.hudson.test.HudsonTestCase;

import com.gargoylesoftware.htmlunit.Page;

/**
 * 
 * @author Asgeir S. Nilsen
 */
public class GreenBallIntegrationTest extends HudsonTestCase {

    static String join(String first, String second) {
        if (first.endsWith("/"))
            first = first.substring(0, first.length() - 1);
        if (second.startsWith("/"))
            second = second.substring(1);
        return first + "/" + second;
    }

    public void testRedirection() throws Exception {
        WebClient wc = new WebClient();
        URL url = new URL(join(wc.getContextPath(), "images/48x48/blue.gif"));
        URL expected = new URL(join(wc.getContextPath(),
                "plugin/greenballs/48x48/green.gif"));
        Page page = wc.getPage(url);
        assertEquals(expected, page.getWebResponse().getUrl());
    }

    public void testPassthrough() throws Exception {
        WebClient wc = new WebClient();
        URL url = new URL(join(wc.getContextPath(), "images/48x48/red.gif"));
        URL expected = new URL(
                join(wc.getContextPath(), "images/48x48/red.gif"));
        Page page = wc.getPage(url);
        assertEquals(expected, page.getWebResponse().getUrl());
    }

    public void testExpiresHeader() throws Exception {
        WebClient wc = new WebClient();
        URL url = new URL(join(wc.getContextPath(), "images/48x48/blue.gif"));
        Page page = wc.getPage(url);
        if (!new Date().before(DateUtil.parseDate(page.getWebResponse()
                .getResponseHeaderValue("Expires"))))
            System.err.println("Response has expired :-(");
    }

    public void testCacheControlHeader() throws Exception {
        WebClient wc = new WebClient();
        URL url = new URL(join(wc.getContextPath(), "images/48x48/blue.gif"));
        Page page = wc.getPage(url);
        assertTrue(page.getWebResponse()
                .getResponseHeaderValue("Cache-Control").contains("s-maxage"));
    }
}
