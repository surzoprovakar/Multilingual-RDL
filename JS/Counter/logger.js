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

function createLog(rId) {
    const logFile = `Replica_${rId}.log`

    if (fs.existsSync(logFile)) {
        console.log("Log File Already Exists")
    } else {
        fs.writeFileSync(logFile, "", { flag: "w" })
    }

    lc = new LamportClock()
}

function persist(rId, msg) {
    const logFile = `Replica_${rId}.log`

    lc.increment()
    const { lamportTime, physicalTime } = lc.getTimestamp()
    const logEntry = `${msg}, Lamport Time: ${lamportTime}, Physical Time: ${physicalTime.toISOString()}\n`

    fs.appendFile(logFile, logEntry, (err) => {
        if (err) console.error("Failed to log:", err)
    })
}

function execute(bytes) {
    const logMsg = LogMsg.deserializeBinary(bytes)

    const id = logMsg.getId()
    const logMessage = logMsg.getLogs()

    if (logMessage === "create") {
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

main().catch(err => {
    console.error('Error:', err.message)
    process.exit(1)
})