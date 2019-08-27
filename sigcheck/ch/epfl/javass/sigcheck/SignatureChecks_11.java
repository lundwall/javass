package ch.epfl.javass.sigcheck;

import ch.epfl.javass.LocalMain;
import ch.epfl.javass.RemoteMain;

public final class SignatureChecks_11 {
    private SignatureChecks_11() {}

    void checkLocalMain() throws Exception {
        String[] a = null;
        LocalMain.main(a);
    }

    void checkRemoteMain() throws Exception {
        String[] a = null;
        RemoteMain.main(a);
    }
}
