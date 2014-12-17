.class public Landroid/util/Xml;
.super Ljava/lang/Object;
.source "Xml.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/util/Xml$Encoding;,
        Landroid/util/Xml$XmlSerializerFactory;
    }
.end annotation


# static fields
.field public static FEATURE_RELAXED:Ljava/lang/String;


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .prologue
    .line 47
    const-string v0, "http://xmlpull.org/v1/doc/features.html#relaxed"

    sput-object v0, Landroid/util/Xml;->FEATURE_RELAXED:Ljava/lang/String;

    return-void
.end method

.method public constructor <init>()V
    .registers 1

    .prologue
    .line 39
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static asAttributeSet(Lorg/xmlpull/v1/XmlPullParser;)Landroid/util/AttributeSet;
    .registers 2
    .param p0, "parser"    # Lorg/xmlpull/v1/XmlPullParser;

    .prologue
    .line 176
    instance-of v0, p0, Landroid/util/AttributeSet;

    if-eqz v0, :cond_7

    check-cast p0, Landroid/util/AttributeSet;

    .end local p0    # "parser":Lorg/xmlpull/v1/XmlPullParser;
    :goto_6
    return-object p0

    .restart local p0    # "parser":Lorg/xmlpull/v1/XmlPullParser;
    :cond_7
    new-instance v0, Landroid/util/XmlPullAttributes;

    invoke-direct {v0, p0}, Landroid/util/XmlPullAttributes;-><init>(Lorg/xmlpull/v1/XmlPullParser;)V

    move-object p0, v0

    goto :goto_6
.end method

.method public static findEncodingByName(Ljava/lang/String;)Landroid/util/Xml$Encoding;
    .registers 6
    .param p0, "encodingName"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/UnsupportedEncodingException;
        }
    .end annotation

    .prologue
    .line 148
    if-nez p0, :cond_5

    .line 149
    sget-object v1, Landroid/util/Xml$Encoding;->UTF_8:Landroid/util/Xml$Encoding;

    .line 154
    :cond_4
    return-object v1

    .line 152
    :cond_5
    invoke-static {}, Landroid/util/Xml$Encoding;->values()[Landroid/util/Xml$Encoding;

    move-result-object v0

    .local v0, "arr$":[Landroid/util/Xml$Encoding;
    array-length v3, v0

    .local v3, "len$":I
    const/4 v2, 0x0

    .local v2, "i$":I
    :goto_b
    if-ge v2, v3, :cond_1a

    aget-object v1, v0, v2

    .line 153
    .local v1, "encoding":Landroid/util/Xml$Encoding;
    iget-object v4, v1, Landroid/util/Xml$Encoding;->expatName:Ljava/lang/String;

    invoke-virtual {v4, p0}, Ljava/lang/String;->equalsIgnoreCase(Ljava/lang/String;)Z

    move-result v4

    if-nez v4, :cond_4

    .line 152
    add-int/lit8 v2, v2, 0x1

    goto :goto_b

    .line 156
    .end local v1    # "encoding":Landroid/util/Xml$Encoding;
    :cond_1a
    new-instance v4, Ljava/io/UnsupportedEncodingException;

    invoke-direct {v4, p0}, Ljava/io/UnsupportedEncodingException;-><init>(Ljava/lang/String;)V

    throw v4
.end method

.method public static newPullParser()Lorg/xmlpull/v1/XmlPullParser;
    .registers 4

    .prologue
    .line 92
    :try_start_0
    new-instance v1, Lorg/kxml2/io/KXmlParser;

    invoke-direct {v1}, Lorg/kxml2/io/KXmlParser;-><init>()V

    .line 93
    .local v1, "parser":Lorg/kxml2/io/KXmlParser;
    const-string v2, "http://xmlpull.org/v1/doc/features.html#process-docdecl"

    const/4 v3, 0x1

    invoke-virtual {v1, v2, v3}, Lorg/kxml2/io/KXmlParser;->setFeature(Ljava/lang/String;Z)V

    .line 94
    const-string v2, "http://xmlpull.org/v1/doc/features.html#process-namespaces"

    const/4 v3, 0x1

    invoke-virtual {v1, v2, v3}, Lorg/kxml2/io/KXmlParser;->setFeature(Ljava/lang/String;Z)V
    :try_end_11
    .catch Lorg/xmlpull/v1/XmlPullParserException; {:try_start_0 .. :try_end_11} :catch_12

    .line 95
    return-object v1

    .line 96
    :catch_12
    move-exception v0

    .line 97
    .local v0, "e":Lorg/xmlpull/v1/XmlPullParserException;
    new-instance v2, Ljava/lang/AssertionError;

    invoke-direct {v2}, Ljava/lang/AssertionError;-><init>()V

    throw v2
.end method

.method public static newSerializer()Lorg/xmlpull/v1/XmlSerializer;
    .registers 2

    .prologue
    .line 106
    :try_start_0
    sget-object v1, Landroid/util/Xml$XmlSerializerFactory;->instance:Lorg/xmlpull/v1/XmlPullParserFactory;

    invoke-virtual {v1}, Lorg/xmlpull/v1/XmlPullParserFactory;->newSerializer()Lorg/xmlpull/v1/XmlSerializer;
    :try_end_5
    .catch Lorg/xmlpull/v1/XmlPullParserException; {:try_start_0 .. :try_end_5} :catch_7

    move-result-object v1

    return-object v1

    .line 107
    :catch_7
    move-exception v0

    .line 108
    .local v0, "e":Lorg/xmlpull/v1/XmlPullParserException;
    new-instance v1, Ljava/lang/AssertionError;

    invoke-direct {v1, v0}, Ljava/lang/AssertionError;-><init>(Ljava/lang/Object;)V

    throw v1
.end method

.method public static parse(Ljava/io/InputStream;Landroid/util/Xml$Encoding;Lorg/xml/sax/ContentHandler;)V
    .registers 6
    .param p0, "in"    # Ljava/io/InputStream;
    .param p1, "encoding"    # Landroid/util/Xml$Encoding;
    .param p2, "contentHandler"    # Lorg/xml/sax/ContentHandler;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;,
            Lorg/xml/sax/SAXException;
        }
    .end annotation

    .prologue
    .line 80
    new-instance v0, Lorg/apache/harmony/xml/ExpatReader;

    invoke-direct {v0}, Lorg/apache/harmony/xml/ExpatReader;-><init>()V

    .line 81
    .local v0, "reader":Lorg/xml/sax/XMLReader;
    invoke-interface {v0, p2}, Lorg/xml/sax/XMLReader;->setContentHandler(Lorg/xml/sax/ContentHandler;)V

    .line 82
    new-instance v1, Lorg/xml/sax/InputSource;

    invoke-direct {v1, p0}, Lorg/xml/sax/InputSource;-><init>(Ljava/io/InputStream;)V

    .line 83
    .local v1, "source":Lorg/xml/sax/InputSource;
    iget-object v2, p1, Landroid/util/Xml$Encoding;->expatName:Ljava/lang/String;

    invoke-virtual {v1, v2}, Lorg/xml/sax/InputSource;->setEncoding(Ljava/lang/String;)V

    .line 84
    invoke-interface {v0, v1}, Lorg/xml/sax/XMLReader;->parse(Lorg/xml/sax/InputSource;)V

    .line 85
    return-void
.end method

.method public static parse(Ljava/io/Reader;Lorg/xml/sax/ContentHandler;)V
    .registers 4
    .param p0, "in"    # Ljava/io/Reader;
    .param p1, "contentHandler"    # Lorg/xml/sax/ContentHandler;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;,
            Lorg/xml/sax/SAXException;
        }
    .end annotation

    .prologue
    .line 69
    new-instance v0, Lorg/apache/harmony/xml/ExpatReader;

    invoke-direct {v0}, Lorg/apache/harmony/xml/ExpatReader;-><init>()V

    .line 70
    .local v0, "reader":Lorg/xml/sax/XMLReader;
    invoke-interface {v0, p1}, Lorg/xml/sax/XMLReader;->setContentHandler(Lorg/xml/sax/ContentHandler;)V

    .line 71
    new-instance v1, Lorg/xml/sax/InputSource;

    invoke-direct {v1, p0}, Lorg/xml/sax/InputSource;-><init>(Ljava/io/Reader;)V

    invoke-interface {v0, v1}, Lorg/xml/sax/XMLReader;->parse(Lorg/xml/sax/InputSource;)V

    .line 72
    return-void
