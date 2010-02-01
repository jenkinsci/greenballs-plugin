/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hudson.plugins.greenballs;

import hudson.plugins.greenballs.GreenBallFilter;

import java.util.regex.Matcher;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class GreenBallFilterTest {

  GreenBallFilter greenBallFilter;
  
  @Before
  public void setup() {
    greenBallFilter = new GreenBallFilter();
  }
  
  @Test
  public void patternShouldMatch() {
    final Matcher m = greenBallFilter.pattern.matcher("/nocacheImages/48x48/blue.gif");
    assertThat(m.matches(), is(true));
    assertThat(m.group(1), equalTo("48x48"));
    assertThat(m.group(2), equalTo(""));
  }

  @Test
  public void patternShouldAlsoMatch() {
    final Matcher m = greenBallFilter.pattern.matcher("/nocacheImages/48x48/blue_anime.gif");
    assertThat(m.matches(), is(true));
    assertThat(m.group(1), equalTo("48x48"));
    assertThat(m.group(2), equalTo("_anime"));
  }

  @Test
  public void patternShouldNotMatch() {
    final Matcher m = greenBallFilter.pattern.matcher("/nocacheImages/48x48/red_anime.gif");
    assertThat(m.matches(), is(false));
  }
}