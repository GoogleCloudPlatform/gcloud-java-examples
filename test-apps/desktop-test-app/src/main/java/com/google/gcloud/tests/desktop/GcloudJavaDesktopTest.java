package com.google.gcloud.tests.desktop;

import static java.nio.charset.StandardCharsets.UTF_8;

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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;

public class GcloudJavaDesktopTest {

  public static void main(String[] args) throws FileNotFoundException, IOException {
    runBigQueryActions(BigQueryOptions.defaultInstance().service());
    runDatastoreActions(
        DatastoreOptions.builder()
            .authCredentials(AuthCredentials.createForJson(
                new FileInputStream("my-service-account-credentials.json")))
            .projectId("your-app-id")
            .build()
            .service());
    runResourceManagerActions(ResourceManagerOptions.defaultInstance().service());
    runStorageActions(StorageOptions.defaultInstance().service());
  }

  private static void runBigQueryActions(BigQuery bigquery) {
    System.out.println("Testing BigQuery.");
    String datasetName = RemoteBigQueryHelper.generateDatasetName();
    bigquery.create(DatasetInfo.builder(datasetName).build());
    TableId tableId = TableId.of(datasetName, "my_table");
    BaseTableInfo info = bigquery.getTable(tableId);
    if (info == null) {
      System.out.println("Creating table " + tableId);
      Field integerField = Field.of("fieldName", Field.Type.integer());
      bigquery.create(TableInfo.of(tableId, Schema.of(integerField)));
    }
    RemoteBigQueryHelper.forceDelete(bigquery, datasetName);
    System.out.println("Finished BigQuery test.");
  }

  private static void runDatastoreActions(Datastore datastore) {
    System.out.println("Testing Datastore.");
    KeyFactory keyFactory = datastore.newKeyFactory().kind("Person");
    Key key = keyFactory.newKey("myid");
    Entity entity = datastore.get(key);
    if (entity == null) {
      entity = Entity.builder(key)
          .set("name", "John Doe")
          .set("age", 30)
          .set("access_time", DateTime.now())
          .build();
      datastore.put(entity);
    } else {
      System.out.println("Updating access_time for " + entity.getString("name"));
      entity = Entity.builder(entity)
          .set("access_time", DateTime.now())
          .build();
      datastore.update(entity);
    }
    System.out.println("Finished Datastore test.");
  }

  private static void runResourceManagerActions(ResourceManager resourceManager) {
    System.out.println("Testing Resource Manager.");
    Iterator<ProjectInfo> projectIterator = resourceManager.list().iterateAll();
    System.out.println("Projects I can view:");
    while (projectIterator.hasNext()) {
      System.out.println(projectIterator.next().projectId());
    }
    System.out.println("Finished Resource Manager test.");
  }

  private static void runStorageActions(Storage storage) {
    System.out.println("Testing Storage.");
    String bucketName = RemoteGcsHelper.generateBucketName();
    storage.create(BucketInfo.of(bucketName));
    BlobId blobId = BlobId.of(bucketName, "my_blob");
    Blob blob = Blob.load(storage, blobId);
    if (blob == null) {
      BlobInfo blobInfo = BlobInfo.builder(blobId).contentType("text/plain").build();
      storage.create(blobInfo, "Hello, Cloud Storage!".getBytes(UTF_8));
      System.out.println("Writing a file to Storage.");
    } else {
      System.out.println("Updating content for " + blobId.name());
      byte[] prevContent = blob.content();
      System.out.println(new String(prevContent, UTF_8));
      WritableByteChannel channel = blob.writer();
      try {
        channel.write(ByteBuffer.wrap("Updated content".getBytes(UTF_8)));
        channel.close();
      } catch (IOException e) {
        System.out.println(e.toString());
      }
    }
    System.out.println("Finished Storage test.");
  }
}
