.class final Landroid/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask;
.super Landroid/os/AsyncTask;
.source "ActivityChooserModel.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/widget/ActivityChooserModel;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x12
    name = "PersistHistoryAsyncTask"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Landroid/os/AsyncTask",
        "<",
        "Ljava/lang/Object;",
        "Ljava/lang/Void;",
        "Ljava/lang/Void;",
        ">;"
    }
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;


# direct methods
.method private constructor <init>(Landroid/support/v7/internal/widget/ActivityChooserModel;)V
    .locals 0

    #@0
    .prologue
    .line 1037
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask;->this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@2
    invoke-direct {p0}, Landroid/os/AsyncTask;-><init>()V

    #@5
    return-void
.end method

.method synthetic constructor <init>(Landroid/support/v7/internal/widget/ActivityChooserModel;Landroid/support/v7/internal/widget/ActivityChooserModel$1;)V
    .locals 0
    .param p1, "x0"    # Landroid/support/v7/internal/widget/ActivityChooserModel;
    .param p2, "x1"    # Landroid/support/v7/internal/widget/ActivityChooserModel$1;

    #@0
    .prologue
    .line 1037
    invoke-direct {p0, p1}, Landroid/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask;-><init>(Landroid/support/v7/internal/widget/ActivityChooserModel;)V

    #@3
    return-void
.end method


