const { LogMsg } = require('./Plugin/logger_pb.js')
const net = require("net")


class PluginManager {
    notify(id, msg) {
        let logMessage = new LogMsg()
        logMessage.setId(id)
        logMessage.setLogs(msg)

        let serialized = logMessage.serializeBinary()
        serialized = Buffer.from([...serialized, 0x00])

        this.propagateToLogger(serialized)
    }

    propagateToLogger(message) {
        const client = new net.Socket()
        client.connect(8080, 'localhost', () => {
            client.write(message, (err) => {
                if (err) {
                    console.log(`Failed to send message: ${err}`)
                }
            })
        })

        client.on('error', (err) => {
            console.log(`Failed to connect to logger: ${err}`)
        })

        client.on('close', () => {
            client.destroy()
        })
    }
}

module.exports = PluginManager
