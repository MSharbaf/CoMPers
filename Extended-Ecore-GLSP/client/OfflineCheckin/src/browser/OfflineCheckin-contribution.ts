import { CommonMenus } from "@theia/core/lib/browser";
import {
    Command,
    CommandContribution,
    CommandRegistry,
    MenuContribution,
    MenuModelRegistry,
    MessageService
} from "@theia/core/lib/common";
import { inject, injectable } from "@theia/core/shared/inversify";

import { HelloBackendService } from "../common/protocol";

const SayHelloViaBackendCommand: Command = {
    id: 'sayHelloOnBackend.command',
    label: 'Commit Changes (Offline Check-in)',
};

@injectable()
export class OfflineCheckinCommandContribution implements CommandContribution {

    constructor(
        @inject(MessageService) private readonly messageService: MessageService,
        @inject(HelloBackendService) private readonly helloBackendService: HelloBackendService,
    ) { }

    registerCommands(registry: CommandRegistry): void {
        registry.registerCommand(SayHelloViaBackendCommand, {
            execute: () => {
                this.helloBackendService.sayHelloTo('World').then(r => console.log(r));
                this.messageService.info('Changes are submitted to the Server ...!');
            }
        });
    }
}

@injectable()
export class OfflineCheckinMenuContribution implements MenuContribution {

    registerMenus(menus: MenuModelRegistry): void {
        menus.registerMenuAction(CommonMenus.VIEW_PRIMARY, {
            commandId: SayHelloViaBackendCommand.id,
            label: SayHelloViaBackendCommand.label
        });
    }
}
