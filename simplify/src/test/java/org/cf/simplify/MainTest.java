package org.cf.simplify;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import org.cf.smalivm.exception.UnhandledVirtualException;
import org.junit.jupiter.api.Test;

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
