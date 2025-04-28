const http = require('http')
const url = require('url')

// Sample ambient data with unique timestamps as keys
const ambientData = new Map([
    ['2024-08-20T10:00:00Z', { temperature: 30, humidity: 75 }],
    ['2024-08-20T11:00:00Z', { temperature: 22, humidity: 45 }],
    ['2024-08-20T12:00:00Z', { temperature: 15, humidity: 85 }],
    ['2024-08-20T13:00:00Z', { temperature: 10, humidity: 50 }],
    ['2024-08-20T14:00:00Z', { temperature: 35, humidity: 30 }],
])

// Function to generate weather icons based on temperature and humidity
function generateWeatherIcons(temp, humidity) {
    const temperatureIcon = temp >= 30 ? '🌞' : temp <= 15 ? '❄️' : '🌤️'
    const humidityIcon = humidity >= 70 ? '💧' : '🌵'
    return { temperatureIcon, humidityIcon }
}

// Function to generate HTML content for the page
function generateHTML() {
    let html = `
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Ambient Data Report</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background-color: #f4f4f4;
                    margin: 0;
                    padding: 20px;
                }
                .container {
                    max-width: 800px;
                    margin: 0 auto;
                }
                .card {
                    background-color: white;
                    border: 1px solid #ddd;
                    border-radius: 5px;
                    padding: 15px;
                    margin-bottom: 10px;
                    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                }
                .card h3 {
                    margin: 0 0 10px;
                }
                .card button {
                    background-color: #ff4d4d;
                    color: white;
                    border: none;
                    padding: 8px 12px;
                    border-radius: 5px;
                    cursor: pointer;
                }
                .card button:hover {
                    background-color: #e60000;
                }
            </style>
        </head>
        <body>
        <div class="container">
            <h1>Ambient Data Report</h1>`

    ambientData.forEach((values, timestamp) => {
        const { temperatureIcon, humidityIcon } = generateWeatherIcons(values.temperature, values.humidity)

        html += `
            <div class="card">
                <h3>Timestamp: ${timestamp}</h3>
                <p>Temperature: ${values.temperature}°C ${temperatureIcon}</p>
                <p>Humidity: ${values.humidity}% ${humidityIcon}</p>
                <form method="GET" action="/delete">
                    <input type="hidden" name="timestamp" value="${timestamp}">
                    <button type="submit">Delete</button>
                </form>
            </div>`
    })

    html += `
            </div>
        </body>
        </html>`

    return html
}

// Create the HTTP server
const server = http.createServer((req, res) => {
    const queryObject = url.parse(req.url, true).query
    const pathname = url.parse(req.url, true).pathname

    if (pathname === '/delete') {
        const timestamp = queryObject.timestamp
        if (ambientData.has(timestamp)) {
            ambientData.delete(timestamp)
        }
        // Redirect back to the main page
        res.writeHead(302, { Location: '/' })
        res.end()
    } else {
        // Serve the HTML page with UTF-8 encoding
        res.writeHead(200, { 'Content-Type': 'text/html; charset=UTF-8' })
        res.end(generateHTML())
    }
})

// Server listening on port 3000
server.listen(3000, () => {
    console.log('Server running at http://localhost:3000/')
})
