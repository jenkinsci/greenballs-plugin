package org.jvnet.hudson.plugins.greenballs;

import hudson.Plugin;
import hudson.util.PluginServletFilter;

/**
 * Entry point of a plugin.
 *
 * <p>
 * There must be one {@link Plugin} class in each plugin.
 * See javadoc of {@link Plugin} for more about what can be done on this class.
 *
 * @author Kohsuke Kawaguchi
 */
public class PluginImpl extends Plugin {

  @Override
  public void start() throws Exception {
    super.start();
    PluginServletFilter.addFilter(new GreenBallFilter());
  }

}
