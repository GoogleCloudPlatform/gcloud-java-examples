gcloud-java Test Applications
=============================

This folder contains examples of test apps for gcloud-java.

In order to run these tests, you will need to:

* Create a [Google Developers Console](https://console.developers.google.com/) project with the BigQuery, Datastore, and Storage JSON APIs enabled. [Follow these instructions](https://cloud.google.com/docs/authentication#preparation) to get your project set up.
* [Enable billing](https://support.google.com/cloud/answer/6158867?hl=en).
* Set up the local development environment by [installing the Google Cloud SDK](https://cloud.google.com/sdk/) and running the following commands in command line: `gcloud auth login` and `gcloud config set project [YOUR PROJECT ID]`.

To run the App Engine test app:

1. Change "your-project-id" to your project ID in `src/main/webapp/WEB-INF/appengine-web.xml`.
2. Create JSON service account credentials and save the file as follows: `src/main/webapp/WEB-INF/lib/my-service-account-credentials.json`.
3. Run `mvn appengine:devserver` to test locally and `mvn appengine:update` to test the app in production.
4. Navigate to the URL where the app is deployed. Add the service you wish to test to the URL ("bigquery", "datastore", "resourcemanager", or "storage"). You can also set optional request parameters for the project ID and whether to use the JSON credentials file you included in `WEB-INF/lib`. Here's an example URL using datastore with project ID "my-project-id" that uses the JSON credentials file:

  ```
  http://localhost:8080/datastore?project-id=my-project-id&credentials-file=true
  ```

To run the command line test app on Compute Engine:

1. Create JSON service account credentials and save the file in application's base directory as `my-service-account-credentials.json`.
2. Create a Compute Engine instance with the User Info, Big Query, Datastore, and Storage APIs (read write) enabled.
3. Copy your the app to your instance using the Google Cloud SDK command:

  ```
  gcloud compute copy-files test-apps/command-line-test-app [username@instance-name]:~
  ```
4. Ensure that Maven and the Java 7 JDK are installed.  Also check that the environment variable `JAVA_HOME` points to the Java 7 JDK.
5. Ensure that you have User Credentials available on your Compute Engine instance. These credentials are necessary to use Resource Manager. If you get authentication errors stemming from inadequate authentication scope during execution, you can copy your gcloud SDK credentials (usually located in the `~/.config/gcloud` directory) to your Compute Engine instance and set the GOOGLE_APPLICATION_CREDENTIALS environment variable as follows: `export GOOGLE_APPLICATION_CREDENTIALS=<insert-credential-location>`.
6. Run the app using Maven's exec plugin. Specify the service and optional parameters for explicitly setting a project ID and using your JSON credentials file as command line parameters. Here is an example of running the storage test, including both the optional parameters.

  ```
  mvn clean compile exec:java \
      -Dexec.mainClass="com.google.gcloud.tests.commandline.GcloudJavaCommandLineTest" \
      -Dexec.args="storage project-id=my-project-id credentials-file=true"
  ```

To run the command line test app on your desktop:

1. Create JSON service account credentials and save the file in application's base directory as `my-service-account-credentials.json`.
2. Run the app using the same command line structure as for Compute Engine.

License
-------

Apache 2.0 - See [LICENSE](https://github.com/GoogleCloudPlatform/gcloud-java-examples/blob/master/LICENSE) for more information.
