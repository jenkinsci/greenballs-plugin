package hudson.plugins.greenballs;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.Plugin;
import hudson.util.ColorPalette;
import hudson.util.PluginServletFilter;

public class GreenBallsPlugin extends Plugin {

    transient final Logger logger = Logger.getLogger("hudson.plugins.greenballs");

    @Override
    public void start() throws Exception {
        super.start();
        PluginServletFilter.addFilter(new GreenBallFilter());
        try {
            Field colorValue = Color.class.getDeclaredField("value");
            colorValue.setAccessible(true);
            colorValue.setInt(ColorPalette.BLUE, new Color(172, 218, 0).getRGB());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to change BLUE ColorPalette", e);
        }
    }

    @Override
    public void doDynamic(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
        rsp.setHeader("Cache-Control", "public, s-maxage=86400");
        super.doDynamic(req, rsp);
    }

}