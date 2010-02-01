package hudson.plugins.greenballs;

import hudson.Plugin;
import hudson.PluginWrapper;
import hudson.util.PluginServletFilter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Entry point of a plugin.
 * 
 * <p>
 * There must be one {@link Plugin} class in each plugin. See javadoc of
 * {@link Plugin} for more about what can be done on this class.
 * 
 * @author Kohsuke Kawaguchi
 */
public class PluginImpl extends Plugin {

    PluginWrapper wrapper;

    @Override
    public void start() throws Exception {
        super.start();
        PluginServletFilter.addFilter(new GreenBallFilter());
        try {
            wrapper = null;
            Field wrapperField = Plugin.class.getDeclaredField("wrapper");
            wrapperField.setAccessible(true);
            wrapper = (PluginWrapper) wrapperField.get(this);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Green Balls!");
        }
    }

    @Override
    public void doDynamic(StaplerRequest req, StaplerResponse rsp)
            throws IOException, ServletException {
        rsp.setHeader("Cache-Control", "public, s-maxage=86400");
        if (wrapper == null) {
            super.doDynamic(req, rsp);
            return;
        }
        String path = req.getRestOfPath();

        if (path.length() == 0)
            path = "/";

        if (path.indexOf("..") != -1 || path.length() < 1) {
            // don't serve anything other than files in the sub directory.
            rsp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // use serveLocalizedFile to support automatic locale selection
        rsp.serveLocalizedFile(req,
                new URL(wrapper.baseResourceURL, '.' + path), 86400000);
    }
}
