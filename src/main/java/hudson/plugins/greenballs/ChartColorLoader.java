package hudson.plugins.greenballs;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class ChartColorLoader {
  final Logger logger = Logger.getLogger("hudson.plugins.greenballs");

  public Color loadColorFromProperties() {
    // preset the result --> if the config file is not found, the standard color will be returned
    Color result = new Color(172, 218, 0);

    try {
      // get jenkins_home
      String jenkinsHome = System.getenv("JENKINS_HOME");
      // if the last slash is missing, add it
      if (!jenkinsHome.endsWith("/")) {
        jenkinsHome += "/";
      }

      // load the properties file
      File file = new File(jenkinsHome + "plugins/greenballs/greenballs.properties");
      logger.severe("path: " + file.getAbsolutePath());
      FileInputStream fileInput = new FileInputStream(file);
      Properties props = new Properties();
      props.load(fileInput);
      fileInput.close();

      // resolve the color
      String rgbProperty = props.getProperty("chart_rgb");
      result = resolveColorFromProperty(rgbProperty);
    } catch (IOException e) {
      logger.warning("\"greenballs.properties\" file was not found. Chart color will be defaulted.");
    } catch (NumberFormatException ex) {
      logger.severe("Property \"chart-rgb\" could not be parsed to an Integer. " +
                        "Make sure only full numbers splitted by commas are used.");
    }

    return result;
  }

  private Color resolveColorFromProperty(String rgbString) throws NumberFormatException{
    String[] split = rgbString.split(",");
    Integer r = Integer.parseInt(split[0]);
    Integer g = Integer.parseInt(split[1]);
    Integer b = Integer.parseInt(split[2]);
    return new Color(r, g, b);
  }
}
