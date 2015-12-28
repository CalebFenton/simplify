.class Landroid/support/v7/internal/app/NavItemSelectedListener;
.super Ljava/lang/Object;
.source "NavItemSelectedListener.java"

# interfaces
.implements Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemSelectedListener;


# instance fields
.field private final mListener:Landroid/support/v7/app/ActionBar$OnNavigationListener;


# direct methods
.method public constructor <init>(Landroid/support/v7/app/ActionBar$OnNavigationListener;)V
    .locals 0
    .param p1, "listener"    # Landroid/support/v7/app/ActionBar$OnNavigationListener;

    #@0
    .prologue
    .line 33
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 34
    iput-object p1, p0, Landroid/support/v7/internal/app/NavItemSelectedListener;->mListener:Landroid/support/v7/app/ActionBar$OnNavigationListener;

    #@5
    .line 35
    return-void
.end method


# virtual methods
.method public onItemSelected(Landroid/support/v7/internal/widget/AdapterViewCompat;Landroid/view/View;IJ)V
    .locals 2
    .param p2, "view"    # Landroid/view/View;
    .param p3, "position"    # I
    .param p4, "id"    # J
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/support/v7/internal/widget/AdapterViewCompat",
            "<*>;",
            "Landroid/view/View;",
            "IJ)V"
        }
    .end annotation

    #@0
    .prologue
    .line 39
    .local p1, "parent":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<*>;"
    iget-object v0, p0, Landroid/support/v7/internal/app/NavItemSelectedListener;->mListener:Landroid/support/v7/app/ActionBar$OnNavigationListener;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 40
    iget-object v0, p0, Landroid/support/v7/internal/app/NavItemSelectedListener;->mListener:Landroid/support/v7/app/ActionBar$OnNavigationListener;

    #@6
    invoke-interface {v0, p3, p4, p5}, Landroid/support/v7/app/ActionBar$OnNavigationListener;->onNavigationItemSelected(IJ)Z

    #@9
    .line 42
    :cond_0
    return-void
.end method

.method public onNothingSelected(Landroid/support/v7/internal/widget/AdapterViewCompat;)V
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/support/v7/internal/widget/AdapterViewCompat",
            "<*>;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 47
    .local p1, "parent":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<*>;"
    return-void
.end method
