const net = require('net')

function establishConnections(addresses) {
    const connections = []

    addresses.forEach((address, index) => {
        console.log(`Establishing connection to ${address}`)

        const conn = net.createConnection({ host: address.split(':')[0], port: address.split(':')[1] }, () => {
            // console.log(`Connected to ${address}`)
        })

        conn.on('error', (err) => {
            console.error(`Error connecting to ${address}: ${err.message}`)
            process.exit(1)
        })

        connections.push(conn)
    })

    return connections
}

// Propagates Sync Reqs to Other Replicas
function broadcast(connections, content) {
    // Append custom delimiter 0x00 to the content
    content = Buffer.from([...content, 0x00]) 

    connections.forEach((conn, index) => {
        conn.write(content, (err) => {
            if (err) {
                console.error(`Error writing to socket ${index}: ${err.message}`)
                process.exit(1)
            }
        })
    })
}

module.exports = {establishConnections, broadcast}

// // Example usage
// const addresses = ['localhost:8080', 'localhost:9090'] 
// const connections = establishConnections(addresses)

// // Broadcast a message
// const message = Buffer.from('Hello, world!')
// broadcast(connections, message)
