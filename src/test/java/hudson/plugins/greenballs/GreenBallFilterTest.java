package hudson.plugins.greenballs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.regex.Matcher;

import org.junit.Test;

public class GreenBallFilterTest {

    static void patternMatchGif(Matcher m) {
        assertThat(m.find(), is(true));
        assertThat(m.group(1), equalTo("48x48"));
        assertThat(m.group(2), equalTo(""));
        assertThat(m.group(3), equalTo("gif"));
    }

    static void patternMatchPng(Matcher m) {
        assertThat(m.find(), is(true));
        assertThat(m.group(1), equalTo("48x48"));
        assertThat(m.group(2), equalTo(""));
        assertThat(m.group(3), equalTo("png"));
    }

    static void patternMatchAnime(Matcher m) {
        assertThat(m.find(), is(true));
        assertThat(m.group(1), equalTo("48x48"));
        assertThat(m.group(2), equalTo("_anime"));
    }

    static void patternMatchColorblind(Matcher m) {
        assertThat(m.find(), is(true));
        assertThat(m.group(1), equalTo("48x48"));
        assertThat(m.group(2), equalTo(""));
    }

    static void patternMatchColorblindAnime(Matcher m) {
        assertThat(m.find(), is(true));
        assertThat(m.group(1), equalTo("48x48"));
        assertThat(m.group(2), equalTo("_anime"));
    }

    @Test
    public void patternShouldMatch() {
        final Matcher m = GreenBallFilter.patternBlue.matcher("/nocacheImages/48x48/blue.gif");
        final Matcher m2 = GreenBallFilter.patternYellow.matcher("/nocacheImages/48x48/yellow.gif");
        final Matcher m3 = GreenBallFilter.patternRed.matcher("/nocacheImages/48x48/red.gif");
        patternMatchGif(m);
        patternMatchGif(m2);
        patternMatchGif(m3);
    }

    @Test
    public void patternShouldMatchPNG() {
        final Matcher m = GreenBallFilter.patternBlue.matcher("/nocacheImages/48x48/blue.png");
        final Matcher m2 = GreenBallFilter.patternYellow.matcher("/nocacheImages/48x48/yellow.png");
        final Matcher m3 = GreenBallFilter.patternRed.matcher("/nocacheImages/48x48/red.png");
        patternMatchPng(m);
        patternMatchPng(m2);
        patternMatchPng(m3);
    }

    @Test
    public void patternShouldAlsoMatch() {
        final Matcher m = GreenBallFilter.patternBlue.matcher("/nocacheImages/48x48/blue_anime.gif");
        final Matcher m2 = GreenBallFilter.patternYellow.matcher("/nocacheImages/48x48/yellow_anime.gif");
        final Matcher m3 = GreenBallFilter.patternRed.matcher("/nocacheImages/48x48/red_anime.gif");
        patternMatchAnime(m);
        patternMatchAnime(m2);
        patternMatchAnime(m3);
    }

    @Test
    public void patternShouldNotMatch() {
        final Matcher m = GreenBallFilter.patternBlue.matcher("/nocacheImages/48x48/red_anime.gif");
        assertThat(m.find(), is(false));
        final Matcher m2 = GreenBallFilter.patternYellow.matcher("/nocacheImages/48x48/blue_anime.gif");
        assertThat(m2.find(), is(false));
        final Matcher m3 = GreenBallFilter.patternRed.matcher("/nocacheImages/48x48/yello_anime.gif");
        assertThat(m3.find(), is(false));
    }

    @Test
    public void patternShouldMatchColorblind() {
        final Matcher m = GreenBallFilter.patternBlue.matcher("/nocacheImages/colorblind/48x48/blue.gif");
        final Matcher m2 = GreenBallFilter.patternYellow.matcher("/nocacheImages/colorblind/48x48/yellow.gif");
        final Matcher m3 = GreenBallFilter.patternRed.matcher("/nocacheImages/colorblind/48x48/red.gif");
        patternMatchColorblind(m);
        patternMatchColorblind(m2);
        patternMatchColorblind(m3);
    }

    @Test
    public void patternShouldMatchColorblindAnime() {
        final Matcher m = GreenBallFilter.patternBlue.matcher("/nocacheImages/colorblind/48x48/blue_anime.gif");
        final Matcher m2 = GreenBallFilter.patternYellow.matcher("/nocacheImages/colorblind/48x48/yellow_anime.gif");
        final Matcher m3 = GreenBallFilter.patternRed.matcher("/nocacheImages/colorblind/48x48/red_anime.gif");
        patternMatchColorblindAnime(m);
        patternMatchColorblindAnime(m2);
        patternMatchColorblindAnime(m3);
    }
}
