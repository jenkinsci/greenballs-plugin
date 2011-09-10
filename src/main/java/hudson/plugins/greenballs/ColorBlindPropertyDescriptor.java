package hudson.plugins.greenballs;

import hudson.Extension;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import hudson.model.User;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

@Extension
public class ColorBlindPropertyDescriptor extends UserPropertyDescriptor {

  public ColorBlindPropertyDescriptor() {
    super(ColorBlindProperty.class);
  }

  @Override
  public String getDisplayName() {
    return "Color blind suport";
  }

  private ColorBlindProperty newInstanceIfJSONIsNull(StaplerRequest req) throws FormException {
    if (req.getParameter("enabledColorBlindSupport") != null) {
      return new ColorBlindProperty(Boolean.parseBoolean(req.getParameter("enabledColorBlindSupport")));
    } else {
      return new ColorBlindProperty();
    }
  }

  @Override
  public ColorBlindProperty newInstance(StaplerRequest req, JSONObject formData) throws hudson.model.Descriptor.FormException {
    if (formData == null) {
      return newInstanceIfJSONIsNull(req);
    }
    if (formData.has("enabledColorBlindSupport")) {
      return req.bindJSON(ColorBlindProperty.class, formData);
    }
    return new ColorBlindProperty();
  }

  @Override
  public UserProperty newInstance(User arg0) {
    return null;
  }
}