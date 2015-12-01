package com.google.appengine.sparkdemo;
/*
 * Copyright (c) 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import com.google.gson.Gson;
import spark.ResponseTransformer;
import spark.Spark;
import static spark.Spark.after;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

public class UserController {

  public UserController(final UserService userService) {
    Spark.staticFileLocation("/public");

    get("/api/users", (req, res) -> userService.getAllUsers(), UserController::toJson);

    get("/api/users/:id", (req, res) -> {
      String id = req.params(":id");
      User user = userService.getUser(id);
      if (user != null) {
        return user;
      }
      res.status(400);
      return new ResponseError("No user with id '%s' found", id);
    }, json());

    post("/api/users", (req, res) -> userService.createUser(
            req.queryParams("name"),
            req.queryParams("email")
    ), json());

    put("/api/users/:id", (req, res) -> userService.updateUser(
            req.params(":id"),
            req.queryParams("name"),
            req.queryParams("email")
    ), json());
    
    delete("/api/users/:id", (req, res) -> userService.deleteUser(
            req.params(":id"),
            req.queryParams("name"),
            req.queryParams("email")
    ), json());
    after((req, res) -> {
      res.type("application/json");
    });

    exception(IllegalArgumentException.class, (e, req, res) -> {
      res.status(400);
      res.body(toJson(new ResponseError(e)));
    });
  }
  	private static String toJson(Object object) {
		return new Gson().toJson(object);
	}

	private static ResponseTransformer json() {
		return UserController::toJson;
	}
}
