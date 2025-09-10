class EventBus {
    listeners: any;

    constructor() {
        this.listeners = {};
    }

    on(event: string, callback: any) {
        if (!this.listeners[event]) {
            this.listeners[event] = [];
        }
        this.listeners[event].push(callback);
    }

    off(event: string, callback: any) {
        if (this.listeners[event]) {
            this.listeners[event] = this.listeners[event].filter(
                (listener: any) => listener !== callback
            );
        }
    }

    emit(event: string, data: any) {
        console.log(this.listeners);
        if (this.listeners[event]) {
            this.listeners[event].forEach((listener: any) => listener(data));
        }
    }
}

const eventBus = new EventBus();
export default eventBus;