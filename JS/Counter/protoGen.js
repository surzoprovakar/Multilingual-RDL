const fs = require('fs')

function main() {
    fs.readFile('Plugin/logger.json', 'utf8', (err, data) => {
        if (err) {
            console.log(`Error reading logger.json: ${err}`)
            return
        }

        let config
        try {
            config = JSON.parse(data)
        } catch (parseErr) {
            console.log(`Error parsing logger.json: ${parseErr}`)
            return
        }

        generateProtobuf(config)
    })
}

function generateProtobuf(config) {
    let protoBuilder = ''

    protoBuilder += `syntax = "${config.syntax}";\n\n`
    protoBuilder += `package ${config.package};\n`
    protoBuilder += `option go_package = "${config.go_package}";\n\n`

    protoBuilder += `message ${config.message.name} {\n`
    let fieldNumber = 1
    for (const [name, fieldType] of Object.entries(config.message.fields)) {
        protoBuilder += `    ${fieldType} ${name} = ${fieldNumber};\n`
        fieldNumber++
    }
    protoBuilder += '}\n'

    const protoFilename = 'Plugin/logger.proto'
    fs.writeFile(protoFilename, protoBuilder, 'utf8', (writeErr) => {
        if (writeErr) {
            console.log(`Error writing protobuf file: ${writeErr}`)
            return
        }

        console.log(`Generated protobuf file: ${protoFilename}`)
    })
}

main()
