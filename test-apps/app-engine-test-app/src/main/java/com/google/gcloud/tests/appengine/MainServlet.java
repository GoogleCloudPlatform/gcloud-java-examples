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

package com.google.gcloud.tests.appengine;

import static com.google.gcloud.tests.ServiceTests.SUPPORTED_SERVICES;
import static com.google.gcloud.tests.ServiceTests.runAction;

import com.google.gcloud.AuthCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    PrintWriter responseWriter = resp.getWriter();
    String projectId = null;
    AuthCredentials credentials = null;
    String[] pathInfo = req.getPathInfo().split("/");
    if (pathInfo.length != 2) {
      printHelpMessage(responseWriter);
    } else {
      projectId = req.getParameter("project-id");
      if (Boolean.parseBoolean(req.getParameter("credentials-file"))) {
        ServletContext context = getServletContext();
        InputStream in =
            context.getResourceAsStream("/WEB-INF/lib/my-service-account-credentials.json");
        credentials = AuthCredentials.createForJson(in);
      }
      runAction(pathInfo[1], responseWriter, projectId, credentials);
    }
  }

  private static void printHelpMessage(PrintWriter pw) {
    pw.println(
        "Specify the service you wish to test in the URL (i.e. http://localhost:8080/datastore). "
        + "The following services are supported: " + SUPPORTED_SERVICES.toString() + ". "
        + "To explicitly set a project ID, specify the request parameter project-id=my-project-id. "
        + "To use a service account credentials file, specify the request parameter "
        + "'credentials-file=true'. An example of setting both parameters: "
        + "http://localhost:8080/datastore?project-id=my-project-id&credential-file=true");
  }
}
