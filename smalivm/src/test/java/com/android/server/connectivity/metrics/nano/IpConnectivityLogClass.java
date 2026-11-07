package com.android.server.connectivity.metrics.nano;

import com.android.framework.protobuf.nano.MessageNano;

public final class IpConnectivityLogClass {
    private IpConnectivityLogClass() {
    }

    public static final class DefaultNetworkEvent extends MessageNano {
        public static int NONE = 17;
    }
}
