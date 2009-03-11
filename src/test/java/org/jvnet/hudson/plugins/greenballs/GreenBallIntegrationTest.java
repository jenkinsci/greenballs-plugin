package org.jvnet.hudson.plugins.greenballs;

import com.gargoylesoftware.htmlunit.Page;
import java.net.URL;
import org.jvnet.hudson.test.HudsonTestCase;

/**
 *
 * @author Asgeir S. Nilsen
 */
public class GreenBallIntegrationTest extends HudsonTestCase {

  static String join(String first, String second) {
    if (first.endsWith("/"))
      first = first.substring(0,first.length()-1);
    if (second.startsWith("/"))
      second = second.substring(1);
    return first + "/" + second;
  }

  public void testRedirection() throws Exception {
    WebClient wc = new WebClient();
    URL url = new URL(join(wc.getContextPath(), "images/48x48/blue.gif"));
    URL expected = new URL(join(wc.getContextPath(), "plugin/greenballs/48x48/green.gif"));
    Page page = wc.getPage(url);
    assertEquals(expected, page.getWebResponse().getUrl());
  }

  public void testPassthrough() throws Exception {
    WebClient wc = new WebClient();
    URL url = new URL(join(wc.getContextPath(), "images/48x48/red.gif"));
    URL expected = new URL(join(wc.getContextPath(), "images/48x48/red.gif"));
    Page page = wc.getPage(url);
    assertEquals(expected, page.getWebResponse().getUrl());
  }

}
