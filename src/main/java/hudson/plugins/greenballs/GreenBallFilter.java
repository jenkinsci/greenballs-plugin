package hudson.plugins.greenballs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import hudson.init.InitMilestone;
import hudson.model.User;
import hudson.plugins.greenballs.ColorBlindUtils.Status;
import jenkins.model.Jenkins;

/**
 * This filter intercept calls to the static resource blue ball and return a
 * forward response to the local plugin green ball resource.
 *
 * @author Asgeir Storesund Nilsen
 * @author Nikolas Falco
 */
public class GreenBallFilter implements Filter {

    class ReadableContentHttpServletResponse extends HttpServletResponseWrapper {
        private ByteArrayOutputStream outputStream;
        private ServletOutputStream servletOutputStream;

        public ReadableContentHttpServletResponse(HttpServletResponse response) throws IOException {
            super(response);

            final ServletOutputStream responseOutputStream = response.getOutputStream();

            outputStream = new ByteArrayOutputStream();
            servletOutputStream = new ServletOutputStream() {
                private WriteListener writeListener = null;

                @Override
                public void write(int b) throws IOException {
                    outputStream.write(b);
                    // responseOutputStream.write(b);
                    if (writeListener != null) {
                        writeListener.notify();
                    }
                }

                @Override
                public void setWriteListener(WriteListener writeListener) {
                    this.writeListener = writeListener;
                }

                @Override
                public boolean isReady() {
                    return true;
                }
            };
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return new PrintWriter(outputStream);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return servletOutputStream;
        };

        public InputStream getInputStream() {
            return new ByteArrayInputStream(outputStream.toByteArray());
        }

        public void setStream(byte[] newResource) {
            outputStream = new ByteArrayOutputStream(newResource.length);
            try {
                outputStream.write(newResource);
                getResponse().getOutputStream().write(newResource);
            } catch (IOException e) {
                // never happens, I'm writing in memory
            }
        }
    }

    private final String patternStr = "/(\\d{2}x\\d{2})/%s(_anime|)\\.(gif|png)";
    final Pattern patternBlue = Pattern.compile(String.format(patternStr, "blue"));
    final Pattern patternRed = Pattern.compile(String.format(patternStr, "red"));
    final Pattern patternYellow = Pattern.compile(String.format(patternStr, "yellow"));
    final Logger logger = Logger.getLogger("hudson.plugins.greenballs");

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        boolean isAnimateImage = false;
        String uri = null;
        if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {
            final HttpServletRequest httpServletRequest = (HttpServletRequest) req;
            final HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
            uri = httpServletRequest.getRequestURI();
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
                isAnimateImage = isColorBlindEnabled() && isAnimateImage(uri);
                if (isAnimateImage) {
                    // wrap the response
                    resp = new ReadableContentHttpServletResponse(httpServletResponse);
                }
            }
        }
        chain.doFilter(req, resp);

        // if user has enable colour blind and it's an image ball than overlay the capital status char
        if (isAnimateImage && resp instanceof ReadableContentHttpServletResponse) {
            ReadableContentHttpServletResponse wrappedResp = (ReadableContentHttpServletResponse) resp;
            byte[] newResource = overlay(uri, wrappedResp.getInputStream());
            wrappedResp.setStream(newResource);
        }
    }

    private String mapImage(String uri) throws IOException {
        // Fix for JENKINS-28422
        Jenkins jenkins = Jenkins.getActiveInstance();
        if (InitMilestone.EXTENSIONS_AUGMENTED.compareTo(jenkins.getInitLevel()) > 0) {
            return null;
        }
        if (uri.contains("plugin/greenballs/")) {
            return null;
        }
        Matcher m = patternBlue.matcher(uri);
        if (m.find()) {
            return "/static/.../plugin/greenballs/" + m.group(1) + "/green" + m.group(2) + "." + m.group(3);
        }
        return null;
    }

    private boolean isColorBlindEnabled() {
        Jenkins jenkins = Jenkins.getActiveInstance();
        User user = jenkins.getUser(Jenkins.getAuthentication().getName());
        if (user != null) {
            ColorBlindProperty colorBlindProperty = user.getProperty(ColorBlindProperty.class);
            return (colorBlindProperty != null && colorBlindProperty.isEnabledColorBlindSupport());
        }
        return false;
    }

    private boolean isAnimateImage(String uri) throws IOException {
        return patternBlue.matcher(uri).find() || patternRed.matcher(uri).find() || patternYellow.matcher(uri).find();
    }

    private byte[] overlay(String uri, InputStream inputStream) throws IOException {
        Status status = null;
        if ((patternBlue.matcher(uri)).find()) {
            status = Status.SUCCESS;
        } else if ((patternRed.matcher(uri)).find()) {
            status = Status.FAILURE;
        } else if ((patternYellow.matcher(uri)).find()) {
            status = Status.UNSTABLE;
        }

        if (status != null) {
            return ColorBlindUtils.overlayAnimate(ImageIO.createImageInputStream(inputStream), status.getOverlay());
        }
        return null;
    }

    @Override
    public void destroy() {
    }
}