.class public Lorg/cf/obfuscated/MainActivity;
.super Landroid/support/v7/app/ActionBarActivity;
.source "MainActivity.java"


# direct methods
.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 9
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method protected onCreate(Landroid/os/Bundle;)V
    .locals 1
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 13
    invoke-super {p0, p1}, Landroid/support/v7/app/ActionBarActivity;->onCreate(Landroid/os/Bundle;)V

    #@3
    .line 14
    const v0, 0x7f030017

    #@6
    invoke-virtual {p0, v0}, Lorg/cf/obfuscated/MainActivity;->setContentView(I)V

    #@9
    .line 15
    return-void
.end method

.method public onCreateOptionsMenu(Landroid/view/Menu;)Z
    .locals 2
    .param p1, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 21
    invoke-virtual {p0}, Lorg/cf/obfuscated/MainActivity;->getMenuInflater()Landroid/view/MenuInflater;

    #@3
    move-result-object v0

    #@4
    const/high16 v1, 0x7f0c0000

    #@6
    invoke-virtual {v0, v1, p1}, Landroid/view/MenuInflater;->inflate(ILandroid/view/Menu;)V

    #@9
    .line 22
    const/4 v0, 0x1

    #@a
    return v0
.end method

.method public onOptionsItemSelected(Landroid/view/MenuItem;)Z
    .locals 2
    .param p1, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 30
    invoke-interface {p1}, Landroid/view/MenuItem;->getItemId()I

    #@3
    move-result v0

    #@4
    .line 31
    .local v0, "id":I
    const v1, 0x7f08003f

    #@7
    if-ne v0, v1, :cond_0

    #@9
    .line 32
    const/4 v1, 0x1

    #@a
    .line 34
    :goto_0
    return v1

    #@b
    :cond_0
    invoke-super {p0, p1}, Landroid/support/v7/app/ActionBarActivity;->onOptionsItemSelected(Landroid/view/MenuItem;)Z

    #@e
    move-result v1

    #@f
    goto :goto_0
.end method
