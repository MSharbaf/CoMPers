package app.propagation;

import io.javalin.http.Handler;
import java.util.Map;

import app.util.ViewUtil;

import static app.Main.*;
import static app.util.RequestUtil.*;

public class PropagationController {

    public static Handler fetchAllPropagations = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("propagations", propagationDao.getAllPropagations());
 //       ctx.render(Path.Template.RESOURCES_AL, model);
    };

    public static Handler fetchOnePropagation = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("propagation", propagationDao.getPropagation(getParamResourceID(ctx),getParamClientID(ctx)));
 //       ctx.render(Path.Template.RESOURCES_ON, model);
    };
}
