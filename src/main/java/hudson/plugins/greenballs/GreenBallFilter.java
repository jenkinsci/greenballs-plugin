package hudson.plugins.greenballs;

import hudson.model.Hudson;
import hudson.model.User;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Asgeir Storesund Nilsen
 */
public class GreenBallFilter implements Filter {

  final String patternStr = "/(\\d{2}x\\d{2})/%s(_anime|)\\.(gif|png)";

  final Pattern patternBlue = Pattern.compile(String.format(patternStr, "blue"));

  final Pattern patternRed = Pattern.compile(String.format(patternStr, "red"));

  final Pattern patternYellow = Pattern.compile(String.format(patternStr, "yellow"));

  final Logger logger = Logger.getLogger("hudson.plugins.greenballs");

  public void init(FilterConfig config) throws ServletException {
  }

  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
    if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {
      final HttpServletRequest httpServletRequest = (HttpServletRequest) req;
      final HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
      final String uri = httpServletRequest.getRequestURI();
      if (uri.endsWith(".gif") || uri.endsWith(".png")) {
        String newImageUrl = mapImage(uri);
        if (newImageUrl != null) {
          if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Redirecting {0} to {1}", new Object[] { uri, newImageUrl });
          }
          RequestDispatcher dispatcher = httpServletRequest.getRequestDispatcher(newImageUrl);
          dispatcher.forward(httpServletRequest, httpServletResponse);
          return;
        }
      }
    }
    chain.doFilter(req, resp);
  }

  private String mapImage(String uri) {
    if (uri.contains("plugin/greenballs/")) return null;
    Matcher m;
    User user = Hudson.getInstance().getUser(Hudson.getAuthentication().getName());
    if (user!=null) {
      ColorBlindProperty colorBlindProperty = user.getProperty(ColorBlindProperty.class);
      if (colorBlindProperty != null && colorBlindProperty.isEnabledColorBlindSupport()) {
        if ((m = patternBlue.matcher(uri)).find()) {
          return "/plugin/greenballs/colorblind/" + m.group(1) + "/green" + m.group(2) + ".gif";
        } else if ((m = patternRed.matcher(uri)).find()) {
          return "/plugin/greenballs/colorblind/" + m.group(1) + "/red" + m.group(2) + ".gif";
        } else if ((m = patternYellow.matcher(uri)).find()) {
          return "/plugin/greenballs/colorblind/" + m.group(1) + "/yellow" + m.group(2) + ".gif";
        }
        return null;
      }
    }

    if ((m = patternBlue.matcher(uri)).find()) {
      return "/plugin/greenballs/" + m.group(1) + "/green" + m.group(2) + "." + m.group(3);
    }
    return null;
  }

  public void destroy() {
  }
}