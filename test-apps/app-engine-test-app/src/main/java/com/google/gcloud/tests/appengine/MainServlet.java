package com.google.gcloud.tests.appengine;

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
import com.google.gcloud.storage.Blob;
import com.google.gcloud.storage.BlobId;
import com.google.gcloud.storage.BlobInfo;
import com.google.gcloud.storage.BucketInfo;
import com.google.gcloud.storage.Storage;
import com.google.gcloud.storage.StorageOptions;
import com.google.gcloud.storage.testing.RemoteGcsHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {

  private PrintWriter responseWriter;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    responseWriter = resp.getWriter();
    runBigQueryActions(BigQueryOptions.defaultInstance().service());
    ServletContext context = getServletContext();
    InputStream in =
        context.getResourceAsStream("/WEB-INF/lib/my-service-account-credentials.json");
    runDatastoreActions(
        DatastoreOptions.builder()
            .authCredentials(AuthCredentials.createForJson(in))
            .projectId("your-project-id")
            .build()
            .service());
    runStorageActions(StorageOptions.defaultInstance().service());
  }

  private void runBigQueryActions(BigQuery bigquery) {
    responseWriter.println("Testing BigQuery.");
    String datasetName = RemoteBigQueryHelper.generateDatasetName();
    bigquery.create(DatasetInfo.builder(datasetName).build());
    TableId tableId = TableId.of(datasetName, "my_table");
    BaseTableInfo info = bigquery.getTable(tableId);
    if (info == null) {
      responseWriter.println("Creating table " + tableId);
      Field integerField = Field.of("fieldName", Field.Type.integer());
      bigquery.create(TableInfo.of(tableId, Schema.of(integerField)));
    }
    RemoteBigQueryHelper.forceDelete(bigquery, datasetName);
    responseWriter.println("Finished BigQuery test.");
  }

  private void runDatastoreActions(Datastore datastore) {
    responseWriter.println("Testing Datastore.");
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
      responseWriter.println("Updating access_time for " + entity.getString("name"));
      entity = Entity.builder(entity)
          .set("access_time", DateTime.now())
          .build();
      datastore.update(entity);
    }
    responseWriter.println("Finished Datastore test.");
  }

  private void runStorageActions(Storage storage) {
    responseWriter.println("Testing Storage.");
    String bucketName = RemoteGcsHelper.generateBucketName();
    storage.create(BucketInfo.of(bucketName));
    BlobId blobId = BlobId.of(bucketName, "my_blob");
    Blob blob = Blob.load(storage, blobId);
    if (blob == null) {
      BlobInfo blobInfo = BlobInfo.builder(blobId).contentType("text/plain").build();
      storage.create(blobInfo, "Hello, Cloud Storage!".getBytes(UTF_8));
      responseWriter.println("Writing a file to Storage.");
    } else {
      responseWriter.println("Updating content for " + blobId.name());
      byte[] prevContent = blob.content();
      responseWriter.println(new String(prevContent, UTF_8));
      WritableByteChannel channel = blob.writer();
      try {
        channel.write(ByteBuffer.wrap("Updated content".getBytes(UTF_8)));
        channel.close();
      } catch (IOException e) {
        responseWriter.println(e.toString());
      }
    }
    responseWriter.println("Finished Storage test.");
  }
}
