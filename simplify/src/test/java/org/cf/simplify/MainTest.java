package org.cf.simplify;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MainTest {

    @Test
    public void runsLauncher() throws IOException {
        Launcher launcher = mock(Launcher.class);
        Main.setLauncher(launcher);
        String[] args = new String[0];
        Main.main(args);

        verify(launcher, times(1)).run(eq(args));
    }

}
