# Bodyless declaration of the host class DirectHostCallsFixture, so its methods resolve in the loaded set and
# execute through the reflection backend.
.class public final Lorg/cf/smalivm/core/DirectHostCallsFixture;
.super Ljava/lang/Object;

.method public static native recordReceived(Ljava/lang/Object;)V
.end method

.method public static native ignore(Ljava/lang/Object;)V
.end method

.method public static native detachChild(Ljava/lang/Object;)Ljava/lang/Object;
.end method

.method public static native detachHolderChild(Ljava/lang/Object;)Ljava/lang/Object;
.end method

.method public static native throwHolderChild(Ljava/lang/Object;)V
.end method

.method public static native holderChild(Ljava/lang/Object;)Ljava/lang/Object;
.end method

.method public static native appendChanged(Ljava/lang/StringBuilder;)V
.end method

.method public static native returnStored()Ljava/lang/Object;
.end method

.method public static native firstElement(Ljava/lang/Object;)Ljava/lang/Object;
.end method

.method public static native newBuilder()Ljava/lang/Object;
.end method

.method public static native appendThenThrow(Ljava/lang/StringBuilder;)V
.end method

.method public static native throwAndRecord(Ljava/lang/Throwable;)V
.end method

.method public static native setFirstThenThrow([I)V
.end method

.method public static native newIntArray()Ljava/lang/Object;
.end method

.method public static native storeNewIntArray([Ljava/lang/Object;)V
.end method

.method public static native boxedZero()Ljava/lang/Object;
.end method
