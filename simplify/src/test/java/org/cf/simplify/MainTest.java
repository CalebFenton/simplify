package org.cf.simplify;

import org.cf.smalivm.UnhandledVirtualException;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MainTest {

    @Test
    public void runsLauncher() throws IOException, UnhandledVirtualException {
        Launcher launcher = mock(Launcher.class);
        Main.setLauncher(launcher);
        String[] args = new String[0];
        Main.main(args);

        verify(launcher, times(1)).run(eq(args));
    }

}
