.class final Landroid/support/v4/speech/tts/TextToSpeechICSMR1$1;
.super Landroid/speech/tts/UtteranceProgressListener;
.source "TextToSpeechICSMR1.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v4/speech/tts/TextToSpeechICSMR1;->setUtteranceProgressListener(Landroid/speech/tts/TextToSpeech;Landroid/support/v4/speech/tts/TextToSpeechICSMR1$UtteranceProgressListenerICSMR1;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = null
.end annotation


# instance fields
.field final synthetic val$listener:Landroid/support/v4/speech/tts/TextToSpeechICSMR1$UtteranceProgressListenerICSMR1;


# direct methods
.method constructor <init>(Landroid/support/v4/speech/tts/TextToSpeechICSMR1$UtteranceProgressListenerICSMR1;)V
    .locals 0

    #@0
    .prologue
    .line 47
    iput-object p1, p0, Landroid/support/v4/speech/tts/TextToSpeechICSMR1$1;->val$listener:Landroid/support/v4/speech/tts/TextToSpeechICSMR1$UtteranceProgressListenerICSMR1;

    #@2
    invoke-direct {p0}, Landroid/speech/tts/UtteranceProgressListener;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public onDone(Ljava/lang/String;)V
    .locals 1
    .param p1, "utteranceId"    # Ljava/lang/String;

    #@0
    .prologue
    .line 60
    iget-object v0, p0, Landroid/support/v4/speech/tts/TextToSpeechICSMR1$1;->val$listener:Landroid/support/v4/speech/tts/TextToSpeechICSMR1$UtteranceProgressListenerICSMR1;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v4/speech/tts/TextToSpeechICSMR1$UtteranceProgressListenerICSMR1;->onDone(Ljava/lang/String;)V

    #@5
    .line 61
    return-void
.end method

.method public onError(Ljava/lang/String;)V
    .locals 1
    .param p1, "utteranceId"    # Ljava/lang/String;

    #@0
    .prologue
    .line 55
    iget-object v0, p0, Landroid/support/v4/speech/tts/TextToSpeechICSMR1$1;->val$listener:Landroid/support/v4/speech/tts/TextToSpeechICSMR1$UtteranceProgressListenerICSMR1;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v4/speech/tts/TextToSpeechICSMR1$UtteranceProgressListenerICSMR1;->onError(Ljava/lang/String;)V

    #@5
    .line 56
    return-void
.end method

.method public onStart(Ljava/lang/String;)V
    .locals 1
    .param p1, "utteranceId"    # Ljava/lang/String;

    #@0
    .prologue
    .line 50
    iget-object v0, p0, Landroid/support/v4/speech/tts/TextToSpeechICSMR1$1;->val$listener:Landroid/support/v4/speech/tts/TextToSpeechICSMR1$UtteranceProgressListenerICSMR1;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v4/speech/tts/TextToSpeechICSMR1$UtteranceProgressListenerICSMR1;->onStart(Ljava/lang/String;)V

    #@5
    .line 51
    return-void
.end method