# virtual methods
.method public bridge synthetic doInBackground([Ljava/lang/Object;)Ljava/lang/Object;
    .locals 1
    .param p1, "x0"    # [Ljava/lang/Object;

    #@0
    .prologue
    .line 1037
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask;->doInBackground([Ljava/lang/Object;)Ljava/lang/Void;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public varargs doInBackground([Ljava/lang/Object;)Ljava/lang/Void;
    .locals 18
    .param p1, "args"    # [Ljava/lang/Object;

    #@0
    .prologue
    .line 1042
    const/4 v13, 0x0

    #@1
    aget-object v4, p1, v13

    #@3
    check-cast v4, Ljava/util/List;

    #@5
    .line 1043
    .local v4, "historicalRecords":Ljava/util/List;, "Ljava/util/List<Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;>;"
    const/4 v13, 0x1

    #@6
    aget-object v5, p1, v13

    #@8
    check-cast v5, Ljava/lang/String;

    #@a
    .line 1045
    .local v5, "hostoryFileName":Ljava/lang/String;
    const/4 v3, 0x0

    #@b
    .line 1048
    .local v3, "fos":Ljava/io/FileOutputStream;
    :try_start_0
    move-object/from16 v0, p0

    #@d
    iget-object v13, v0, Landroid/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask;->this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@f
    # getter for: Landroid/support/v7/internal/widget/ActivityChooserModel;->mContext:Landroid/content/Context;
    invoke-static {v13}, Landroid/support/v7/internal/widget/ActivityChooserModel;->access$200(Landroid/support/v7/internal/widget/ActivityChooserModel;)Landroid/content/Context;

    #@12
    move-result-object v13

    #@13
    const/4 v14, 0x0

    #@14
    invoke-virtual {v13, v5, v14}, Landroid/content/Context;->openFileOutput(Ljava/lang/String;I)Ljava/io/FileOutputStream;
    :try_end_0
    .catch Ljava/io/FileNotFoundException; {:try_start_0 .. :try_end_0} :catch_0

    #@17
    move-result-object v3

    #@18
    .line 1054
    invoke-static {}, Landroid/util/Xml;->newSerializer()Lorg/xmlpull/v1/XmlSerializer;

    #@1b
    move-result-object v12

    #@1c
    .line 1057
    .local v12, "serializer":Lorg/xmlpull/v1/XmlSerializer;
    const/4 v13, 0x0

    #@1d
    :try_start_1
    invoke-interface {v12, v3, v13}, Lorg/xmlpull/v1/XmlSerializer;->setOutput(Ljava/io/OutputStream;Ljava/lang/String;)V

    #@20
    .line 1058
    const-string v13, "UTF-8"

    #@22
    const/4 v14, 0x1

    #@23
    invoke-static {v14}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    #@26
    move-result-object v14

    #@27
    invoke-interface {v12, v13, v14}, Lorg/xmlpull/v1/XmlSerializer;->startDocument(Ljava/lang/String;Ljava/lang/Boolean;)V

    #@2a
    .line 1059
    const/4 v13, 0x0

    #@2b
    const-string v14, "historical-records"

    #@2d
    invoke-interface {v12, v13, v14}, Lorg/xmlpull/v1/XmlSerializer;->startTag(Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;

    #@30
    .line 1061
    invoke-interface {v4}, Ljava/util/List;->size()I

    #@33
    move-result v11

    #@34
    .line 1062
    .local v11, "recordCount":I
    const/4 v6, 0x0

    #@35
    .local v6, "i":I
    :goto_0
    if-ge v6, v11, :cond_0

    #@37
    .line 1063
    const/4 v13, 0x0

    #@38
    invoke-interface {v4, v13}, Ljava/util/List;->remove(I)Ljava/lang/Object;

    #@3b
    move-result-object v10

    #@3c
    check-cast v10, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;

    #@3e
    .line 1064
    .local v10, "record":Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;
    const/4 v13, 0x0

    #@3f
    const-string v14, "historical-record"

    #@41
    invoke-interface {v12, v13, v14}, Lorg/xmlpull/v1/XmlSerializer;->startTag(Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;

    #@44
    .line 1065
    const/4 v13, 0x0

    #@45
    const-string v14, "activity"

    #@47
    iget-object v15, v10, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->activity:Landroid/content/ComponentName;

    #@49
    invoke-virtual {v15}, Landroid/content/ComponentName;->flattenToString()Ljava/lang/String;

    #@4c
    move-result-object v15

    #@4d
    invoke-interface {v12, v13, v14, v15}, Lorg/xmlpull/v1/XmlSerializer;->attribute(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;

    #@50
    .line 1067
    const/4 v13, 0x0

    #@51
    const-string v14, "time"

    #@53
    iget-wide v0, v10, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->time:J

    #@55
    move-wide/from16 v16, v0

    #@57
    invoke-static/range {v16 .. v17}, Ljava/lang/String;->valueOf(J)Ljava/lang/String;

    #@5a
    move-result-object v15

    #@5b
    invoke-interface {v12, v13, v14, v15}, Lorg/xmlpull/v1/XmlSerializer;->attribute(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;

    #@5e
    .line 1068
    const/4 v13, 0x0

    #@5f
    const-string v14, "weight"

    #@61
    iget v15, v10, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->weight:F

    #@63
    invoke-static {v15}, Ljava/lang/String;->valueOf(F)Ljava/lang/String;

    #@66
    move-result-object v15

    #@67
    invoke-interface {v12, v13, v14, v15}, Lorg/xmlpull/v1/XmlSerializer;->attribute(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;

    #@6a
    .line 1069
    const/4 v13, 0x0

    #@6b
    const-string v14, "historical-record"

    #@6d
    invoke-interface {v12, v13, v14}, Lorg/xmlpull/v1/XmlSerializer;->endTag(Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
    :try_end_1
    .catch Ljava/lang/IllegalArgumentException; {:try_start_1 .. :try_end_1} :catch_1
    .catch Ljava/lang/IllegalStateException; {:try_start_1 .. :try_end_1} :catch_3
    .catch Ljava/io/IOException; {:try_start_1 .. :try_end_1} :catch_5
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    #@70
    .line 1062
    add-int/lit8 v6, v6, 0x1

    #@72
    goto :goto_0

    #@73
    .line 1049
    .end local v6    # "i":I
    .end local v10    # "record":Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;
    .end local v11    # "recordCount":I
    .end local v12    # "serializer":Lorg/xmlpull/v1/XmlSerializer;
    :catch_0
    move-exception v2

    #@74
    .line 1050
    .local v2, "fnfe":Ljava/io/FileNotFoundException;
    # getter for: Landroid/support/v7/internal/widget/ActivityChooserModel;->LOG_TAG:Ljava/lang/String;
    invoke-static {}, Landroid/support/v7/internal/widget/ActivityChooserModel;->access$300()Ljava/lang/String;

    #@77
    move-result-object v13

    #@78
    new-instance v14, Ljava/lang/StringBuilder;

    #@7a
    invoke-direct {v14}, Ljava/lang/StringBuilder;-><init>()V

    #@7d
    const-string v15, "Error writing historical recrod file: "

    #@7f
    invoke-virtual {v14, v15}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@82
    move-result-object v14

    #@83
    invoke-virtual {v14, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@86
    move-result-object v14

    #@87
    invoke-virtual {v14}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@8a
    move-result-object v14

    #@8b
    invoke-static {v13, v14, v2}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@8e
    .line 1051
    const/4 v13, 0x0

    #@8f
    .line 1097
    .end local v2    # "fnfe":Ljava/io/FileNotFoundException;
    :goto_1
    return-object v13

    #@90
    .line 1075
    .restart local v6    # "i":I
    .restart local v11    # "recordCount":I
    .restart local v12    # "serializer":Lorg/xmlpull/v1/XmlSerializer;
    :cond_0
    const/4 v13, 0x0

    #@91
    :try_start_2
    const-string v14, "historical-records"

    #@93
    invoke-interface {v12, v13, v14}, Lorg/xmlpull/v1/XmlSerializer;->endTag(Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;

    #@96
    .line 1076
    invoke-interface {v12}, Lorg/xmlpull/v1/XmlSerializer;->endDocument()V
    :try_end_2
    .catch Ljava/lang/IllegalArgumentException; {:try_start_2 .. :try_end_2} :catch_1
    .catch Ljava/lang/IllegalStateException; {:try_start_2 .. :try_end_2} :catch_3
    .catch Ljava/io/IOException; {:try_start_2 .. :try_end_2} :catch_5
    .catchall {:try_start_2 .. :try_end_2} :catchall_0

    #@99
    .line 1088
    move-object/from16 v0, p0

    #@9b
    iget-object v13, v0, Landroid/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask;->this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@9d
    const/4 v14, 0x1

    #@9e
    # setter for: Landroid/support/v7/internal/widget/ActivityChooserModel;->mCanReadHistoricalData:Z
    invoke-static {v13, v14}, Landroid/support/v7/internal/widget/ActivityChooserModel;->access$502(Landroid/support/v7/internal/widget/ActivityChooserModel;Z)Z

    #@a1
    .line 1089
    if-eqz v3, :cond_1

    #@a3
    .line 1091
    :try_start_3
    invoke-virtual {v3}, Ljava/io/FileOutputStream;->close()V
    :try_end_3
    .catch Ljava/io/IOException; {:try_start_3 .. :try_end_3} :catch_7

    #@a6
    .line 1097
    .end local v6    # "i":I
    .end local v11    # "recordCount":I
    :cond_1
    :goto_2
    const/4 v13, 0x0

    #@a7
    goto :goto_1

    #@a8
    .line 1081
    :catch_1
    move-exception v7

    #@a9
    .line 1082
    .local v7, "iae":Ljava/lang/IllegalArgumentException;
    :try_start_4
    # getter for: Landroid/support/v7/internal/widget/ActivityChooserModel;->LOG_TAG:Ljava/lang/String;
    invoke-static {}, Landroid/support/v7/internal/widget/ActivityChooserModel;->access$300()Ljava/lang/String;

    #@ac
    move-result-object v13

    #@ad
    new-instance v14, Ljava/lang/StringBuilder;

    #@af
    invoke-direct {v14}, Ljava/lang/StringBuilder;-><init>()V

    #@b2
    const-string v15, "Error writing historical recrod file: "

    #@b4
    invoke-virtual {v14, v15}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@b7
    move-result-object v14

    #@b8
    move-object/from16 v0, p0

    #@ba
    iget-object v15, v0, Landroid/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask;->this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@bc
    # getter for: Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoryFileName:Ljava/lang/String;
    invoke-static {v15}, Landroid/support/v7/internal/widget/ActivityChooserModel;->access$400(Landroid/support/v7/internal/widget/ActivityChooserModel;)Ljava/lang/String;

    #@bf
    move-result-object v15

    #@c0
    invoke-virtual {v14, v15}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@c3
    move-result-object v14

    #@c4
    invoke-virtual {v14}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@c7
    move-result-object v14

    #@c8
    invoke-static {v13, v14, v7}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    :try_end_4
    .catchall {:try_start_4 .. :try_end_4} :catchall_0

    #@cb
    .line 1088
    move-object/from16 v0, p0

    #@cd
    iget-object v13, v0, Landroid/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask;->this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@cf
    const/4 v14, 0x1

    #@d0
    # setter for: Landroid/support/v7/internal/widget/ActivityChooserModel;->mCanReadHistoricalData:Z
    invoke-static {v13, v14}, Landroid/support/v7/internal/widget/ActivityChooserModel;->access$502(Landroid/support/v7/internal/widget/ActivityChooserModel;Z)Z

    #@d3
    .line 1089
    if-eqz v3, :cond_1

    #@d5
    .line 1091
    :try_start_5
    invoke-virtual {v3}, Ljava/io/FileOutputStream;->close()V
    :try_end_5
    .catch Ljava/io/IOException; {:try_start_5 .. :try_end_5} :catch_2

    #@d8
    goto :goto_2

    #@d9
    .line 1092
    :catch_2
    move-exception v13

    #@da
    goto :goto_2

    #@db
    .line 1083
    .end local v7    # "iae":Ljava/lang/IllegalArgumentException;
    :catch_3
    move-exception v9

    #@dc
    .line 1084
    .local v9, "ise":Ljava/lang/IllegalStateException;
    :try_start_6
    # getter for: Landroid/support/v7/internal/widget/ActivityChooserModel;->LOG_TAG:Ljava/lang/String;
    invoke-static {}, Landroid/support/v7/internal/widget/ActivityChooserModel;->access$300()Ljava/lang/String;

    #@df
    move-result-object v13

    #@e0
    new-instance v14, Ljava/lang/StringBuilder;

    #@e2
    invoke-direct {v14}, Ljava/lang/StringBuilder;-><init>()V

    #@e5
    const-string v15, "Error writing historical recrod file: "

    #@e7
    invoke-virtual {v14, v15}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@ea
    move-result-object v14

    #@eb
    move-object/from16 v0, p0

    #@ed
    iget-object v15, v0, Landroid/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask;->this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@ef
    # getter for: Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoryFileName:Ljava/lang/String;
    invoke-static {v15}, Landroid/support/v7/internal/widget/ActivityChooserModel;->access$400(Landroid/support/v7/internal/widget/ActivityChooserModel;)Ljava/lang/String;

    #@f2
    move-result-object v15

    #@f3
    invoke-virtual {v14, v15}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@f6
    move-result-object v14

    #@f7
    invoke-virtual {v14}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@fa
    move-result-object v14

    #@fb
    invoke-static {v13, v14, v9}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    :try_end_6
    .catchall {:try_start_6 .. :try_end_6} :catchall_0

    #@fe
    .line 1088
    move-object/from16 v0, p0

    #@100
    iget-object v13, v0, Landroid/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask;->this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@102
    const/4 v14, 0x1

    #@103
    # setter for: Landroid/support/v7/internal/widget/ActivityChooserModel;->mCanReadHistoricalData:Z
    invoke-static {v13, v14}, Landroid/support/v7/internal/widget/ActivityChooserModel;->access$502(Landroid/support/v7/internal/widget/ActivityChooserModel;Z)Z

    #@106
    .line 1089
    if-eqz v3, :cond_1

    #@108
    .line 1091
    :try_start_7
    invoke-virtual {v3}, Ljava/io/FileOutputStream;->close()V
    :try_end_7
    .catch Ljava/io/IOException; {:try_start_7 .. :try_end_7} :catch_4

    #@10b
    goto :goto_2

    #@10c
    .line 1092
    :catch_4
    move-exception v13

    #@10d
    goto :goto_2

    #@10e
    .line 1085
    .end local v9    # "ise":Ljava/lang/IllegalStateException;
    :catch_5
    move-exception v8

    #@10f
    .line 1086
    .local v8, "ioe":Ljava/io/IOException;
    :try_start_8
    # getter for: Landroid/support/v7/internal/widget/ActivityChooserModel;->LOG_TAG:Ljava/lang/String;
    invoke-static {}, Landroid/support/v7/internal/widget/ActivityChooserModel;->access$300()Ljava/lang/String;

    #@112
    move-result-object v13

    #@113
    new-instance v14, Ljava/lang/StringBuilder;

    #@115
    invoke-direct {v14}, Ljava/lang/StringBuilder;-><init>()V

    #@118
    const-string v15, "Error writing historical recrod file: "

    #@11a
    invoke-virtual {v14, v15}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@11d
    move-result-object v14

    #@11e
    move-object/from16 v0, p0

    #@120
    iget-object v15, v0, Landroid/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask;->this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@122
    # getter for: Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoryFileName:Ljava/lang/String;
    invoke-static {v15}, Landroid/support/v7/internal/widget/ActivityChooserModel;->access$400(Landroid/support/v7/internal/widget/ActivityChooserModel;)Ljava/lang/String;

    #@125
    move-result-object v15

    #@126
    invoke-virtual {v14, v15}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@129
    move-result-object v14

    #@12a
    invoke-virtual {v14}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@12d
    move-result-object v14

    #@12e
    invoke-static {v13, v14, v8}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    :try_end_8
    .catchall {:try_start_8 .. :try_end_8} :catchall_0

    #@131
    .line 1088
    move-object/from16 v0, p0

    #@133
    iget-object v13, v0, Landroid/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask;->this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@135
    const/4 v14, 0x1

    #@136
    # setter for: Landroid/support/v7/internal/widget/ActivityChooserModel;->mCanReadHistoricalData:Z
    invoke-static {v13, v14}, Landroid/support/v7/internal/widget/ActivityChooserModel;->access$502(Landroid/support/v7/internal/widget/ActivityChooserModel;Z)Z

    #@139
    .line 1089
    if-eqz v3, :cond_1

    #@13b
    .line 1091
    :try_start_9
    invoke-virtual {v3}, Ljava/io/FileOutputStream;->close()V
    :try_end_9
    .catch Ljava/io/IOException; {:try_start_9 .. :try_end_9} :catch_6

    #@13e
    goto/16 :goto_2

    #@140
    .line 1092
    :catch_6
    move-exception v13

    #@141
    goto/16 :goto_2

    #@143
    .line 1088
    .end local v8    # "ioe":Ljava/io/IOException;
    :catchall_0
    move-exception v13

    #@144
    move-object/from16 v0, p0

    #@146
    iget-object v14, v0, Landroid/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask;->this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@148
    const/4 v15, 0x1

    #@149
    # setter for: Landroid/support/v7/internal/widget/ActivityChooserModel;->mCanReadHistoricalData:Z
    invoke-static {v14, v15}, Landroid/support/v7/internal/widget/ActivityChooserModel;->access$502(Landroid/support/v7/internal/widget/ActivityChooserModel;Z)Z

    #@14c
    .line 1089
    if-eqz v3, :cond_2

    #@14e
    .line 1091
    :try_start_a
    invoke-virtual {v3}, Ljava/io/FileOutputStream;->close()V
    :try_end_a
    .catch Ljava/io/IOException; {:try_start_a .. :try_end_a} :catch_8

    #@151
    .line 1094
    :cond_2
    :goto_3
    throw v13

    #@152
    .line 1092
    .restart local v6    # "i":I
    .restart local v11    # "recordCount":I
    :catch_7
    move-exception v13

    #@153
    goto/16 :goto_2

    #@155
    .end local v6    # "i":I
    .end local v11    # "recordCount":I
    :catch_8
    move-exception v14

    #@156
    goto :goto_3
.end method
