.class public LtestExamples;
.super Ljava/lang/Object;

.method const_trivialResolve()I
    .locals 1

    const/4 v0, 0x5

    const/4 v0, 0x7

    # v0 = 7 + 7 = 14
    add-int v0, v0, v0

    return v0
.end method

.method const_simpleResolve1()I
    .locals 2

    const/4 v0, 0x5

    const/4 v1, 0x7

    # v0 = 5 + 7 = 12
    add-int v0, v0, v1

    return v0
.end method

.method const_simpleResolve1()I
    .locals 2

    const/4 v0, 0x5

    const/4 v1, 0x7

    goto :add_me

    :end
    return v0

    :add_me

    # v0 = 5 + 7 = 12
    add-int v0, v0, v1

    goto :end
.end method

.method examples()V
    .locals 11

#Example of string lookup:
    const/16 v1, 0x47
    const/16 v2, -0x13
    const/16 v3, -0x11
    invoke-static {v1, v2, v3}, Lcom/android/system/admin/CIlOCClc;->oCIlCll(III)Ljava/lang/String;
    move-result-object v1

#Example class instantiation (after string resolution):
    const-string v0, "java.util.concurrent.TimeUnit"
    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    move-result-object v0

#Example virtual field lookup:
    const-string v0, "android.net.wifi.ScanResult"
    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    move-result-object v0
    const-string v1, "capabilities"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    invoke-virtual {v0, v10}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0

#Example static field lookup:
    const-string v0, "java.util.concurrent.TimeUnit"
    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    move-result-object v0
    const-string v1, "SECONDS"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v1

#Example field data lookup:
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v1
.end method

