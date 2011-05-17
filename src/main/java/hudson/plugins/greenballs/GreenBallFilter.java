package hudson.plugins.greenballs;

import hudson.model.Hudson;

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

import org.springframework.util.StringUtils;

/**
 * 
 * @author Asgeir Storesund Nilsen
 */
public class GreenBallFilter implements Filter {

  private PluginImpl plugin;

  final String patternStr = "/(\\d{2}x\\d{2})/%s(_anime|)\\.(gif|png)";

  final Pattern patternBlue = Pattern.compile(String.format(patternStr, "blue"));

  final Pattern patternRed = Pattern.compile(String.format(patternStr, "red"));

  final Pattern patternYellow = Pattern.compile(String.format(patternStr, "yellow"));

  final Logger logger = Logger.getLogger("hudson.plugins.greenballs");

  public GreenBallFilter(PluginImpl plugin) {
    this.plugin = plugin;
  }

  public void init(FilterConfig config) throws ServletException {
  }

  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
    if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {
      final HttpServletRequest httpServletRequest = (HttpServletRequest) req;
      final HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
      final String uri = httpServletRequest.getRequestURI();
      if (uri.endsWith(".gif") || uri.endsWith(".png")) {
        boolean supportColorBlind = false;
        for (String accountName : StringUtils.commaDelimitedListToStringArray(plugin.getColorBlindPeople())) {
          if (accountName.trim().equals(Hudson.getAuthentication().getName())) {
            supportColorBlind = true;
            break;
          }
        }
        String newImageUrl = null;
        Matcher m;
        if (supportColorBlind) {
          if ((m = patternBlue.matcher(uri)).find()) {
            newImageUrl = "/plugin/greenballs/colorblind/" + m.group(1) + "/green" + m.group(2) + ".gif";
          } else if ((m = patternRed.matcher(uri)).find()) {
            newImageUrl = "/plugin/greenballs/colorblind/" + m.group(1) + "/red" + m.group(2) + ".gif";
          } else if ((m = patternYellow.matcher(uri)).find()) {
            newImageUrl = "/plugin/greenballs/colorblind/" + m.group(1) + "/yellow" + m.group(2) + ".gif";
          }
        } else {
          if ((m = patternBlue.matcher(uri)).find()) {
            newImageUrl = "/plugin/greenballs/" + m.group(1) + "/green" + m.group(2) + "." + m.group(3);
          }
        }
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

  public void destroy() {
  }
}