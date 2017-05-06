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
        return Messages.ColorBlindSupport_DisplayName();
    }

    @Override
    public ColorBlindProperty newInstance(StaplerRequest req, JSONObject formData) {
        return req.bindJSON(ColorBlindProperty.class, formData);
    }

    @Override
    public UserProperty newInstance(User arg0) {
        return null;
    }
}