package qdb.io.server;

import com.typesafe.config.Config;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

/**
 * Routes requests to handlers for processing.
 */
@Singleton
public class Router implements Container {

    private static final Logger log = LoggerFactory.getLogger(Router.class);

    private Config cfg;

//    @Inject
//    private QueueManager databaseRegistry;

    @Inject
    public Router(Config cfg) throws IOException {
        this.cfg = cfg;
    }

    @Override
    public void handle(Request req, Response resp) {
        try {
            log.debug("path = " + req.getPath());
            if ("POST".equals(req.getMethod())) {
//                buffer.append(System.currentTimeMillis(), req.getPath().getPath(), req.getByteChannel(),
//                        req.getContentLength());
//                buffer.sync();
            } else {
//                resp.set("Content-Type", "text/plain");
//                PrintStream p = resp.getPrintStream();
//                MessageCursor c = buffer.cursor(0);
//                while (c.next()) {
//                    p.println("id " + c.getId() + " timestamp " + c.getTimestamp() + " key " + c.getRoutingKey());
//                }
//                c.close();
            }
            resp.setCode(200);
            resp.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
