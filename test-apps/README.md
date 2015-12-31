gcloud-java Test Applications
=============================

This folder contains examples of end-to-end test apps for gcloud-java.

In order to run these tests, you will need to:

* Create a [Google Developers Console](https://console.developers.google.com/) project with the BigQuery, Datastore, and Storage JSON API enabled.
* [Enable billing](https://support.google.com/cloud/answer/6158867?hl=en). [Follow these instructions](https://cloud.google.com/docs/authentication#preparation) to get your project set up.
* Set up the local development environment by [installing the Google Cloud SDK](https://cloud.google.com/sdk/) and running the following commands in command line: `gcloud auth login` and `gcloud config set project [YOUR PROJECT ID]`.

To run the App Engine test app:
1. Change "your-app-id" to your project ID in `src/main/webapp/WEB-INF/appengine-web.xml` and in `src/main/java/com/google/gcloud/tests/gcloud-appengine-test/MainServlet.java`.
2. Create JSON service account credentials and save it as follows: `src/main/webapp/WEB-INF/lib/my-service-account-credentials.json`.
3. Run `mvn appengine:devserver` to test locally and `mvn appengine:update` to test the app in production.

To run the Compute Engine test app:

1. Change "your-app-id" to your project ID in `src/main/java/com/google/gcloud/tests/gcloud-java-compute-test/GcloudJavaComputeEngineTest.java`.
2. Create JSON service account credentials and save the file in application's base directory as `my-service-account-credentials.json`.
3. Create a Compute Engine instance with the User Info, Big Query, Datastore, and Storage APIs (read write) enabled.
4. Copy your the app to your instance using the Google Cloud SDK command:
  ```
  gcloud compute copy-files [the app's base directory] [username@instance-name]:~
  ```
5. Ensure that Maven and the Java 7 JDK are installed.  Also check that the environment variable `JAVA_HOME` points to the Java 7 JDK.
6. Run the app using the command
  ```
  mvn clean compile exec:java -Dexec.mainClass="com.google.gcloud.tests.desktop.GcloudJavaComputeEngineTest"
  ```

To run the desktop test app:

1. Change "your-app-id" to your project ID in `src/main/java/com/google/gcloud/tests/gcloud-java-desktop-test/GcloudJavaDesktopTest.java`.
2. Create JSON service account credentials and save the file in application's base directory as `my-service-account-credentials.json`.
3. Run the app using the command
  ```
  mvn clean compile exec:java -Dexec.mainClass="com.google.gcloud.tests.desktop.GcloudJavaDesktopTest"
  ```

License
-------

Apache 2.0 - See [LICENSE](https://github.com/GoogleCloudPlatform/gcloud-java-examples/blob/master/LICENSE) for more information.
