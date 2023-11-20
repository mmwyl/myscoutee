import {
    Component, EventEmitter, Input, OnDestroy, OnInit, Output
} from '@angular/core';
import { Router } from '@angular/router';
import { NavigationService } from 'src/app/navigation.service';
import { MqttService } from 'src/app/services/mqtt.service';
import * as uuid from 'uuid';

const DIFF_TYPING = 10000;

const PREFIX = "channels/pages"

@Component({
    selector: 'ms-bar',
    templateUrl: './index.html',
    styles: [':host { position: fixed !important; bottom: 0; width: 100%; height: 48px }']
})
export class MsBar implements OnInit, OnDestroy {
    @Input() alias: string;
    @Output() add: EventEmitter<any> = new EventEmitter();

    btnDisabled: boolean = true;
    lastTyping: number;
    message: string = "";

    url: string;

    constructor(
        private mqttService: MqttService,
        private navService: NavigationService,
        private router: Router
    ) {
        this.url = PREFIX + this.router.url;
    }

    ngOnDestroy() {
        this.mqttService.unregister(this.url);
    }

    ngOnInit() {
        this.mqttService.register(this.url, (msg) => {
            this.handleMsg(msg);
        });

        setInterval(() => {
            let now = new Date().getTime();
            if (this.lastTyping && (now - this.lastTyping) >= 10000) {
                this.lastTyping = undefined;
                this.typing(false);
            }
        }, DIFF_TYPING);
        /*let avatarsBottom = new Array();
        for (let i = 0; i < 12; i++) {
            avatarsBottom.push("assets/img/profiles/man1.jpg");
        }

        this.avatarsBottom = avatarsBottom;*/
    }

    handleMsg(msg) {
        console.log("msg arrived: " + msg);

        msg = msg.substring(0, msg.length - 1);

        let msgObj = JSON.parse(msg);
        if (msgObj.message.type === "p") {
            this.add.emit(msgObj);

            msgObj.message.type = "r";

            this.mqttService.publish(this.url, JSON.stringify(msgObj));
        } else if (msgObj.message.type === "r") {
            console.log("read");
            console.log(msgObj);
        }
    }

    send() {
        let fromImage = this.navService.user["profile"]["images"][0];
        let payload = { from: fromImage, message: { type: "p", value: this.message, ref: uuid.v4() } };

        this.mqttService.publish(this.url, JSON.stringify(payload));
        this.message = "";
        this.lastTyping = undefined;
        this.btnDisabled = true;
    }

    msgChange(msg) {
        this.message = msg;
        if (this.message !== "") {
            this.btnDisabled = false;
        }

        if (!this.lastTyping) {
            this.typing(true);
        }
        this.lastTyping = new Date().getTime();
    }

    typing(isTyping) {
        let fromImage = this.navService.user["profile"]["images"][0];
        let payload = { from: fromImage, message: { type: "w", value: isTyping } };

        this.mqttService.publish(this.url, JSON.stringify(payload));
    }

    isConfirmNeeded() {
        return false;
    }
}