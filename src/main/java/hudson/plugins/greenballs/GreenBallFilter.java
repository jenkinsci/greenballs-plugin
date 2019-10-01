package hudson.plugins.greenballs;

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

import hudson.init.InitMilestone;
import hudson.model.User;
import jenkins.model.Jenkins;

/**
 * This filter intercept calls to the static resource blue ball and return a
 * forward response to the local plugin green ball resource.
 *
 * @author Asgeir Storesund Nilsen
 */
public class GreenBallFilter implements Filter {

    private static final String patternStr = "/(\\d{2}x\\d{2})/%s(_anime|)\\.(gif|png)";

    public static final Pattern patternBlue = Pattern.compile(String.format(patternStr, "blue"));

    public static final Pattern patternRed = Pattern.compile(String.format(patternStr, "red"));

    public static final Pattern patternYellow = Pattern.compile(String.format(patternStr, "yellow"));

    private static final Logger logger = Logger.getLogger("hudson.plugins.greenballs");

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
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
        // Fix for JENKINS-28422
        Jenkins jenkins = Jenkins.getInstance();
        if (InitMilestone.EXTENSIONS_AUGMENTED.compareTo(jenkins.getInitLevel()) > 0) {
            return null;
        }
        if (uri.contains("plugin/greenballs/")) {
            return null;
        }
        String basePath = "/static/.../plugin/greenballs/";
        Matcher m;
        User user = jenkins.getUser(Jenkins.getAuthentication().getName());
        if (user != null) {
            ColorBlindProperty colorBlindProperty = user.getProperty(ColorBlindProperty.class);
            if (colorBlindProperty != null && colorBlindProperty.isEnabledColorBlindSupport()) {
                if ((m = patternBlue.matcher(uri)).find()) {
                    return basePath + "colorblind" + m.group(1) + "/green" + m.group(2) + ".gif";
                } else if ((m = patternRed.matcher(uri)).find()) {
                    return basePath + "colorblind" + m.group(1) + "/red" + m.group(2) + ".gif";
                } else if ((m = patternYellow.matcher(uri)).find()) {
                    return basePath + "colorblind" + m.group(1) + "/yellow" + m.group(2) + ".gif";
                }
                return null;
            }
        }

        if ((m = patternBlue.matcher(uri)).find()) {
            return basePath + m.group(1) + "/green" + m.group(2) + "." + m.group(3);
        }
        return null;
    }

    @Override
    public void destroy() {
    }
}
