.class public Lorg/cf/obfuscated/DESCrypt;
.super Ljava/lang/Object;
.source "DESCrypt.java"


# static fields
.field private static cipher:Ljavax/crypto/Cipher;

.field private static mySecretKeyFactory:Ljavax/crypto/SecretKeyFactory;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 11
    sput-object v0, Lorg/cf/obfuscated/DESCrypt;->cipher:Ljavax/crypto/Cipher;

    #@3
    .line 12
    sput-object v0, Lorg/cf/obfuscated/DESCrypt;->mySecretKeyFactory:Ljavax/crypto/SecretKeyFactory;

    #@5
    .line 16
    :try_start_0
    const-string v0, "DES"

    #@7
    invoke-static {v0}, Ljavax/crypto/Cipher;->getInstance(Ljava/lang/String;)Ljavax/crypto/Cipher;

    #@a
    move-result-object v0

    #@b
    sput-object v0, Lorg/cf/obfuscated/DESCrypt;->cipher:Ljavax/crypto/Cipher;

    #@d
    .line 17
    const-string v0, "DES"

    #@f
    invoke-static {v0}, Ljavax/crypto/SecretKeyFactory;->getInstance(Ljava/lang/String;)Ljavax/crypto/SecretKeyFactory;

    #@12
    move-result-object v0

    #@13
    sput-object v0, Lorg/cf/obfuscated/DESCrypt;->mySecretKeyFactory:Ljavax/crypto/SecretKeyFactory;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@15
    .line 21
    :goto_0
    return-void

    #@16
    .line 18
    :catch_0
    move-exception v0

    #@17
    goto :goto_0
.end method

.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 9
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static decrypt([BLjava/lang/String;)Ljava/lang/String;
    .locals 8
    .param p0, "encrypted"    # [B
    .param p1, "keyString"    # Ljava/lang/String;

    #@0
    .prologue
    .line 41
    const/4 v0, 0x0

    #@1
    .line 43
    .local v0, "decrypted":Ljava/lang/String;
    :try_start_0
    const-string v6, "UTF8"

    #@3
    invoke-virtual {p1, v6}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    #@6
    move-result-object v3

    #@7
    .line 44
    .local v3, "encryptionKey":[B
    new-instance v5, Ljavax/crypto/spec/DESKeySpec;

    #@9
    invoke-direct {v5, v3}, Ljavax/crypto/spec/DESKeySpec;-><init>([B)V

    #@c
    .line 45
    .local v5, "myKeySpec":Ljava/security/spec/KeySpec;
    sget-object v6, Lorg/cf/obfuscated/DESCrypt;->mySecretKeyFactory:Ljavax/crypto/SecretKeyFactory;

    #@e
    invoke-virtual {v6, v5}, Ljavax/crypto/SecretKeyFactory;->generateSecret(Ljava/security/spec/KeySpec;)Ljavax/crypto/SecretKey;

    #@11
    move-result-object v4

    #@12
    .line 47
    .local v4, "key":Ljavax/crypto/SecretKey;
    sget-object v6, Lorg/cf/obfuscated/DESCrypt;->cipher:Ljavax/crypto/Cipher;

    #@14
    const/4 v7, 0x2

    #@15
    invoke-virtual {v6, v7, v4}, Ljavax/crypto/Cipher;->init(ILjava/security/Key;)V

    #@18
    .line 48
    sget-object v6, Lorg/cf/obfuscated/DESCrypt;->cipher:Ljavax/crypto/Cipher;

    #@1a
    invoke-virtual {v6, p0}, Ljavax/crypto/Cipher;->doFinal([B)[B

    #@1d
    move-result-object v1

    #@1e
    .line 49
    .local v1, "decryptedBytes":[B
    new-instance v6, Ljava/lang/String;

    #@20
    invoke-direct {v6, v1}, Ljava/lang/String;-><init>([B)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@23
    .line 52
    .end local v1    # "decryptedBytes":[B
    .end local v3    # "encryptionKey":[B
    .end local v4    # "key":Ljavax/crypto/SecretKey;
    .end local v5    # "myKeySpec":Ljava/security/spec/KeySpec;
    :goto_0
    return-object v6

    #@24
    .line 50
    :catch_0
    move-exception v2

    #@25
    .line 51
    .local v2, "e":Ljava/lang/Exception;
    invoke-virtual {v2}, Ljava/lang/Exception;->printStackTrace()V

    #@28
    .line 52
    const-string v6, ""

    #@2a
    goto :goto_0
.end method

.method public static encrypt(Ljava/lang/String;Ljava/lang/String;)[B
    .locals 8
    .param p0, "string"    # Ljava/lang/String;
    .param p1, "keyString"    # Ljava/lang/String;

    #@0
    .prologue
    .line 25
    :try_start_0
    const-string v6, "UTF8"

    #@2
    invoke-virtual {p1, v6}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    #@5
    move-result-object v2

    #@6
    .line 26
    .local v2, "encryptionKey":[B
    new-instance v4, Ljavax/crypto/spec/DESKeySpec;

    #@8
    invoke-direct {v4, v2}, Ljavax/crypto/spec/DESKeySpec;-><init>([B)V

    #@b
    .line 27
    .local v4, "myKeySpec":Ljava/security/spec/KeySpec;
    sget-object v6, Lorg/cf/obfuscated/DESCrypt;->mySecretKeyFactory:Ljavax/crypto/SecretKeyFactory;

    #@d
    invoke-virtual {v6, v4}, Ljavax/crypto/SecretKeyFactory;->generateSecret(Ljava/security/spec/KeySpec;)Ljavax/crypto/SecretKey;

    #@10
    move-result-object v3

    #@11
    .line 29
    .local v3, "key":Ljavax/crypto/SecretKey;
    sget-object v6, Lorg/cf/obfuscated/DESCrypt;->cipher:Ljavax/crypto/Cipher;

    #@13
    const/4 v7, 0x1

    #@14
    invoke-virtual {v6, v7, v3}, Ljavax/crypto/Cipher;->init(ILjava/security/Key;)V

    #@17
    .line 30
    const-string v6, "UTF8"

    #@19
    invoke-virtual {p0, v6}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    #@1c
    move-result-object v5

    #@1d
    .line 31
    .local v5, "stringBytes":[B
    sget-object v6, Lorg/cf/obfuscated/DESCrypt;->cipher:Ljavax/crypto/Cipher;

    #@1f
    invoke-virtual {v6, v5}, Ljavax/crypto/Cipher;->doFinal([B)[B
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@22
    move-result-object v1

    #@23
    .line 36
    .end local v2    # "encryptionKey":[B
    .end local v3    # "key":Ljavax/crypto/SecretKey;
    .end local v4    # "myKeySpec":Ljava/security/spec/KeySpec;
    .end local v5    # "stringBytes":[B
    :goto_0
    return-object v1

    #@24
    .line 34
    :catch_0
    move-exception v0

    #@25
    .line 35
    .local v0, "e":Ljava/lang/Exception;
    invoke-virtual {v0}, Ljava/lang/Exception;->printStackTrace()V

    #@28
    .line 36
    const/4 v6, 0x0

    #@29
    new-array v1, v6, [B

    #@2b
    goto :goto_0
.end method

.method public static main([Ljava/lang/String;)V
    .locals 0
    .param p0, "argv"    # [Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    #@0
    .prologue
    .line 61
    return-void
.end method
