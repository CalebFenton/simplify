package com.android.server.connectivity.metrics;

import com.google.protobuf.nano.MessageNano;

public final class IpConnectivityLogClass {
    private IpConnectivityLogClass() {
    }

    public static final class DefaultNetworkEvent extends MessageNano {
        public static int NONE = 17;
    }
}
