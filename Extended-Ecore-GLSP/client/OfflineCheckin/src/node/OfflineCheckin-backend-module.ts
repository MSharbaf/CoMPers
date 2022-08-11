import { ConnectionHandler, JsonRpcConnectionHandler } from "@theia/core";
import { ContainerModule } from "@theia/core/shared/inversify";

import { HELLO_BACKEND_PATH, HelloBackendService } from "../common/protocol";
import { HelloBackendServiceImpl } from "./hello-backend-service";

export default new ContainerModule(bind => {
    bind(HelloBackendService).to(HelloBackendServiceImpl).inSingletonScope()
    bind(ConnectionHandler).toDynamicValue(ctx =>
        new JsonRpcConnectionHandler(HELLO_BACKEND_PATH, () => {
            return ctx.container.get<HelloBackendService>(HelloBackendService);
        })
    ).inSingletonScope();

});
