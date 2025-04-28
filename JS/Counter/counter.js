const { SyncMsg } = require('./SyncMsg/msg_pb.js')
const PluginManager = require('./pluginManager')

let pm = new PluginManager()

class Counter {
    constructor(id) {
        this.id = id
        this.value = 0
        this.updates = []
        this.lock = false
        pm.notify(id, "create")
    }

    async acquireLock() {
        while (this.lock) {
            await new Promise(resolve => setTimeout(resolve, 10))
        }
        this.lock = true
    }

    releaseLock() {
        this.lock = false
    }

    async setVal(newVal, optName) {
        this.value = newVal
        this.updates.push(optName)
    }

    async setRemoteVal(rid, optName) {
        if (optName === "Inc") {
            this.value += 1
        } else if (optName === "Dec") {
            this.value -= 1
        }
    }

    async inc() {
        await this.acquireLock()
        const newVal = this.value + 1
        await this.setVal(newVal, "Inc")
        this.releaseLock()

        // Logging
        const logMsg = `Local Inc, Updated Value is ${this.value}`
        pm.notify(this.id, logMsg)
    }

    async dec() {
        await this.acquireLock();
        const newVal = this.value - 1
        await this.setVal(newVal, "Dec")
        this.releaseLock()

        // Logging
        const logMsg = `Local Dec, Updated Value is ${this.value}`
        pm.notify(this.id, logMsg)
    }

    getId() {
        return this.id
    }

    getValue() {
        return this.value
    }

    async merge(rid, rUpdates) {
        console.log(`Starting to merge req from replica_ ${rid}`)
        if (rUpdates.length > 0) {
            for (let i = 0; i < rUpdates.length; i++) {
                await this.setRemoteVal(rid, rUpdates[i])
            }
        }
        console.log(`Merged ${this.print()}`)

        // logging
        const logMsg = `Synchronizing with Replica ${rid}, Updated Value is ${this.value}`
        pm.notify(this.id, logMsg)
    }

    print() {
        return `Counter_${this.getId()}:${this.getValue()}`
    }

    // toMarshal() {
    //     const data = [String(this.getId()), ...this.updates]
    //     const jsonData = JSON.stringify(data)
    //     this.updates = []
    //     return jsonData
    // }

    toMarshal() {
        let msg = new SyncMsg()

        msg.setId(this.getId())
        msg.setUpdatesList(this.updates)

        let serialized = msg.serializeBinary()
        // console.log("Serialized bytes:", serialized);
        this.updates = []

        // logging
        const logMsg = `BroadCasting current state`
        pm.notify(this.id, logMsg)

        return serialized
    }

    // static fromMarshalData(bytes) {
    //     const remoteUpdates = JSON.parse(bytes)
    //     const rid = parseInt(remoteUpdates[0], 10)
    //     if (remoteUpdates.length === 1) {
    //         return [rid, []]
    //     }
    //     const rUpdates = remoteUpdates.slice(1)
    //     return [rid, rUpdates]
    // }

    static fromMarshalData(bytes) {
        let receivedMessage = SyncMsg.deserializeBinary(bytes);
        return [receivedMessage.getId(), receivedMessage.getUpdatesList()]
    }
}

module.exports = Counter