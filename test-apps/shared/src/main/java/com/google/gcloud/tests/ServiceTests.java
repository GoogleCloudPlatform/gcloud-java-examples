/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gcloud.tests;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.collect.ImmutableSet;
import com.google.gcloud.AuthCredentials;
import com.google.gcloud.bigquery.BaseTableInfo;
import com.google.gcloud.bigquery.BigQuery;
import com.google.gcloud.bigquery.BigQueryOptions;
import com.google.gcloud.bigquery.DatasetInfo;
import com.google.gcloud.bigquery.Field;
import com.google.gcloud.bigquery.Schema;
import com.google.gcloud.bigquery.TableId;
import com.google.gcloud.bigquery.TableInfo;
import com.google.gcloud.bigquery.testing.RemoteBigQueryHelper;
import com.google.gcloud.datastore.Datastore;
import com.google.gcloud.datastore.DatastoreOptions;
import com.google.gcloud.datastore.DateTime;
import com.google.gcloud.datastore.Entity;
import com.google.gcloud.datastore.Key;
import com.google.gcloud.datastore.KeyFactory;
import com.google.gcloud.resourcemanager.ProjectInfo;
import com.google.gcloud.resourcemanager.ResourceManager;
import com.google.gcloud.resourcemanager.ResourceManagerOptions;
import com.google.gcloud.storage.Blob;
import com.google.gcloud.storage.BlobId;
import com.google.gcloud.storage.BlobInfo;
import com.google.gcloud.storage.BucketInfo;
import com.google.gcloud.storage.Storage;
import com.google.gcloud.storage.StorageOptions;
import com.google.gcloud.storage.testing.RemoteGcsHelper;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.Set;

public class ServiceTests {

  public static final Set<String> SUPPORTED_SERVICES =
      ImmutableSet.of("bigquery", "datastore", "resourcemanager", "storage");

  private static void runBigQueryActions(BigQuery bigquery, PrintWriter pw) {
    pw.println("Testing BigQuery.");
    String datasetName = RemoteBigQueryHelper.generateDatasetName();
    bigquery.create(DatasetInfo.builder(datasetName).build());
    TableId tableId = TableId.of(datasetName, "my_table");
    BaseTableInfo info = bigquery.getTable(tableId);
    if (info == null) {
      pw.println("Creating table " + tableId);
      Field integerField = Field.of("fieldName", Field.Type.integer());
      bigquery.create(TableInfo.of(tableId, Schema.of(integerField)));
    }
    RemoteBigQueryHelper.forceDelete(bigquery, datasetName);
    pw.println("Finished BigQuery test.");
  }

  private static void runDatastoreActions(Datastore datastore, PrintWriter pw) {
    pw.println("Testing Datastore.");
    KeyFactory keyFactory = datastore.newKeyFactory().kind("Person");
    Key key = keyFactory.newKey("myid");
    Entity entity = datastore.get(key);
    if (entity == null) {
      pw.println("Creating entity");
      entity = Entity.builder(key)
          .set("name", "John Doe")
          .set("age", 30)
          .set("access_time", DateTime.now())
          .build();
      datastore.put(entity);
    } else {
      pw.println("Updating access_time for " + entity.getString("name"));
      entity = Entity.builder(entity)
          .set("access_time", DateTime.now())
          .build();
      datastore.update(entity);
    }
    pw.println("Finished Datastore test.");
  }

  private static void runResourceManagerActions(ResourceManager resourceManager, PrintWriter pw) {
    pw.println("Testing Resource Manager.");
    Iterator<ProjectInfo> projectIterator = resourceManager.list().iterateAll();
    pw.println("Projects I can view:");
    while (projectIterator.hasNext()) {
      pw.println(projectIterator.next().projectId());
    }
    pw.println("Finished Resource Manager test.");
  }

  private static void runStorageActions(Storage storage, PrintWriter pw) {
    pw.println("Testing Storage.");
    String bucketName = RemoteGcsHelper.generateBucketName();
    storage.create(BucketInfo.of(bucketName));
    BlobId blobId = BlobId.of(bucketName, "my_blob");
    Blob blob = Blob.load(storage, blobId);
    if (blob == null) {
      BlobInfo blobInfo = BlobInfo.builder(blobId).contentType("text/plain").build();
      storage.create(blobInfo, "Hello, Cloud Storage!".getBytes(UTF_8));
      pw.println("Writing a file to Storage.");
    } else {
      pw.println("Updating content for " + blobId.name());
      byte[] prevContent = blob.content();
      pw.println(new String(prevContent, UTF_8));
      WritableByteChannel channel = blob.writer();
      try {
        channel.write(ByteBuffer.wrap("Updated content".getBytes(UTF_8)));
        channel.close();
      } catch (IOException e) {
        pw.println(e.toString());
      }
    }
    pw.println("Finished Storage test.");
  }

  public static void runAction(
      String service, PrintWriter pw, String projectId, AuthCredentials credentials) {
    switch (service.toLowerCase()) {
      case "bigquery":
        runBigQueryActions(
            BigQueryOptions.builder()
                .projectId(projectId)
                .authCredentials(credentials)
                .build()
                .service(),
            pw);
        break;
      case "datastore":
        runDatastoreActions(
            DatastoreOptions.builder()
                .projectId(projectId)
                .authCredentials(credentials)
                .build()
                .service(),
            pw);
        break;
      case "resourcemanager":
        runResourceManagerActions(
            ResourceManagerOptions.builder()
                .projectId(projectId)
                .authCredentials(credentials)
                .build()
                .service(),
            pw);
        break;
      case "storage":
        runStorageActions(
            StorageOptions.builder()
                .projectId(projectId)
                .authCredentials(credentials)
                .build()
                .service(),
            pw);
        break;
      default:
        pw.println("The service argument " + service
            + " is not included in the set of supported services " + SUPPORTED_SERVICES.toString());
    }
  }
}
