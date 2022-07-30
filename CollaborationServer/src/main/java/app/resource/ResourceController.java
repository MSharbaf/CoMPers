package app.resource;

import io.javalin.http.Handler;
import java.util.Map;

import app.util.Path;
import app.util.ViewUtil;

import static app.Main.*;
import static app.util.RequestUtil.*;

public class ResourceController {

    public static Handler fetchAllResources = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("resources", resourceDao.getAllResources());
        ctx.render(Path.Template.RESOURCES_ALL, model);
    };

    public static Handler fetchOneResource = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("resource", resourceDao.getResourceByresourceID(getParamResourceID(ctx)));
        ctx.render(Path.Template.RESOURCES_ONE, model);
    };
}
