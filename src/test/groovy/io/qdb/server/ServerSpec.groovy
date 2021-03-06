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

package io.qdb.server

class ServerSpec extends StandaloneBase {

    def "Get status"() {
        def ans = GET("/status")

        expect:
        ans.code == 200
        ans.json.uptime != null
    }

    def "Authentication required for non-root urls"() {
        HttpURLConnection con = new URL(client.serverUrl + "/users").openConnection() as HttpURLConnection

        expect:
        con.responseCode == 401
        con.getHeaderField("WWW-Authenticate") == "basic realm=\"qdb\""
    }
}
