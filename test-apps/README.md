gcloud-java Test Applications
=============================

This folder contains examples of test apps for gcloud-java.

Running the Test Applications
-----------------------------

### Prerequisites

In order to run these tests, you will need to:

* Create a [Google Developers Console](https://console.developers.google.com/) project with the BigQuery, Datastore, and Storage JSON APIs enabled. [Follow these instructions](https://cloud.google.com/docs/authentication#preparation) to get your project set up.
* [Enable billing](https://support.google.com/cloud/answer/6158867?hl=en).
* Set up the local development environment by [installing the Google Cloud SDK](https://cloud.google.com/sdk/) and running the following commands in command line: `gcloud auth login` and `gcloud config set project [YOUR PROJECT ID]`.
* Ensure the version of gcloud-java you wish to test is correctly specified in the `pom.xml` files in the `app-engine-test-app` and `command-line-test-app` folders.

### Running on App Engine

1. Change "your-project-id" to your project ID in `src/main/webapp/WEB-INF/appengine-web.xml`.
2. Run `mvn appengine:devserver` to test locally and `mvn appengine:update` to test the app in production.
3. Navigate to the URL where the app is deployed. Add the service you wish to test to the URL ("bigquery", "datastore", "resourcemanager", or "storage"). For example, to test datastore, navigate to `http://localhost:8080/datastore`.

### Running on Compute Engine

To run the command line test app on Compute Engine:

1. Create a Compute Engine instance with the User Info, Big Query, Datastore, and Storage APIs (read write) enabled.
2. Copy your the app to your instance using the Google Cloud SDK command:

  ```
  gcloud compute copy-files test-apps/command-line-test-app [username@instance-name]:~
  ```
3. Ensure that Maven and the Java 7 JDK are installed.  Also check that the environment variable `JAVA_HOME` points to the Java 7 JDK.
4. Ensure that you have User Credentials available on your Compute Engine instance. These credentials are necessary to use Resource Manager. If you get authentication errors stemming from inadequate authentication scope during execution, you can copy your gcloud SDK credentials (usually located in the `~/.config/gcloud` directory) to your Compute Engine instance and set the GOOGLE_APPLICATION_CREDENTIALS environment variable as follows: `export GOOGLE_APPLICATION_CREDENTIALS=<insert-credential-location>`.
6. Run the app using Maven's exec plugin. Specify the service you wish to test as a command line parameter. Here is an example of running the storage test.

  ```
  mvn clean compile exec:java \
      -Dexec.mainClass="com.google.gcloud.tests.commandline.GcloudJavaCommandLineTest" \
      -Dexec.args="storage"
  ```

### Running from your desktop

Run the app using the same command line structure as for Compute Engine.

### Testing authentication and project ID settings

##### On App Engine

1. Create JSON service account credentials and save the file as follows: `src/main/webapp/WEB-INF/lib/my-service-account-credentials.json`.
2. Set the optional request parameters to specify your project ID and to use the JSON credentials file you created. Here's an example URL using datastore with project ID "my-project-id" that uses the JSON credentials file:

  ```
  http://localhost:8080/datastore?project-id=my-project-id&credentials-file=true
  ```

##### On Compute Engine or your desktop

1. If you are testing authentication on Compute Engine or your own desktop, save the file in the application's base directory as `my-service-account-credentials.json`.
2. Specify the authentication and project ID as command line arguments ("project-id=your-project-id" and "credentials-file=true", respectively). Here is an example of running the storage test that includes both the optional parameters.

  ```
  mvn clean compile exec:java \
      -Dexec.mainClass="com.google.gcloud.tests.commandline.GcloudJavaCommandLineTest" \
      -Dexec.args="storage project-id=my-project-id credentials-file=true"
  ```
3. You can also test the environment variables for setting the project ID and authentication. Do not specify project ID and credentials file using the command line parameters. Instead, set the environment variables `GCLOUD_PROJECT` (to your project ID) and `GOOGLE_APPLICATION_CREDENTIALS` (to the location of your credentials file).

License
-------

Apache 2.0 - See [LICENSE](https://github.com/GoogleCloudPlatform/gcloud-java-examples/blob/master/LICENSE) for more information.
