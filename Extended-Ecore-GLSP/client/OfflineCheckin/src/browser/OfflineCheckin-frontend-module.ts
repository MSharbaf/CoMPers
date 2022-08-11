import { CommandContribution, MenuContribution } from "@theia/core";
import { WebSocketConnectionProvider } from "@theia/core/lib/browser";
import { ContainerModule } from "@theia/core/shared/inversify";

import { HELLO_BACKEND_PATH, HelloBackendService } from "../common/protocol";
import { OfflineCheckinCommandContribution, OfflineCheckinMenuContribution } from "./OfflineCheckin-contribution";

export default new ContainerModule(bind => {
    bind(CommandContribution).to(OfflineCheckinCommandContribution).inSingletonScope();
    bind(MenuContribution).to(OfflineCheckinMenuContribution);

    bind(HelloBackendService).toDynamicValue(ctx => {
        const connection = ctx.container.get(WebSocketConnectionProvider);
        return connection.createProxy<HelloBackendService>(HELLO_BACKEND_PATH);
    }).inSingletonScope();
});