.end method

.method public static parse(Ljava/lang/String;Lorg/xml/sax/ContentHandler;)V
    .registers 6
    .param p0, "xml"    # Ljava/lang/String;
    .param p1, "contentHandler"    # Lorg/xml/sax/ContentHandler;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lorg/xml/sax/SAXException;
        }
    .end annotation

    .prologue
    .line 55
    :try_start_0
    new-instance v1, Lorg/apache/harmony/xml/ExpatReader;

    invoke-direct {v1}, Lorg/apache/harmony/xml/ExpatReader;-><init>()V

    .line 56
    .local v1, "reader":Lorg/xml/sax/XMLReader;
    invoke-interface {v1, p1}, Lorg/xml/sax/XMLReader;->setContentHandler(Lorg/xml/sax/ContentHandler;)V

    .line 57
    new-instance v2, Lorg/xml/sax/InputSource;

    new-instance v3, Ljava/io/StringReader;

    invoke-direct {v3, p0}, Ljava/io/StringReader;-><init>(Ljava/lang/String;)V

    invoke-direct {v2, v3}, Lorg/xml/sax/InputSource;-><init>(Ljava/io/Reader;)V

    invoke-interface {v1, v2}, Lorg/xml/sax/XMLReader;->parse(Lorg/xml/sax/InputSource;)V
    :try_end_15
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_15} :catch_16

    .line 61
    return-void

    .line 58
    .end local v1    # "reader":Lorg/xml/sax/XMLReader;
    :catch_16
    move-exception v0

    .line 59
    .local v0, "e":Ljava/io/IOException;
    new-instance v2, Ljava/lang/AssertionError;

    invoke-direct {v2, v0}, Ljava/lang/AssertionError;-><init>(Ljava/lang/Object;)V

    throw v2
.end method
