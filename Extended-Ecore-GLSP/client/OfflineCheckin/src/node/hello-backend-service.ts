import { injectable } from "@theia/core/shared/inversify";

import { HelloBackendService } from "../common/protocol";

@injectable()
export class HelloBackendServiceImpl implements HelloBackendService {

    sayHelloTo(name: string): Promise<string> {

        const WebSocket = require('ws');

        const ws = new WebSocket('ws://localhost:6000/websocket');

        ws.on('open', function open() {
            console.log('connected');
            ws.send('Commit Change Offline');
            console.log('sent');
        });

        return new Promise<string>(resolve => resolve('Hello ' + name));
    }
}
