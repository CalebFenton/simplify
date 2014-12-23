.class public Lorg/cf/obfuscated/DESCrypt;
.super Ljava/lang/Object;
.source "DESCrypt.java"


# static fields
.field private static cipher:Ljavax/crypto/Cipher;

.field private static mySecretKeyFactory:Ljavax/crypto/SecretKeyFactory;


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .prologue
    const/4 v0, 0x0

    .line 11
    sput-object v0, Lorg/cf/obfuscated/DESCrypt;->cipher:Ljavax/crypto/Cipher;

    .line 12
    sput-object v0, Lorg/cf/obfuscated/DESCrypt;->mySecretKeyFactory:Ljavax/crypto/SecretKeyFactory;

    .line 16
    :try_start_5
    const-string v0, "DES"

    invoke-static {v0}, Ljavax/crypto/Cipher;->getInstance(Ljava/lang/String;)Ljavax/crypto/Cipher;

    move-result-object v0

    sput-object v0, Lorg/cf/obfuscated/DESCrypt;->cipher:Ljavax/crypto/Cipher;

    .line 17
    const-string v0, "DES"

    invoke-static {v0}, Ljavax/crypto/SecretKeyFactory;->getInstance(Ljava/lang/String;)Ljavax/crypto/SecretKeyFactory;

    move-result-object v0

    sput-object v0, Lorg/cf/obfuscated/DESCrypt;->mySecretKeyFactory:Ljavax/crypto/SecretKeyFactory;
    :try_end_15
    .catch Ljava/lang/Exception; {:try_start_5 .. :try_end_15} :catch_16

    .line 21
    :goto_15
    return-void

    .line 18
    :catch_16
    move-exception v0

    goto :goto_15
.end method

.method public constructor <init>()V
    .registers 1

    .prologue
    .line 9
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static decrypt([BLjava/lang/String;)Ljava/lang/String;
    .registers 10
    .param p0, "encrypted"    # [B
    .param p1, "keyString"    # Ljava/lang/String;

    .prologue
    .line 41
    const/4 v0, 0x0

    .line 43
    .local v0, "decrypted":Ljava/lang/String;
    :try_start_1
    const-string v6, "UTF8"

    invoke-virtual {p1, v6}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v3

    .line 44
    .local v3, "encryptionKey":[B
    new-instance v5, Ljavax/crypto/spec/DESKeySpec;

    invoke-direct {v5, v3}, Ljavax/crypto/spec/DESKeySpec;-><init>([B)V

    .line 45
    .local v5, "myKeySpec":Ljava/security/spec/KeySpec;
    sget-object v6, Lorg/cf/obfuscated/DESCrypt;->mySecretKeyFactory:Ljavax/crypto/SecretKeyFactory;

    invoke-virtual {v6, v5}, Ljavax/crypto/SecretKeyFactory;->generateSecret(Ljava/security/spec/KeySpec;)Ljavax/crypto/SecretKey;

    move-result-object v4

    .line 47
    .local v4, "key":Ljavax/crypto/SecretKey;
    sget-object v6, Lorg/cf/obfuscated/DESCrypt;->cipher:Ljavax/crypto/Cipher;

    const/4 v7, 0x2

    invoke-virtual {v6, v7, v4}, Ljavax/crypto/Cipher;->init(ILjava/security/Key;)V

    .line 48
    sget-object v6, Lorg/cf/obfuscated/DESCrypt;->cipher:Ljavax/crypto/Cipher;

    invoke-virtual {v6, p0}, Ljavax/crypto/Cipher;->doFinal([B)[B

    move-result-object v1

    .line 49
    .local v1, "decryptedBytes":[B
    new-instance v6, Ljava/lang/String;

    invoke-direct {v6, v1}, Ljava/lang/String;-><init>([B)V
    :try_end_23
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_23} :catch_24

    .line 52
    .end local v1    # "decryptedBytes":[B
    .end local v3    # "encryptionKey":[B
    .end local v4    # "key":Ljavax/crypto/SecretKey;
    .end local v5    # "myKeySpec":Ljava/security/spec/KeySpec;
    :goto_23
    return-object v6

    .line 50
    :catch_24
    move-exception v2

    .line 51
    .local v2, "e":Ljava/lang/Exception;
    invoke-virtual {v2}, Ljava/lang/Exception;->printStackTrace()V

    .line 52
    const-string v6, ""

    goto :goto_23
.end method

.method public static encrypt(Ljava/lang/String;Ljava/lang/String;)[B
    .registers 10
    .param p0, "string"    # Ljava/lang/String;
    .param p1, "keyString"    # Ljava/lang/String;

    .prologue
    .line 25
    :try_start_0
    const-string v6, "UTF8"

    invoke-virtual {p1, v6}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v2

    .line 26
    .local v2, "encryptionKey":[B
    new-instance v4, Ljavax/crypto/spec/DESKeySpec;

    invoke-direct {v4, v2}, Ljavax/crypto/spec/DESKeySpec;-><init>([B)V

    .line 27
    .local v4, "myKeySpec":Ljava/security/spec/KeySpec;
    sget-object v6, Lorg/cf/obfuscated/DESCrypt;->mySecretKeyFactory:Ljavax/crypto/SecretKeyFactory;

    invoke-virtual {v6, v4}, Ljavax/crypto/SecretKeyFactory;->generateSecret(Ljava/security/spec/KeySpec;)Ljavax/crypto/SecretKey;

    move-result-object v3

    .line 29
    .local v3, "key":Ljavax/crypto/SecretKey;
    sget-object v6, Lorg/cf/obfuscated/DESCrypt;->cipher:Ljavax/crypto/Cipher;

    const/4 v7, 0x1

    invoke-virtual {v6, v7, v3}, Ljavax/crypto/Cipher;->init(ILjava/security/Key;)V

    .line 30
    const-string v6, "UTF8"

    invoke-virtual {p0, v6}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v5

    .line 31
    .local v5, "stringBytes":[B
    sget-object v6, Lorg/cf/obfuscated/DESCrypt;->cipher:Ljavax/crypto/Cipher;

    invoke-virtual {v6, v5}, Ljavax/crypto/Cipher;->doFinal([B)[B
    :try_end_22
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_22} :catch_24

    move-result-object v1

    .line 36
    .end local v2    # "encryptionKey":[B
    .end local v3    # "key":Ljavax/crypto/SecretKey;
    .end local v4    # "myKeySpec":Ljava/security/spec/KeySpec;
    .end local v5    # "stringBytes":[B
    :goto_23
    return-object v1

    .line 34
    :catch_24
    move-exception v0

    .line 35
    .local v0, "e":Ljava/lang/Exception;
    invoke-virtual {v0}, Ljava/lang/Exception;->printStackTrace()V

    .line 36
    const/4 v6, 0x0

    new-array v1, v6, [B

    goto :goto_23
.end method

.method public static main([Ljava/lang/String;)V
    .registers 1
    .param p0, "argv"    # [Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .prologue
    .line 61
    return-void
.end method
