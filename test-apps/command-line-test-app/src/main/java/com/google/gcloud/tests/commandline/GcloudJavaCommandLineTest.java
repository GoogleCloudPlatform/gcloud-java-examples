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

package com.google.gcloud.tests.commandline;

import static com.google.gcloud.tests.ServiceTests.SUPPORTED_SERVICES;
import static com.google.gcloud.tests.ServiceTests.runAction;

import com.google.gcloud.AuthCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * A command line application to test gcloud-java on Google Compute Engine and your desktop.
 */
public class GcloudJavaCommandLineTest {

  /**
   * Runs some simple tests for the service specified as a command line argument. Run this program
   * without any arguments to see the list of services available to test.
   *
   * @param args [service] [project-id=your-project-id] [credentials-file=true|false]. The first
   *     first argument, the service, is required and must be the first argument. The others are
   *     optional.
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    PrintWriter pw = new PrintWriter(System.out, true);
    String projectId = null;
    AuthCredentials credentials = null;
    if (args.length == 0) {
      pw.println("Must specify a service to test as the first command line argument.  Options are: "
          + SUPPORTED_SERVICES.toString());
    } else {
      for (String arg : Arrays.copyOfRange(args, 1, args.length)) {
        String[] argInfo = arg.split("=");
        switch (argInfo[0]) {
          case "project-id":
            projectId = argInfo.length > 1 ? argInfo[1] : null;
            break;
          case "credentials-file":
            credentials = AuthCredentials.createForJson(
                new FileInputStream("my-service-account-credentials.json"));
            break;
          default:
            pw.println("Unrecognized optional argument " + arg + ". Acceptable optional "
                + "arguments are 'project-id=[your-project-id]' and "
                + "'credentials-file=[true|false]'");
            return;
        }
      }
      runAction(args[0], pw, projectId, credentials);
    }
  }
}
