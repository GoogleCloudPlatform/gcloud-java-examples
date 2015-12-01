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
package com.google.appengine.sparkdemo;

import com.google.gcloud.datastore.Datastore;
import com.google.gcloud.datastore.DatastoreOptions;
import com.google.gcloud.datastore.Entity;
import com.google.gcloud.datastore.FullEntity;
import com.google.gcloud.datastore.Key;
import com.google.gcloud.datastore.KeyFactory;
import com.google.gcloud.datastore.Query;
import com.google.gcloud.datastore.QueryResults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {

  static String KINDNAME = "DEMOUSER";

  static Datastore datastore = DatastoreOptions.defaultInstance().service();

  public List<User> getAllUsers() {
    Map<String, User> users = new HashMap<>();

    String gql = "SELECT * FROM " + KINDNAME;
    Query<Entity> query = Query.gqlQueryBuilder(Query.ResultType.ENTITY, gql)
            .build();
    QueryResults<Entity> results = datastore.run(query);

    while (results.hasNext()) {
      Entity result = results.next();

      User u = new User(result.getString("name"), result.getString("email"));

      users.put(result.getString("name"), u);
    }
    return new ArrayList<>(users.values());
  }

  public User getUser(String id) {
    System.out.println("get user called");
    KeyFactory keyFactory = datastore.newKeyFactory().kind(KINDNAME);
    Key key = keyFactory.newKey(id);
    Entity entity = datastore.get(key);
    User user = new User(entity.getString("name"), entity.getString("email"));
    return user;
  }

  public User createUser(String name, String email) {
    System.out.println("create user called");
    KeyFactory keyFactory = datastore.newKeyFactory().kind(KINDNAME);
    Key key = keyFactory.newKey(email);

    FullEntity entity = Entity.builder(key)
            .set("name", name)
            .set("email", email)
            .build();
    Entity e = datastore.add(entity);

    failIfInvalid(name, email);
    User user = new User(name, email);
    return user;
  }

  public String deleteUser(String id, String name, String email) {
    System.out.println("delete user called");
    KeyFactory keyFactory = datastore.newKeyFactory().kind(KINDNAME);
    Key key = keyFactory.newKey(id);
    datastore.delete(key);

    return "ok";
  }

  public User updateUser(String id, String name, String email) {
    System.out.println("Update user called");
    KeyFactory keyFactory = datastore.newKeyFactory().kind(KINDNAME);
    Key key = keyFactory.newKey(id);
    Entity entity = datastore.get(key);
    if (entity == null) {
      throw new IllegalArgumentException("No user with id '" + id + "' found");

    } else {
      System.out.println("Updating access_time for " + entity.getString("name"));
      entity = Entity.builder(entity)
              .set("name", name)
              .set("email", email)
              .build();
      datastore.update(entity);
    }
    User user = new User(name, email);
    return user;
  }

  private void failIfInvalid(String name, String email) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Parameter 'name' cannot be empty");
    }
    if (email == null || email.isEmpty()) {
      throw new IllegalArgumentException("Parameter 'email' cannot be empty");
    }
  }
}
