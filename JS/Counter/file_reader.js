const fs = require('fs')

function readFile(fileName) {
    try {
        const data = fs.readFileSync(fileName, 'utf8')
        const lines = data.split(/\r?\n/)
        return lines
    } catch (err) {
        console.error('Error reading file:', err)
        process.exit(1)
    }
}

module.exports = {readFile}

// // Example usage
// const fileName = 'Actions1.txt';
// const lines = readFile(fileName);
// console.log(lines);
// console.log(lines.length)