.method asdf()V
    :catchall_8
    move-exception v0
    invoke-virtual {v0}, Ljava/lang/Throwable;->getCause()Ljava/lang/Throwable;
    move-result-object v0
    throw v0

    :goto_9
    :try_start_a
    const-string v0, "android.telephony.TelephonyManager"
    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    move-result-object v0

    const-string v1, "getDataState"

    # Build parameter type array, 0 parameters
    const/4 v2, 0x0

    invoke-virtual {v0, v1, v2}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0

    const/4 v1, 0x0
    invoke-virtual {v0, p0, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0

    check-cast v0, Ljava/lang/Integer;
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I
    move-result v0
    :try_end_a
    .catchall {:try_start_a .. :try_end_a} :catchall_8

    # Becomes:
    #invoke-virtual {p0}, Landroid/telephony/TelephonyManager;->getDataState()I
    #move-result v0

.end method

.method asdf2()V
    :goto_0
    :try_start_1
    const/4 v0, 0x1
    new-array v2, v0, [Ljava/lang/Object;
    invoke-static {v3, v4}, Ljava/lang/Long;->valueOf(J)Ljava/lang/Long;
    move-result-object v0
    const/4 v3, 0x0
    aput-object v0, v2, v3

    # replace with: const-class v0, Ljava/util/concurrent/TimeUnit;
    const-string v0, "java.util.concurrent.TimeUnit"
    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    move-result-object v0

    const-string v3, "sleep"

    # Build parameter type array, 1 parameter
    const/4 v4, 0x1
    new-array v4, v4, [Ljava/lang/Class;
    sget-object v5, Ljava/lang/Long;->TYPE:Ljava/lang/Class;
    const/4 v6, 0x0
    aput-object v5, v4, v6

    invoke-virtual {v0, v3, v4}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0

    invoke-virtual {v0, v1, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    # void return!
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0
.end method

.method asdf2_resolved()V
    :goto_0
    :try_start_1
    const/4 v0, 0x1
    new-array v2, v0, [Ljava/lang/Object;
    invoke-static {v3, v4}, Ljava/lang/Long;->valueOf(J)Ljava/lang/Long;
    move-result-object v0
    const/4 v3, 0x0
    aput-object v0, v2, v3

    const-class v0, Ljava/util/concurrent/TimeUnit;

    const-string v3, "sleep"

    # Build parameter type array, 1 parameter
    const/4 v4, 0x1
    new-array v4, v4, [Ljava/lang/Class;
    sget-object v5, Ljava/lang/Long;->TYPE:Ljava/lang/Class;
    const/4 v6, 0x0
    aput-object v5, v4, v6

    invoke-virtual {v0, v3, v4}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0

    invoke-virtual {v0, v1, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    # void return!
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0
.end method

.method asdf3()V
    # standard string decryption, must run after de-dexguard string decoding
    const-string v1, "ZmRwG1UybmF3PD5hfQ=="

    invoke-static {v1}, Lcom/android/system/admin/OcOCclc;->ICOColc(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1
.end method

.method asdf4_1()V
    :catchall_1
    move-exception v0
    invoke-virtual {v0}, Ljava/lang/Throwable;->getCause()Ljava/lang/Throwable;
    move-result-object v0
    throw v0

    :goto_1
    :try_start_1
    const/4 v2, 0x1
    new-array v2, v2, [Ljava/lang/Object;

    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v0
    const/4 v3, 0x0
    aput-object v0, v2, v3

    const-string v0, "android.net.wifi.WifiManager"
    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    move-result-object v0

    const-string v3, "removeNetwork"
    const/4 v4, 0x1
    new-array v4, v4, [Ljava/lang/Class;

    sget-object v5, Ljava/lang/Integer;->TYPE:Ljava/lang/Class;
    const/4 v6, 0x0
    aput-object v5, v4, v6

    invoke-virtual {v0, v3, v4}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0

    invoke-virtual {v0, v1, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_1
.end method

.method asdf4_2()V
    :goto_3
    :try_start_7
    const/4 v0, 0x2
    new-array v2, v0, [Ljava/lang/Object;

    const/16 v0, 0x1388
    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v0
    const/4 v3, 0x1
    aput-object v0, v2, v3

    const/4 v0, 0x0
    aput-object p0, v2, v0

    const-string v0, "java.net.Socket"
    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    move-result-object v0

    const-string v3, "connect"
    const/4 v4, 0x2
    new-array v4, v4, [Ljava/lang/Class;

    const-string v5, "java.net.SocketAddress"
    invoke-static {v5}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    move-result-object v5
    const/4 v6, 0x0
    aput-object v5, v4, v6

    sget-object v5, Ljava/lang/Integer;->TYPE:Ljava/lang/Class;
    const/4 v6, 0x1
    aput-object v5, v4, v6

    invoke-virtual {v0, v3, v4}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0

    invoke-virtual {v0, v1, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_7
    .catchall {:try_start_7 .. :try_end_7} :catchall_3
.end method

.method asdf5_1()V
    :goto_2
    :try_start_5
    const-string v0, "java.net.Socket"

    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v0

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredConstructor([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;

    move-result-object v0

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Ljava/lang/reflect/Constructor;->newInstance([Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v1
    :try_end_5
    .catchall {:try_start_5 .. :try_end_5} :catchall_2
.end method

.method asdf5_2()V
    :goto_1
    :try_start_3
    const/4 v1, 0x2

    new-array v1, v1, [Ljava/lang/Object;

    const/16 v2, 0x50

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v2

    const/4 v3, 0x1

    aput-object v2, v1, v3

    const/4 v2, 0x0

    aput-object v0, v1, v2

    const-string v0, "java.net.InetSocketAddress"

    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v0

    const/4 v2, 0x2

    new-array v2, v2, [Ljava/lang/Class;

    const-string v3, "java.net.InetAddress"

    invoke-static {v3}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v3

    const/4 v4, 0x0

    aput-object v3, v2, v4

    sget-object v3, Ljava/lang/Integer;->TYPE:Ljava/lang/Class;

    const/4 v4, 0x1

    aput-object v3, v2, v4

    invoke-virtual {v0, v2}, Ljava/lang/Class;->getDeclaredConstructor([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;

    move-result-object v0

    invoke-virtual {v0, v1}, Ljava/lang/reflect/Constructor;->newInstance([Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object p0
    :try_end_3
    .catchall {:try_start_3 .. :try_end_3} :catchall_1
.end method