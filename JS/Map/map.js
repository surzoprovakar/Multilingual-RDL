const { SyncMsg } = require('./SyncMsg/msg_pb.js')

class MapCRDT {
    constructor(id) {
        this.id = id
        this.value = new Map()
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

    async add(key, val) {
        await this.acquireLock()
        if (!this.value.has(key)) {
            this.value.set(key, val)
            this.updates.push("Add:" + key + ":" + val)
        }
        this.releaseLock()
    }

    async delete(key) {
        await this.acquireLock()
        if (this.value.has(key)) {
            this.value.delete(key)
            this.updates.push("Delete:" + key)
        }
        this.releaseLock()
    }

    async update(key, newVal) {
        await this.acquireLock()
        if (this.value.has(key)) {
            this.value.set(key, newVal)
            this.updates.push("Update:" + key + ":" + newVal)
        }
        this.releaseLock()
    }

    async setRemoteVal(rid, optName, key, val) {
        if (optName === "Add") {
            this.add(key, val)
        } else if (optName === "Delete") {
            this.delete(key)
        } else if (optName === "Update") {
            this.update(key, val)
        }
    }

    getId() {
        return this.id
    }

    getValue() {
        let sortedArray = Array.from(this.value.entries()).sort((a, b) => a[0].localeCompare(b[0]))
        return new Map(sortedArray)
    }

    async merge(rid, rUpdates) {
        console.log(`Starting to merge req from replica_ ${rid}`)
        if (rUpdates.length > 0) {
            for (let i = 0; i < rUpdates.length; i++) {
                var reqs = rUpdates[i].split(":")
                await this.setRemoteVal(rid, reqs[0], reqs[1], parseInt(reqs[2]))
            }
        }
        console.log("Merged")
        this.print()
    }

    print() {
        console.log("Map:" + this.id + " ", this.getValue())
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

module.exports = MapCRDT