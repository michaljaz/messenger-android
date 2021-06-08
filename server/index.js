const express = require('express')
const app = express()
const http = require('http')
const server = http.createServer(app)
const { Server } = require('socket.io')
const io = new Server(server)
const port = process.env.PORT || 8080
const cors = require('cors')

app.use(cors())

app.get('/', (req, res) => {
  console.log(Date.now() + ' Ping Received')
  res.sendStatus(200)
})

io.on('connection', (socket) => {
  console.log('a user connected')
  socket.on('signin', (username, password) => {
    console.log('User signin')
    if (username === password) {
      socket.emit('signin_ok')
    }
  })
  socket.on('signup', (username, password) => {
    console.log('User signup')
    socket.emit('signup_ok')
  })
  socket.on('disconnect', () => {
    console.log('a user disconnected')
  })
})

server.listen(port, () => {
  console.log(`listening on *:${port}`)
})

setInterval(() => {
  http.get(`http://${process.env.PROJECT_DOMAIN}.glitch.me/`)
}, 280000)
