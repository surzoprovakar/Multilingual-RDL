const { LogMsg } = require('./Plugin/logger_pb.js')
const net = require("net")
const fs = require("fs")
const path = require("path")


class LamportClock {
    constructor() {
        this.lClock = 0
    }

    increment() {
        this.lClock++
    }

    getTimestamp() {
        return { lamportTime: this.lClock, physicalTime: new Date() }
    }
}

let lc = null

class ReplicaState {
    constructor() {
        this.versionMapState = {}
        this.lastVersion = 0
        this.versionMapState[0] = 0
        this.lastVersion++
    }
}

let replicaStates = {}

function createLog(rId) {
    const logFile = `Replica_${rId}.log`

    if (fs.existsSync(logFile)) {
        console.log("Log File Already Exists")
    } else {
        fs.writeFileSync(logFile, "", { flag: "w" })
    }

    lc = new LamportClock()
    replicaStates[rId] = new ReplicaState()
}

function persist(rId, msg) {
    const logFile = `Replica_${rId}.log`

    lc.increment()
    const { lamportTime, physicalTime } = lc.getTimestamp()
    const logEntry = `${msg}, Lamport Time: ${lamportTime}, Physical Time: ${physicalTime.toISOString()}\n`

    fs.appendFile(logFile, logEntry, (err) => {
        if (err) console.error("Failed to log:", err)
    })

    if (String(msg).includes("Updated Value is")) {
        const state = replicaStates[rId]
        const value = extractVersionValue(msg)
        state.versionMapState[state.lastVersion] = value
        state.lastVersion++
    }
}

function execute(bytes) {
    const logMsg = LogMsg.deserializeBinary(bytes)

    const id = logMsg.getId()
    const logMessage = logMsg.getLogs()

    if (String(logMsg).includes("Undo")) {
        const tasks = String(logMsg).split("_")
        const task = tasks[1]

        console.log("Undo request came from application")
        const action = undo(task)

        let counterAction = new LogMsg()
        counterAction.setId(id)
        counterAction.setLogs(action)

        let serialized = counterAction.serializeBinary()
        serialized = Buffer.from([...serialized, 0x00])

        let replicaAddr
        if (id === 1) {
            replicaAddr = "localhost:8081"
        } else if (id === 2) {
            replicaAddr = "localhost:8082"
        } else {
            replicaAddr = "localhost:8083"
        }

        sendBackToReplica(replicaAddr, serialized)
    } else if (String(logMsg).includes("Rev")) {
        const tasks = String(logMsg).split("_")
        const rev = tasks[1]
        console.log("Rollback request came from application:", rev)

        const action = reversibility(id, rev)
        console.log("Counter action is:", action)

        let counterAction = new LogMsg()
        counterAction.setId(id)
        counterAction.setLogs(action)

        let serialized = counterAction.serializeBinary()
        serialized = Buffer.from([...serialized, 0x00])

        let replicaAddr
        if (id === 1) {
            replicaAddr = "localhost:8081"
        } else if (id === 2) {
            replicaAddr = "localhost:8082"
        } else {
            replicaAddr = "localhost:8083"
        }
        sendBackToReplica(replicaAddr, serialized)
    } else if (logMessage === "create") {
        createLog(id)
    } else {
        persist(id, logMessage)
    }
}

async function keepConnection(conn) {
    const reader = conn
    let message = Buffer.alloc(0)

    conn.on('data', (data) => {
        for (let i = 0; i < data.length; i++) {
            if (data[i] === 0x00) {
                execute(message)
                message = Buffer.alloc(0)
                return
            }
            message = Buffer.concat([message, Buffer.from([data[i]])])
        }
    })

    conn.on('end', () => {
        // console.log('Client left.')
        conn.end()
    })
}

async function main() {
    const server = net.createServer((conn) => {
        // console.log("Accepted new connection")
        keepConnection(conn)
    })

    server.listen(8080, "localhost", () => {
        console.log("Logger server started on localhost:8080")
    })

    server.on("error", (err) => {
        console.error("Server error:", err)
    })
}


function sendBackToReplica(replicaAddr, message) {
    const [host, port] = String(replicaAddr).split(":")

    const client = new net.Socket()
    client.connect(port, host, () => {
        client.write(message)
    })

    client.on('error', (err) => {
        console.error("Failed to connect to replica:", err.message)
    })

    client.on('close', () => {
        // console.log("Connection closed")
    })
}

function undo(task) {
    return task === "Inc" ? "Dec" : "Inc"
}

function reversibility(id, version) {
    const state = replicaStates[id]
    const revVersion = parseInt(version, 10)
    const revVal = state.versionMapState[revVersion]
    const curVal = state.versionMapState[state.lastVersion - 1]
    let action = ""

    if (curVal === revVal) {
        console.log("Rolled back version is the same")
    } else if (curVal > revVal) {
        const diff = curVal - revVal
        action = `Rev_${diff}_Dec`
    } else {
        const diff = revVal - curVal
        action = `Rev_${diff}_Inc`
    }

    return action
}

function extractVersionValue(s) {
    const match = String(s).match(/Updated Value is (\d+)/)
    return match ? parseInt(match[1], 10) : 0
}

main().catch(err => {
    console.error('Error:', err.message)
    process.exit(1)
})