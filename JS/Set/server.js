const SetCRDT = require('./set')
const { readFile } = require('./file_reader')
const { establishConnections, broadcast } = require('./client')
const net = require('net')

const connType = 'tcp'
let hosts = []
let setReplica
let conns = []

// Function to execute actions
async function doActions(actions) {
    await new Promise(resolve => setTimeout(resolve, 5000)) // Sleep for 5 seconds
    console.log("Starting to doActions")

    for (const action of actions) {
        if (action.includes(":")) {
            var actionData = action.split(":")
            var opt = actionData[0]
            var val = parseInt(actionData[1])
            if (opt === "Add") {
                await setReplica.add(val)
                setReplica.print()
            } else if (opt === "Remove") {
                await setReplica.remove(val)
                setReplica.print()
            }
        } else if (action === "Broadcast") {
            console.log("Processing Broadcast")

            if (conns.length === 0) { // Establish connections on first broadcast
                conns = establishConnections(hosts)
            }

            console.log("About to broadcast Set:")
            setReplica.print()
            // console.log("broadcast content: ", setReplica.toMarshal())
            broadcast(conns, setReplica.toMarshal())
        } else { // Assume it is a delay
            const delay = parseInt(action, 10)
            if (isNaN(delay)) {
                throw new Error(`Invalid delay action: ${action}`)
            }
            await new Promise(resolve => setTimeout(resolve, delay * 1000))
        }
    }
}

// Function to handle a connection
function handleConnection(conn) {
    let message = Buffer.alloc(0)
    const delimiter = 0x00

    conn.on('data', (data) => {
        for (let i = 0; i < data.length; i++) {
            if (data[i] === delimiter) {
                processMessage(message)
                message = Buffer.alloc(0)
                return
            }
            message = Buffer.concat([message, Buffer.from([data[i]])])
        }
    })

    conn.on('end', () => {
        console.log('Client left.')
        conn.end()
    })


    function processMessage(message) {
        // console.log("message: ", message)
        const messageUint8Array = new Uint8Array(message)
        // console.log("messageUint8Array: ", messageUint8Array)
        const [rid, updates] = SetCRDT.fromMarshalData(messageUint8Array)
        // console.log("rid: ", rid)
        // console.log("updates: ", updates)
        setReplica.merge(rid, updates)
        handleConnection(conn)
    }
}

// Main function
async function main() {
    const args = process.argv.slice(2)

    if (args.length !== 4) {
        console.log("Usage: set_id ip_address crdt_socket_server Replicas'_Addresses.txt Actions.txt")
        process.exit(1)
    }

    const [idStr, ip_address, hostsFile, actionsFile] = args
    const id = parseInt(idStr, 10)

    if (isNaN(id)) {
        throw new Error('Invalid set ID')
    }

    // console.log("set id: ", id)
    setReplica = new SetCRDT(id)
    hosts = readFile(hostsFile)
    const actions = readFile(actionsFile)

    const server = net.createServer((conn) => {
        console.log(`Client ${conn.remoteAddress}:${conn.remotePort} connected.`)
        handleConnection(conn)
    })

    server.listen(ip_address.split(':')[1], ip_address.split(':')[0], () => {
        console.log(`Starting ${connType} server on ${ip_address}`)
    })

    await doActions(actions)
}

// Run the main function
main().catch(err => {
    console.error('Error:', err.message)
    process.exit(1)
})