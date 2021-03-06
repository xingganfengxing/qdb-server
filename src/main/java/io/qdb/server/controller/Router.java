/*
 * Copyright 2013 David Tinker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.qdb.server.controller;

import io.qdb.server.security.Auth;
import io.qdb.server.security.AuthService;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

/**
 * Routes requests to controllers for processing.
 */
@Singleton
public class Router implements Container {

    private static final Logger log = LoggerFactory.getLogger(Router.class);

    private final AuthService authService;
    private final Renderer renderer;
    private final ServerController serverController;
    private final DatabaseController databaseController;
    private final UserController userController;
    private final AdminUIController adminUIController;

    @Inject
    public Router(AuthService authService, Renderer renderer, ServerController serverController,
                  DatabaseController databaseController, UserController userController,
                  AdminUIController adminUIController) {
        this.authService = authService;
        this.renderer = renderer;
        this.serverController = serverController;
        this.databaseController = databaseController;
        this.userController = userController;
        this.adminUIController = adminUIController;
    }

    @Override
    public void handle(Request req, Response resp) {
        try {
            Call call = new Call(req, resp, renderer);
            Auth auth = authService.authenticate(req, resp);
            if (auth == null) {
                authService.sendChallenge(resp);
            } else {
                call.setAuth(auth);
                String seg = call.nextSegment();
                if (seg == null) {
                    adminUIController.handle(call);
                } else if (call.getAuth().isAnonymous()) {
                    authService.sendChallenge(resp);
                } else if ("q".equals(seg)) {
                    databaseController.getController(call, "default", seg).handle(call);
                } else if ("db".equals(seg)) {
                    databaseController.handle(call);
                } else if ("users".equals(seg)) {
                    userController.handle(call);
                } else if ("status".equals(seg)) {
                    serverController.handle(call);
                } else {
                    adminUIController.handle(call);
                }
            }
        } catch (Exception e) {
            String msg = "500: " + req.getPath() + ": " + e;
            // ProducerException is package private so we cannot use instanceof
            if ("org.simpleframework.http.core.ProducerException".equals(e.getClass().getName())) {
                // likely the remote client has closed the connection so only debug
                if (log.isDebugEnabled()) log.debug(msg, e);
            } else {
                log.error(msg, e);
            }
            quietRenderCode(req, resp, 500, null);
        }
        try {
            resp.close();
        } catch (IOException x) {
            if (log.isDebugEnabled()) log.debug("Error closing response: " + x, x);
        }
    }

    private void quietRenderCode(Request req, Response resp, int code, String msg) {
        try {
            renderer.setCode(resp, code, msg);
        } catch (IOException x) {
            if (log.isDebugEnabled()) log.debug(req.getPath() + ": Error sending " + code + ": " + x.getMessage());
        }
    }
}
