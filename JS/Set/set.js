const { SyncMsg } = require('./SyncMsg/msg_pb.js')

class SetCRDT {
    constructor(id) {
        this.id = id
        this.value = new Set()
        this.updates = []
        this.lock = false
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

    async add(val) {
        await this.acquireLock()
        if (!this.value.has(val)) {
            this.value.add(val)
            this.updates.push("Add:" + val)
        }
        this.releaseLock()
    }

    async remove(val) {
        await this.acquireLock()
        if (this.value.has(val)) {
            this.value.delete(val)
            this.updates.push("Remove:" + val)
        }
        this.releaseLock()
    }

    async setRemoteVal(rid, optName, val) {
        if (optName === "Add") {
            this.add(val)
        } else if (optName === "Remove") {
            this.remove(val)
        }
    }

    getId() {
        return this.id
    }

    getValue() {
        var vals = Array.from(this.value)
        return vals.sort((a, b) => a - b)
    }

    async merge(rid, rUpdates) {
        console.log(`Starting to merge req from replica_ ${rid}`)
        if (rUpdates.length > 0) {
            for (let i = 0; i < rUpdates.length; i++) {
                var reqs = rUpdates[i].split(":")
                await this.setRemoteVal(rid, reqs[0], parseInt(reqs[1]))
            }
        }
        console.log("Merged")
        this.print()
    }

    print() {
        console.log("Set:" + this.id + " " , this.getValue())
    }

    // toMarshal() {
    //     const data = [String(this.getId()), ...this.updates]
    //     const jsonData = JSON.stringify(data)
    //     this.updates = []
    //     return jsonData
    // }

    toMarshal() {
        let msg = new SyncMsg();

        msg.setId(this.getId())
        msg.setUpdatesList(this.updates)

        let serialized = msg.serializeBinary()
        // console.log("Serialized bytes:", serialized);
        this.updates = []
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

module.exports = SetCRDT