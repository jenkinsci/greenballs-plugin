package hudson.plugins.greenballs;

import hudson.model.UserProperty;
import hudson.model.User;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

@ExportedBean(defaultVisibility = 999)
public class ColorBlindProperty extends UserProperty {

  private boolean enabledColorBlindSupport;

  public ColorBlindProperty() {
    enabledColorBlindSupport = false;
  }

  @DataBoundConstructor
  public ColorBlindProperty(boolean enabledColorBlindSupport) {
    this.enabledColorBlindSupport = enabledColorBlindSupport;
  }

  @Exported
  public User getUser() {
    return user;
  }

  @Exported
  public boolean isEnabledColorBlindSupport() {
    return enabledColorBlindSupport;
  }

  public void setEnabledColorBlindSupport(boolean enabledColorBlindSupport) {
    this.enabledColorBlindSupport = enabledColorBlindSupport;
  }

  @Override
  public String toString() {
    return String.format("ColorBlindProperty [isEnabledColorBlindSupport=%s]", enabledColorBlindSupport);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (enabledColorBlindSupport ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ColorBlindProperty other = (ColorBlindProperty) obj;
    if (enabledColorBlindSupport != other.enabledColorBlindSupport)
      return false;
    return true;
  }
}